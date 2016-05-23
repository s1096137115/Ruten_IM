package com.avengers.publicim.conponent;

import android.app.Application;
import android.content.Context;

import com.avengers.publicim.data.Constants;
import com.avengers.publicim.data.Manager.ChatManager;
import com.avengers.publicim.data.Manager.MessageManager;
import com.avengers.publicim.data.Manager.RosterManager;
import com.avengers.publicim.data.entities.User;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by D-IT-MAX2 on 2016/3/1.
 */
public class IMApplication extends Application {
	private static Context mContext;
	private Socket mSocket;
	private static RosterManager mRosterManager;
	private static ChatManager mChatManager;
	private static MessageManager mMessageManager;
	private DbHelper mDB;
	private static User mUser;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		mDB = DbHelper.getInstance(this);
		initSocket();
		initManager();
		initUser();
	}

	private void initUser(){
		mUser = new User("Android-2234", "dog");
	}

	public static User getUser(){
		return mUser;
	}

	private void initSocket(){
		try {
			IO.Options opts = new IO.Options();
//			opts.forceNew = true;
//			opts.reconnection = false;
			mSocket = IO.socket(Constants.CHAT_SERVER_URL,opts);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void initManager(){
		mRosterManager = new RosterManager();
		mRosterManager.setEntries(mDB.getLocalRoster());
		mChatManager = new ChatManager();
		mChatManager.setChats(mDB.getContentOfChats());
		mMessageManager = new MessageManager();
	}


	public Socket getSocket() {
		return mSocket;
	}

	public static RosterManager getRosterManager(){
		return mRosterManager;
	}

	public static ChatManager getChatManager(){
		return mChatManager;
	}

	public static MessageManager getMessageManager(){
		return mMessageManager;
	}

	public static Context getContext() {
		return mContext;
	}
}
