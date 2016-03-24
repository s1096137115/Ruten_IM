package com.avengers.publicim.conponent;

import android.app.Application;
import android.content.Context;

import com.avengers.publicim.data.Constants;
import com.avengers.publicim.data.entities.RosterManager;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by D-IT-MAX2 on 2016/3/1.
 */
public class IMApplication extends Application {
	private static Context mContext;
	private Socket mSocket;
	private static RosterManager mRosterManager;
	private DbHelper mDB;

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			mContext = getApplicationContext();
			mDB = DbHelper.getInstance(this);
			mRosterManager = new RosterManager();
			mRosterManager.setEntries(mDB.getLocalRoster());
			IO.Options opts = new IO.Options();
//			opts.forceNew = true;
			opts.reconnection = false;
			mSocket = IO.socket(Constants.CHAT_SERVER_URL,opts);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	public Socket getSocket() {
		return mSocket;
	}

	public static RosterManager getRoster(){
		return mRosterManager;
	}

	public static Context getContext() {
		return mContext;
	}
}
