package com.avengers.publicim.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.avengers.publicim.R;
import com.avengers.publicim.adapter.ChatAdapter;
import com.avengers.publicim.conponent.DbHelper;
import com.avengers.publicim.conponent.IMApplication;
import com.avengers.publicim.data.entities.Chat;
import com.avengers.publicim.data.entities.Message;
import com.avengers.publicim.data.entities.RosterEntry;
import com.avengers.publicim.data.listener.MessageListener;
import com.avengers.publicim.utils.SystemUtils;

import static com.avengers.publicim.conponent.IMApplication.getChatManager;
import static com.avengers.publicim.conponent.IMApplication.getRosterManager;

public class ChatActivity extends BaseActivity implements MessageListener{
	public static final String ROSTER_NAME = "roster_name";
	public static final String ROSTER = "roster";

	private RecyclerView mRecyclerView;
	private ChatAdapter mChatAdapter;
	private RosterEntry mEntry;
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
		mChatAdapter = new ChatAdapter(ChatActivity.this, mDB.getMessages(mEntry));
		setRecyclerView();

		mSendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Message message = new Message(null, IMApplication.getUser(), mEntry.getUser(),
						Message.Type.TEXT, mTextInput.getText().toString(), SystemUtils.getDateTime(),
						mEntry.getUser().getName(), DbHelper.IntBoolean.TRUE);

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
			String name = bundle.getString(ROSTER_NAME);
			if(getRosterManager().contains(name)){
				mEntry = getRosterManager().getEntry(name);
			}else{
				return;
			}

			if(getChatManager().contains(name)){
				mChat = getChatManager().getChat(name);
			}else{
				mChat = new Chat(mEntry.getUser().getName(),mEntry.getUser().getName(),
						SystemUtils.getDateTime());
			}
		}
	}

	public void setToolbar(){
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		getSupportActionBar().setHomeButtonEnabled(true);
		if(mEntry != null){
			getSupportActionBar().setTitle(mEntry.getUser().getName());
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
	public void onMessageUpdate() {
		mChatAdapter.update(mDB.getMessages(mEntry));
		mChatAdapter.refresh();
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mRecyclerView.scrollToPosition(mChatAdapter.getItemCount()-1);
			}
		});
		Log.d("text", "onMessageUpdate: ");
	}
}
