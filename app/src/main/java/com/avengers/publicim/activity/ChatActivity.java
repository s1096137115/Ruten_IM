package com.avengers.publicim.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.avengers.publicim.R;
import com.avengers.publicim.adapter.ChatAdapter;
import com.avengers.publicim.data.action.GetMessage;
import com.avengers.publicim.data.entities.Contact;
import com.avengers.publicim.data.entities.Member;
import com.avengers.publicim.data.entities.Message;
import com.avengers.publicim.data.entities.Option;
import com.avengers.publicim.data.entities.Room;
import com.avengers.publicim.data.event.ServiceEvent;
import com.avengers.publicim.data.listener.MessageListener;
import com.avengers.publicim.data.listener.RoomListener;
import com.avengers.publicim.utils.OnRcvScrollListener;
import com.avengers.publicim.utils.OnRcvScrollListener.Position;
import com.avengers.publicim.utils.SystemUtils;
import com.avengers.publicim.view.LoginAccount;

import java.util.List;

import static com.avengers.publicim.utils.OnRcvScrollListener.Position.BOTTOM;

public class ChatActivity extends BaseActivity implements MessageListener, RoomListener{
	public static final int REQUEST_INVITE_MEMBER = 0;
	public static final int REQUEST_VIEW_MEMBER = 1;

	private RecyclerView mRecyclerView;
	private ChatAdapter mChatAdapter;
	private Room mRoom;
	private Contact mContact;

