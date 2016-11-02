package com.avengers.publicim.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.avengers.publicim.component.DbHelper;
import com.avengers.publicim.component.IMApplication;
import com.avengers.publicim.component.IMService;
import com.avengers.publicim.component.IMService.IMBinder;
import com.avengers.publicim.data.Manager.MessageManager;
import com.avengers.publicim.data.Manager.RoomManager;
import com.avengers.publicim.data.Manager.RosterManager;
import com.avengers.publicim.data.listener.MessageListener;
import com.avengers.publicim.data.listener.RoomListener;
import com.avengers.publicim.data.listener.RosterListener;
import com.avengers.publicim.data.listener.ServiceListener;
import com.avengers.publicim.fragment.BaseFragment;
import com.avengers.publicim.view.DialogBuilder;
import com.avengers.publicim.view.IMProgressDialog;

import static com.avengers.publicim.component.IMApplication.setBuilder;
import static com.avengers.publicim.component.IMApplication.setIMProgress;

/**
 * Created by D-IT-MAX2 on 2016/3/1.
 */
public abstract class BaseActivity extends AppCompatActivity implements ServiceListener {
	protected IMService mIMService;
	protected boolean mIsBind = false;
	protected DbHelper mDB;
	protected Handler mHandler;
	protected RosterManager mRosterManager;
	protected RoomManager mRoomManager;
	protected MessageManager mMessageManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDB = DbHelper.getInstance(this);
		mHandler = new Handler();
		initManager();
		Log.i(this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName());
	}

	private void initManager(){
		mRosterManager = RosterManager.getInstance(IMApplication.getContext());
		mRoomManager = RoomManager.getInstance(IMApplication.getContext());
		mMessageManager = MessageManager.getInstance();
	}

	@Override
	protected void onStart() {
		super.onStart();
		setBuilder(new DialogBuilder(this));
		setIMProgress(new IMProgressDialog(this));
		if(mIsBind){
			registerListeners();
			onBackendConnected();
		}else{
			Intent intent = new Intent(this, IMService.class);
			startService(intent);
			bindService(intent,mSC,BIND_AUTO_CREATE);
		}
//		Log.i(this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName());
	}

	@Override
	protected void onPause() {
		super.onPause();
		setBuilder(null);
		setIMProgress(null);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(mIsBind){
			unbindService(mSC);
			unregisterListeners();
			mIsBind =false;
		}
//		Log.i(this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		Log.i(this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(!(this instanceof MainActivity)){
			switch (item.getItemId()) {
				case android.R.id.home:
					finish();
					return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	public IMService getIMService(){
		return mIMService;
	}

	public void registerListeners(){
		mIMService.addListener(this);
		if (this instanceof RoomListener) {
			mRoomManager.addListener((RoomListener) this);
		}
		if (this instanceof RosterListener) {
			mRosterManager.removeListener((RosterListener) this);
		}
		if (this instanceof MessageListener) {
			mMessageManager.addMessageListener((MessageListener) this);
		}
	}

	public void unregisterListeners(){
		mIMService.removeListener(this);
		if (this instanceof RoomListener) {
			mRoomManager.removeListener((RoomListener) this);
		}
		if (this instanceof RosterListener) {
			mRosterManager.removeListener((RosterListener) this);
		}
		if (this instanceof MessageListener) {
			mMessageManager.removeMessageListener((MessageListener) this);
		}
	}

	protected void onBackendConnected() {
		if(getSupportFragmentManager().getFragments() != null){
			for(Fragment fragment : getSupportFragmentManager().getFragments()){
				((BaseFragment)fragment).onBackendConnected();
			}
		}
		if(!mIMService.connected()) mIMService.connect();
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
