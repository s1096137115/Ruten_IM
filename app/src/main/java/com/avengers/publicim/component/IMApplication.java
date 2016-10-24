package com.avengers.publicim.component;

import android.app.Application;
import android.content.Context;

import com.avengers.publicim.data.Manager.MessageManager;
import com.avengers.publicim.data.Manager.RoomManager;
import com.avengers.publicim.data.Manager.RosterManager;
import com.avengers.publicim.data.entities.Presence;
import com.avengers.publicim.data.entities.RosterEntry;
import com.avengers.publicim.data.entities.User;
import com.avengers.publicim.view.DialogBuilder;
import com.avengers.publicim.view.IMProgressDialog;

import io.socket.client.Socket;

/**
 * Created by D-IT-MAX2 on 2016/3/1.
 */
public class IMApplication extends Application {
	private static Context mContext;
	private Socket mSocket;
	private static RosterManager mRosterManager;
	private static RoomManager mRoomManager;
	private static MessageManager mMessageManager;
	private static IMProgressDialog mProgressDialog;
	private static DialogBuilder mBuilder;
	private static RosterEntry mEntry;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
//		initAccount();
	}

	private void initAccount(){
		mEntry = new RosterEntry(new User("Android-Emulator", "test04"),
				new Presence("","", Presence.Status.ONLINE), 0, "");
	}

	public static void setAccount(RosterEntry entry){
		mEntry = entry;
	}

	public static User getUser(){
		return mEntry != null ? mEntry.getUser() : null;
	}

	public static Presence getPresence(){
		return mEntry.getPresence();
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
