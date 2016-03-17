package com.avengers.publicim.conponent;

import android.app.Application;
import android.content.Context;

import com.avengers.publicim.data.Constants;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by D-IT-MAX2 on 2016/3/1.
 */
public class RutenApplication extends Application {
	private static Context mContext;
	private Socket mSocket;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		try {
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

	public static Context getContext() {
		return mContext.getApplicationContext();
	}
}
