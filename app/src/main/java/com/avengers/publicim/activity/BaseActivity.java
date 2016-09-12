package com.avengers.publicim.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.avengers.publicim.conponent.DbHelper;
import com.avengers.publicim.conponent.IMService;
import com.avengers.publicim.conponent.IMService.IMBinder;
import com.avengers.publicim.data.listener.MessageListener;
import com.avengers.publicim.data.listener.RoomListener;
import com.avengers.publicim.data.listener.RosterListener;
import com.avengers.publicim.data.listener.ServiceListener;
import com.avengers.publicim.fragment.BaseFragment;
import com.avengers.publicim.view.DialogBuilder;
import com.avengers.publicim.view.IMProgressDialog;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.avengers.publicim.conponent.IMApplication.getMessageManager;
import static com.avengers.publicim.conponent.IMApplication.getRoomManager;
import static com.avengers.publicim.conponent.IMApplication.getRosterManager;
import static com.avengers.publicim.conponent.IMApplication.setBuilder;
import static com.avengers.publicim.conponent.IMApplication.setIMProgress;

/**
 * Created by D-IT-MAX2 on 2016/3/1.
 */
public abstract class BaseActivity extends AppCompatActivity implements ServiceListener {
	protected IMService mIMService;
	protected boolean mIsBind = false;
	protected DbHelper mDB;
//	protected DialogBuilder mBuilder;
//	protected ProgressDialog mProgressDialog;
	protected Handler mHandler;
	protected Set<Fragment> mFragments;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDB = DbHelper.getInstance(this);
		setBuilder(new DialogBuilder(this));
		setIMProgress(new IMProgressDialog(this));
		mHandler = new Handler();
		mFragments = new CopyOnWriteArraySet<>();
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

	@Override
	protected void onDestroy() {
//		mBuilder.getDialog().dismiss();
		super.onDestroy();
	}

//	public DialogBuilder getBuilder(){
//		return mBuilder;
//	}
//
//	public ProgressDialog getProgress(){
//		return mProgressDialog;
//	}

	public IMService getIMService(){
		return mIMService;
	}

	public void registerListeners(){
		mIMService.addListener(this);
		if (this instanceof RoomListener) {
			getRoomManager().addListener((RoomListener) this);
		}
		if (this instanceof RosterListener) {
			getRosterManager().removeListener((RosterListener) this);
		}
		if (this instanceof MessageListener) {
			getMessageManager().addMessageListener((MessageListener) this);
		}
	}

	public void unregisterListeners(){
		mIMService.removeListener(this);
		if (this instanceof RoomListener) {
			getRoomManager().removeListener((RoomListener) this);
		}
		if (this instanceof RosterListener) {
			getRosterManager().removeListener((RosterListener) this);
		}
		if (this instanceof MessageListener) {
			getMessageManager().removeMessageListener((MessageListener) this);
		}
	}

	protected void onBackendConnected() {
		for(Fragment fragment : mFragments){
			((BaseFragment)fragment).onBackendConnected();
		}
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
