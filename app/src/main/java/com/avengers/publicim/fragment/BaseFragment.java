package com.avengers.publicim.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.avengers.publicim.activity.BaseActivity;
import com.avengers.publicim.component.DbHelper;
import com.avengers.publicim.component.IMApplication;
import com.avengers.publicim.component.IMService;
import com.avengers.publicim.data.Manager.MessageManager;
import com.avengers.publicim.data.Manager.RoomManager;
import com.avengers.publicim.data.Manager.RosterManager;
import com.avengers.publicim.data.listener.MessageListener;
import com.avengers.publicim.data.listener.RoomListener;
import com.avengers.publicim.data.listener.RosterListener;
import com.avengers.publicim.data.listener.ServiceListener;

/**
 * Created by D-IT-MAX2 on 2016/5/10.
 */
public abstract class BaseFragment extends Fragment implements ServiceListener{
	protected DbHelper mDB;
	protected Handler mHandler = new Handler();
	protected IMService mIMService;
	protected RosterManager mRosterManager;
	protected RoomManager mRoomManager;
	protected MessageManager mMessageManager;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDB = DbHelper.getInstance(getContext());
		initManager();
		Log.i(this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName());
	}

	private void initManager(){
		mRosterManager = RosterManager.getInstance(IMApplication.getContext());
		mRoomManager = RoomManager.getInstance(IMApplication.getContext());
		mMessageManager = MessageManager.getInstance();
	}

	@Override
	public void onStart() {
		super.onStart();
		mIMService = ((BaseActivity)getActivity()).getIMService();
		if(mIMService != null){
			registerListeners();
		}
		Log.i(this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName());
	}

	@Override
	public void onStop() {
		super.onStop();
		unregisterListeners();
		Log.i(this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName());
	}

	public void onBackendConnected() {
		if(getActivity() != null){
			mIMService = ((BaseActivity)getActivity()).getIMService();
			registerListeners();
		}
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
}
