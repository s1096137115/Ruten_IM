package com.avengers.publicim.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.avengers.publicim.activity.BaseActivity;
import com.avengers.publicim.conponent.DbHelper;
import com.avengers.publicim.conponent.IMService;
import com.avengers.publicim.data.listener.MessageListener;
import com.avengers.publicim.data.listener.RoomListener;
import com.avengers.publicim.data.listener.RosterListener;
import com.avengers.publicim.data.listener.ServiceListener;

import static com.avengers.publicim.conponent.IMApplication.getMessageManager;
import static com.avengers.publicim.conponent.IMApplication.getRoomManager;
import static com.avengers.publicim.conponent.IMApplication.getRosterManager;

/**
 * Created by D-IT-MAX2 on 2016/5/10.
 */
public abstract class BaseFragment extends Fragment implements ServiceListener{
	protected DbHelper mDB;
	protected Handler mHandler = new Handler();
	protected IMService mIMService;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDB = DbHelper.getInstance(getContext());
		Log.i(this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName());
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
			getRoomManager().addListener((RoomListener) this);
		}
		if (this instanceof RosterListener) {
			getRosterManager().addListener((RosterListener) this);
		}
		if (this instanceof MessageListener) {
			getMessageManager().addMessageListener((MessageListener) this);
		}
	}

	public void unregisterListeners(){
		if(mIMService != null){
			mIMService.removeListener(this);
		}
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
}
