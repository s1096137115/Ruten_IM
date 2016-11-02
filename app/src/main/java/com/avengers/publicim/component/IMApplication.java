package com.avengers.publicim.component;

import android.app.Application;
import android.content.Context;

import com.avengers.publicim.view.DialogBuilder;
import com.avengers.publicim.view.IMProgressDialog;

/**
 * Created by D-IT-MAX2 on 2016/3/1.
 */
public class IMApplication extends Application {
	private static Context mContext;
	private static IMProgressDialog mProgressDialog;
	private static DialogBuilder mBuilder;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
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
