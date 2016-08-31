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
import com.avengers.publicim.conponent.DbHelper;
import com.avengers.publicim.conponent.IMApplication;
import com.avengers.publicim.data.callback.MessageListener;
import com.avengers.publicim.data.callback.ServiceEvent;
import com.avengers.publicim.data.entities.Contact;
import com.avengers.publicim.data.entities.Room;
import com.avengers.publicim.data.entities.RosterEntry;
import com.avengers.publicim.utils.SystemUtils;

import static com.avengers.publicim.conponent.IMApplication.getProgress;
import static com.avengers.publicim.conponent.IMApplication.getRoomManager;

public class ChatActivity extends BaseActivity implements MessageListener{
	private RecyclerView mRecyclerView;
	private ChatAdapter mChatAdapter;
	private RosterEntry mEntry;
//	private Room mRoom;
	private Contact mContact;
//	private Chat mChat;

	private ImageButton mSendButton;
	private EditText mTextInput;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		mSendButton = (ImageButton) findViewById(R.id.textSendButton);
		mTextInput = (EditText) findViewById(R.id.textInput);
		getContact();
		setToolbar();
		mChatAdapter = new ChatAdapter(ChatActivity.this, mDB.getMessages(mContact));
		setRecyclerView();

		mSendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Message message = null;
//				if(mContact instanceof RosterEntry){
//					message = new Message(null, IMApplication.getUser(), ((RosterEntry)mContact).getUser(), "",
//							Message.Type.TEXT, mTextInput.getText().toString(), SystemUtils.getDateTime(),
//							((RosterEntry)mContact).getUser().getName(), DbHelper.IntBoolean.TRUE);
//				}else if(mContact instanceof Room){
//					message = new Message(null, IMApplication.getUser(), User.newInstance("",""), ((Room)mContact).getRid(),
//							Message.Type.TEXT, mTextInput.getText().toString(), SystemUtils.getDateTime(),
//							((Room)mContact).getRid(), DbHelper.IntBoolean.TRUE);
//				}
//				if(message != null){
//					mIMService.sendMessage(message);
//				}

				//adjust UI
				mTextInput.getText().clear();
				SystemUtils.hideVirtualKeyboard(ChatActivity.this);
			}
		});
	}

	public void getContact(){
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mContact = (Contact)bundle.getSerializable(Contact.Type.CONTACT);
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
		if(mChatAdapter.hasUnread()){
				mIMService.updateMessageOfRead((Room)mContact, DbHelper.IntBoolean.TRUE);
			getRoomManager().reload();
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
				intent.putExtra(Room.RID, mContact.getRid());
				startActivity(intent);
				break;
			case R.id.action_invite_group:
				intent = new Intent(this, InviteActivity.class);
				intent.putExtra(Room.RID, mContact.getRid());
				startActivity(intent);
				break;
			case R.id.action_exit_group:
				getProgress().setMessage("Waiting...");
				getProgress().show();
				mIMService.sendSetRoomMemberRole((Room) mContact, IMApplication.getUser(), Room.Role.EXIT);
				break;
		}
		return true;
	}

	@Override
	public void onMessageUpdate() {
		mIMService.updateMessageOfRead((Room)mContact, DbHelper.IntBoolean.TRUE);
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
		if(this == event.toListener()){
			switch (event.getEvent()){
				case ServiceEvent.EVENT_CLOSE_DIALOG:
					getProgress().dismiss();
					finish();
					break;
			}
		}
	}
}
