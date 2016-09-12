package com.avengers.publicim.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.avengers.publicim.R;
import com.avengers.publicim.adapter.ChatAdapter;
import com.avengers.publicim.conponent.IMApplication;
import com.avengers.publicim.data.entities.Contact;
import com.avengers.publicim.data.entities.Message;
import com.avengers.publicim.data.entities.Room;
import com.avengers.publicim.data.event.ServiceEvent;
import com.avengers.publicim.data.listener.MessageListener;
import com.avengers.publicim.data.listener.RoomListener;
import com.avengers.publicim.utils.SystemUtils;

import java.util.List;

import static com.avengers.publicim.conponent.IMApplication.getProgress;
import static com.avengers.publicim.conponent.IMApplication.getRoomManager;

public class ChatActivity extends BaseActivity implements MessageListener, RoomListener{
	private RecyclerView mRecyclerView;
	private ChatAdapter mChatAdapter;
	private Room mRoom;
	private Contact mContact;

	private ImageButton mSendButton;
	private EditText mTextInput;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		mSendButton = (ImageButton) findViewById(R.id.textSendButton);
		mTextInput = (EditText) findViewById(R.id.textInput);
		getData();
		setToolbar();
		mChatAdapter = new ChatAdapter(ChatActivity.this, mDB.getMessages(mContact), mRoom);
		setRecyclerView();

		mSendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Message message = new Message(mContact.getRid(), Message.Type.TEXT, mTextInput.getText().toString());
				mIMService.sendMessage(message);

				//adjust UI
				mTextInput.getText().clear();
				SystemUtils.hideVirtualKeyboard(ChatActivity.this);
			}
		});
	}

	public void getData(){
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mContact = (Contact)bundle.getSerializable(Contact.Type.CONTACT);
			mRoom = getRoomManager().getItem(Room.Type.ALL ,mContact.getRid());
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
		mRecyclerView.setAdapter(mChatAdapter);
		mRecyclerView.scrollToPosition(mChatAdapter.getItemCount()-1);
	}

	@Override
	protected void onBackendConnected() {
		super.onBackendConnected();
		//hasUnread
		if(mRoom.getUnread() > 0){
			mIMService.sendMessageRead(mContact.getRid(), System.currentTimeMillis());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_chat, menu);
		final MenuItem inviteGroup = menu.findItem(R.id.action_invite_group);
		final MenuItem exitGroup = menu.findItem(R.id.action_exit_group);
		if(mContact instanceof Room){
			inviteGroup.setVisible(true);
			exitGroup.setVisible(true);
		}else{
			inviteGroup.setVisible(false);
			exitGroup.setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		Intent intent;
		switch (id){
			case R.id.action_view_member:
				intent = new Intent(this, MemberActivity.class);
				intent.putExtra(Contact.Type.CONTACT, mContact);
				startActivity(intent);
				break;
			case R.id.action_invite_group:
				intent = new Intent(this, InviteActivity.class);
				intent.putExtra(Contact.Type.CONTACT, mContact);
				startActivity(intent);
				break;
			case R.id.action_exit_group:
				getProgress().setMessage("Waiting...");
				getProgress().show();
				mIMService.sendSetRoomMemberRole(mRoom, IMApplication.getUser(), Room.Role.EXIT);
				break;
		}
		return true;
	}


	public void onMessageUpdate() {
//		mIMService.updateMessageOfRead((Room)mContact, Message.Read.TRUE);
		mIMService.sendMessageRead(mContact.getRid(), System.currentTimeMillis());
		mChatAdapter.update(mDB.getMessages(mContact));
		mChatAdapter.refresh();
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mRecyclerView.scrollToPosition(mChatAdapter.getItemCount()-1);
			}
		});
		Log.d("text", "onMessageUpdate: ");
	}

	@Override
	public void onServeiceResponse(ServiceEvent event) {
		if(this == event.getListener()){
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
	}

	@Override
	public String getName() {
		return "ChatActivity";
	}

	@Override
	public void onMessageAddition(Message message) {
		if(!message.getRid().equals(mRoom.getRid())) return;
		mIMService.sendMessageRead(mContact.getRid(), System.currentTimeMillis());
		mChatAdapter.add(message);
		mChatAdapter.refresh();
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mRecyclerView.scrollToPosition(mChatAdapter.getItemCount()-1);
			}
		});
	}

	@Override
	public void onMessagesAddition(List<Message> list) {
		boolean update = false;
		for (Message message: list) {
			if(message.getRid().equals(mRoom.getRid())){
				mChatAdapter.add(message);
				update = true;
			}
		}
		if(!update) return; //如果沒有新增到該房間的資料就return
		mIMService.sendMessageRead(mContact.getRid(), System.currentTimeMillis());
		mChatAdapter.refresh();
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mRecyclerView.scrollToPosition(mChatAdapter.getItemCount()-1);
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
		switch (event.getEvent()) {
			case ServiceEvent.Event.GET_ROOM:
				mRoom = getRoomManager().getItem(Room.Type.ALL ,mContact.getRid());
				if(mRoom != null) {
					mChatAdapter.update(mRoom);
					mChatAdapter.refresh();
				}else{
					finish();
				}
				break;
		}
	}
}
