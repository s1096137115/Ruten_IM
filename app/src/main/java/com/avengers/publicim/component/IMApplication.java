package com.avengers.publicim.component;

import android.app.Application;
import android.content.Context;

/**
 * Created by D-IT-MAX2 on 2016/3/1.
 */
public class IMApplication extends Application {
	private static Context mContext;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
	}

	public static Context getContext() {
		return mContext;
	}
}