	private ImageButton mSendButton;
	private EditText mTextInput;
	private Position mPosition = BOTTOM;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//todo resume重撈db
		//todo getMessage設size
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		mSendButton = (ImageButton) findViewById(R.id.textSendButton);
		mTextInput = (EditText) findViewById(R.id.textInput);
		getData();
		setToolbar();
		mChatAdapter = new ChatAdapter(ChatActivity.this, mDB.getMessages(mContact), mRoom);
		setRecyclerView();
		mSendButton.setOnClickListener(onClick);
	}

	public void getData(){
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mContact = (Contact)bundle.getSerializable(Contact.Type.CONTACT);
			mRoom = mRoomManager.getItem(Room.Type.ALL ,mContact.getRid());
		}
	}

	public void setToolbar(){
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		getSupportActionBar().setHomeButtonEnabled(true);
		if(mContact != null){
			getSupportActionBar().setTitle(mContact.getName());
		}
	}

	public void setRecyclerView(){
		mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mRecyclerView.setAdapter(mChatAdapter);
		mRecyclerView.addOnScrollListener(onRcvScroll);
		onRcvScroll.setOnPositionListener(onPosition);
		mRecyclerView.addOnLayoutChangeListener(onChange);
		mRecyclerView.scrollToPosition(mChatAdapter.positionOfUnread());
	}

	@Override
	protected void onBackendConnected() {
		super.onBackendConnected();
		//hasUnread
		Member myself = null;
		for (Member member: mRoom.getMembers()) {
			if(member.getUser().equals(LoginAccount.getInstance().getUser().getName())){
				myself = member;
				break;
			}
		}
		if(mChatAdapter.getData().isEmpty()) {
			mIMService.sendGetMessage(System.currentTimeMillis(), mContact.getRid(), GetMessage.Type.ABOVE);
			return;
		}
		int last = mChatAdapter.getData().size()-1;
		//自己的已讀時間<最後一句時才送出已讀
		if(myself.getRead_time() < mChatAdapter.getData().get(last).getDate()){
			mIMService.sendMessageRead(mContact.getRid(), System.currentTimeMillis());
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_chat, menu);
		final MenuItem inviteGroup = menu.findItem(R.id.action_invite_member);
		final MenuItem exitGroup = menu.findItem(R.id.action_exit_group);
		if(!mRoom.getType().equals(Room.Type.SINGLE)){
			inviteGroup.setVisible(true);
			exitGroup.setVisible(true);
		}else{
			inviteGroup.setVisible(false);
			exitGroup.setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(mRoom.getOption() != null && mRoom.getOption().getMute() != null){
			boolean checked = mRoom.getOption().getMute() == Option.Mute.TURN_ON;
			menu.findItem(R.id.action_mute).setChecked(checked);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		Intent intent;
		switch (id){
			case R.id.action_view_member:
				intent = new Intent(this, ViewMemberActivity.class);
				intent.putExtra(Contact.Type.CONTACT, mContact);
				startActivityForResult(intent, REQUEST_VIEW_MEMBER);
				break;
			case R.id.action_mute:
				int value = mRoom.getOption().getMute() == Option.Mute.TURN_ON ?
						Option.Mute.TURN_OFF : Option.Mute.TURN_ON;
				mIMService.sendSetRoomOption(mContact.getRid(), value);
				break;
			case R.id.action_invite_member:
				intent = new Intent(this, InviteMemberActivity.class);
				intent.putExtra(Contact.Type.CONTACT, mContact);
				intent.putExtra(InviteMemberActivity.REQUEST_CODE, InviteMemberActivity.INVITE);
				startActivityForResult(intent, REQUEST_INVITE_MEMBER);
				break;
			case R.id.action_exit_group:
				getProgress().setMessage("Waiting...");
				getProgress().show();
				mIMService.sendSetRoomMemberRole(mRoom, LoginAccount.getInstance().getUser(), Room.Role.EXIT);
				break;
		}
		return true;
	}

	@Override
	public void onServeiceResponse(ServiceEvent event) {
		switch (event.getEvent()){
			case ServiceEvent.Event.CLOSE_DIALOG:
				getProgress().dismiss();
				finish();
				break;
			case ServiceEvent.Event.CLOSE_ACTIVITY:
				String rid = event.getBundle().getString(Room.RID);
				assert rid != null;
				if(rid.equals(mContact.getRid())) finish();
		}
	}

	@Override
	public String getName() {
		return "ChatActivity";
	}

	@Override
	public void onMessageAddition(final Message message) {
		if(!message.getRid().equals(mRoom.getRid())) return;
		mIMService.sendMessageRead(mContact.getRid(), System.currentTimeMillis());
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mChatAdapter.add(message);
				mChatAdapter.notifyItemInserted(mChatAdapter.getItemCount()-1);
				if(mPosition == BOTTOM){
					mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount()-1);
				}else{
					Toast.makeText(ChatActivity.this, message.getContext(), Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public void onMessagesAddition(List<Message> list, String type) {
		boolean update = false;
		for (Message message: list) {
			if(message.getRid().equals(mRoom.getRid())){
				mChatAdapter.add(message);
				update = true;
			}
		}
		if(!update) return; //如果沒有新增到該房間的資料就return
		mIMService.sendMessageRead(mContact.getRid(), System.currentTimeMillis());
		mChatAdapter.refresh(1000);
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount()-1);
			}
		});
	}

	@Override
	public void onMessageUpdate(Message message) {

	}

	@Override
	public void onMessagesUpdate(List<Message> list) {

	}

	@Override
	public void onRoomUpdate(ServiceEvent event) {
		String rid;
		switch (event.getEvent()) {
			case ServiceEvent.Event.GET_ROOM:
				rid = event.getBundle().getString(Room.RID);
				if(rid.equals(mContact.getRid())){
					mRoom = mRoomManager.getItem(Room.Type.ALL ,mContact.getRid());
					if(mRoom != null) {
						mChatAdapter.update(mRoom);
						mChatAdapter.refresh(1000);
					}else{
						finish();
					}
				}
				break;
			case ServiceEvent.Event.LOAD_MESSAGE:
				rid = event.getBundle().getString(Room.RID);
				if(rid.equals(mContact.getRid())){
					mChatAdapter.setFullLoad(false);
					mChatAdapter.loadUp(mDB.getMessages(mContact));
					mChatAdapter.refresh(1000);
				}
				break;
		}
	}

	private View.OnClickListener onClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Message message = new Message(mContact.getRid(), Message.Type.TEXT, mTextInput.getText().toString());
			mIMService.sendMessage(message);

			//adjust UI
			mTextInput.getText().clear();
			SystemUtils.hideVirtualKeyboard(ChatActivity.this);
		}
	};

	private OnRcvScrollListener onRcvScroll = new OnRcvScrollListener(){
		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);
		}

		@Override
		public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
			super.onScrollStateChanged(recyclerView, newState);
		}
	};

	private View.OnLayoutChangeListener onChange = new View.OnLayoutChangeListener() {
		@Override
		public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
			if(mPosition == BOTTOM && bottom < oldBottom){
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						mRecyclerView.scrollToPosition(mChatAdapter.getItemCount()-1);
					}
				}, 10);
			}
		}
	};

	private OnRcvScrollListener.OnPositionListener onPosition = new OnRcvScrollListener.OnPositionListener() {
		@Override
		public void onPosition(Position position) {
			switch (position){
				case TOP:
					break;
				case BOTTOM:
					break;
				case OTHER:
					break;
			}
			mPosition = position;
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQUEST_VIEW_MEMBER:
			case REQUEST_INVITE_MEMBER:
				if(data != null) {
					if(mRoom != null) {
						mChatAdapter.update(mDB.getMessages(mContact));
						mChatAdapter.refresh(1000);
					}else{
						finish();
					}
				}
				break;
		}
	}
}
