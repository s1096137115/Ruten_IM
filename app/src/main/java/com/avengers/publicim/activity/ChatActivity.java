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
import com.avengers.publicim.data.entities.Chat;
import com.avengers.publicim.data.entities.Contact;
import com.avengers.publicim.data.entities.Group;
import com.avengers.publicim.data.entities.Message;
import com.avengers.publicim.data.entities.RosterEntry;
import com.avengers.publicim.data.entities.User;
import com.avengers.publicim.utils.SystemUtils;

import static com.avengers.publicim.conponent.IMApplication.getChatManager;
import static com.avengers.publicim.conponent.IMApplication.getGroupManager;
import static com.avengers.publicim.conponent.IMApplication.getProgress;
import static com.avengers.publicim.conponent.IMApplication.getRosterManager;

public class ChatActivity extends BaseActivity implements MessageListener{
	public static final String ROSTER_NAME = "roster_name";
	public static final String GROUP_ID = "gid";
	public static final String ROSTER = "roster";

	private RecyclerView mRecyclerView;
	private ChatAdapter mChatAdapter;
	private RosterEntry mEntry;
	private Group mGroup;
	private Contact mContact;
	private Chat mChat;

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
		mChatAdapter = new ChatAdapter(ChatActivity.this, mDB.getMessages(mContact));
		setRecyclerView();

		mSendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Message message = null;
				if(mContact instanceof RosterEntry){
					message = new Message(null, IMApplication.getUser(), ((RosterEntry)mContact).getUser(), "",
							Message.Type.TEXT, mTextInput.getText().toString(), SystemUtils.getDateTime(),
							((RosterEntry)mContact).getUser().getName(), DbHelper.IntBoolean.TRUE);
				}else if(mContact instanceof Group){
					message = new Message(null, IMApplication.getUser(), User.newInstance("",""), ((Group)mContact).getGid(),
							Message.Type.TEXT, mTextInput.getText().toString(), SystemUtils.getDateTime(),
							((Group)mContact).getGid(), DbHelper.IntBoolean.TRUE);
				}
				if(message != null){
					mIMService.sendMessage(message);
				}

				//adjust UI
				mTextInput.getText().clear();
				SystemUtils.hideVirtualKeyboard(ChatActivity.this);
			}
		});
	}

	public void getData(){
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			String value = "";
			if(bundle.getString(ROSTER_NAME) != null){
				value = bundle.getString(ROSTER_NAME);
				if(getRosterManager().contains(value)){
					mContact = getRosterManager().getItem(value);
				}
			}else if(bundle.getString(GROUP_ID) != null){
				value = bundle.getString(GROUP_ID);
				if(getGroupManager().contains(value)){
					mContact = getGroupManager().getItem(value);
					((Group)mContact).setMembers(mDB.getMembers((Group)mContact));
				}
			}

			if(getChatManager().contains(value)){
				mChat = getChatManager().getItem(value);
			}else{
				if(mContact instanceof RosterEntry){
					mChat = new Chat(mContact.getName(),mContact.getName(), Chat.TYPE_ROSTER);
				}else{
					mChat = new Chat(mContact.getId(),mContact.getName(), Chat.TYPE_GROUP);
				}
			}
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
		if(mChat == null) return;
		//有chat & 未讀訊息的情況下才更新chat
		if(getChatManager().contains(mChat.getCid())){
			if(mChatAdapter.hasUnread()){
				mIMService.updateMessageOfRead(mChat, DbHelper.IntBoolean.TRUE);
				getChatManager().reload();
			}
		}else{
			mIMService.addChat(mChat);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_chat, menu);
		final MenuItem inviteGroup = menu.findItem(R.id.action_invite_group);
		final MenuItem exitGroup = menu.findItem(R.id.action_exit_group);
		if(mContact instanceof Group){
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
				intent.putExtra(ChatActivity.GROUP_ID, mContact.getId());
				startActivity(intent);
				break;
			case R.id.action_invite_group:
				intent = new Intent(this, InviteActivity.class);
				intent.putExtra(ChatActivity.GROUP_ID, mContact.getId());
				startActivity(intent);
				break;
			case R.id.action_exit_group:
				getProgress().setMessage("Waiting...");
				getProgress().show();
				mIMService.sendSetGroupMemberRole((Group) mContact, Group.ROLE_EXIT);
				break;
		}
		return true;
	}

	@Override
	public void onMessageUpdate() {
		mIMService.updateMessageOfRead(mChat, DbHelper.IntBoolean.TRUE);
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
