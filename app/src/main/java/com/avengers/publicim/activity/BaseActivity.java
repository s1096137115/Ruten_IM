package com.avengers.publicim.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.avengers.publicim.conponent.DbHelper;
import com.avengers.publicim.conponent.IMService;
import com.avengers.publicim.conponent.IMService.IMBinder;

/**
 * Created by D-IT-MAX2 on 2016/3/1.
 */
public abstract class BaseActivity extends AppCompatActivity {
	protected IMService mIMService;
	protected boolean mIsBind = false;
	protected DbHelper mDB;

	@Override
	public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
		super.onCreate(savedInstanceState, persistentState);
		mDB = DbHelper.getInstance(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(mIsBind){
			registerListeners();
			onBackendConnected();
		}else{
			Intent intent = new Intent(this, IMService.class);
			startService(intent);
			bindService(intent,mSC,BIND_AUTO_CREATE);
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		if(mIsBind){
			unbindService(mSC);
			mIsBind =false;
		}
	}

	public void registerListeners(){

	}

	public void unregisterListeners(){

	}

	protected void onBackendConnected() {
	}

	private ServiceConnection mSC = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			IMBinder binder = (IMBinder)service;
			mIMService = binder.getService();
			mIsBind = true;
			registerListeners();
			onBackendConnected();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mIsBind = false;
			unregisterListeners();
		}
	};
}
