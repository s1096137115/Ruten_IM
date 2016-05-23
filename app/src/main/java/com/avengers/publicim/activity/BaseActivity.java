package com.avengers.publicim.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.avengers.publicim.conponent.DbHelper;
import com.avengers.publicim.conponent.IMService;
import com.avengers.publicim.conponent.IMService.IMBinder;
import com.avengers.publicim.data.listener.ChatListener;
import com.avengers.publicim.data.listener.MessageListener;
import com.avengers.publicim.data.listener.RosterListener;

import static com.avengers.publicim.conponent.IMApplication.getChatManager;
import static com.avengers.publicim.conponent.IMApplication.getMessageManager;
import static com.avengers.publicim.conponent.IMApplication.getRosterManager;

/**
 * Created by D-IT-MAX2 on 2016/3/1.
 */
public abstract class BaseActivity extends AppCompatActivity {
	protected IMService mIMService;
	protected boolean mIsBind = false;
	protected DbHelper mDB;
	protected Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
			unregisterListeners();
			mIsBind =false;
		}
	}

	public IMService getIMService(){
		return mIMService;
	}

	public void registerListeners(){
		if (this instanceof ChatListener) {
			getChatManager().addChatListener((ChatListener) this);
		}
		if (this instanceof RosterListener) {
			getRosterManager().addRosterListener((RosterListener) this);
		}
		if (this instanceof MessageListener) {
			getMessageManager().addMessageListener((MessageListener) this);
		}
	}

	public void unregisterListeners(){
		if (this instanceof ChatListener) {
			getChatManager().removeChatListener((ChatListener) this);
		}
		if (this instanceof RosterListener) {
			getRosterManager().removeRosterListener((RosterListener) this);
		}
		if (this instanceof MessageListener) {
			getMessageManager().removeMessageListener((MessageListener) this);
		}
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
