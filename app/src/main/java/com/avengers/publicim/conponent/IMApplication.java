package com.avengers.publicim.conponent;

import android.app.Application;
import android.content.Context;

import com.avengers.publicim.data.Constants;
import com.avengers.publicim.data.Manager.ChatManager;
import com.avengers.publicim.data.Manager.GroupManager;
import com.avengers.publicim.data.Manager.MessageManager;
import com.avengers.publicim.data.Manager.RosterManager;
import com.avengers.publicim.data.entities.Presence;
import com.avengers.publicim.data.entities.RosterEntry;
import com.avengers.publicim.data.entities.User;
import com.avengers.publicim.view.DialogBuilder;
import com.avengers.publicim.view.IMProgressDialog;

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
	private static GroupManager mGroupManager;
	private static MessageManager mMessageManager;
	private static IMProgressDialog mProgressDialog;
	private static DialogBuilder mBuilder;
	private DbHelper mDB;
	private static RosterEntry mEntry;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		mDB = DbHelper.getInstance(this);
		initSocket();
		initManager();
		initAccount();
	}

	private void initAccount(){
		mEntry = new RosterEntry(new User("Android-0704-1503", "ac02"),
				new Presence("","",Presence.STATUS_ONLINE), 0);
	}

	public static User getUser(){
		return mEntry.getUser();
	}

	public static Presence getPresence(){
		return mEntry.getPresence();
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
		mRosterManager = new RosterManager(mContext);
		mRosterManager.reload();
		mChatManager = new ChatManager(mContext);
		mChatManager.reload();
		mGroupManager = new GroupManager(mContext);
		mGroupManager.reload();
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

	public static GroupManager getGroupManager(){
		return mGroupManager;
	}

	public static MessageManager getMessageManager(){
		return mMessageManager;
	}

	public static Context getContext() {
		return mContext;
	}

	public static DialogBuilder getBuilder(){
		return mBuilder;
	}

	public static void setBuilder(DialogBuilder builder){
		mBuilder = builder;
	}

	public static void setIMProgress(IMProgressDialog progress){
		mProgressDialog = progress;
	}

	public static IMProgressDialog getProgress(){
		return mProgressDialog;
	}
}
