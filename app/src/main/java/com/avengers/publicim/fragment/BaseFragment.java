package com.avengers.publicim.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.avengers.publicim.activity.BaseActivity;
import com.avengers.publicim.conponent.DbHelper;
import com.avengers.publicim.conponent.IMService;
import com.avengers.publicim.data.callback.GroupListener;
import com.avengers.publicim.data.callback.MessageListener;
import com.avengers.publicim.data.callback.RoomListener;
import com.avengers.publicim.data.callback.RosterListener;
import com.avengers.publicim.data.callback.ServiceListener;

import static com.avengers.publicim.conponent.IMApplication.getGroupManager;
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
	}

	@Override
	public void onStart() {
		super.onStart();
		if(mIMService != null){
			registerListeners();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		unregisterListeners();
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
		if (this instanceof GroupListener) {
			getGroupManager().addListener((GroupListener) this);
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
		if (this instanceof GroupListener) {
			getGroupManager().removeListener((GroupListener) this);
		}
		if (this instanceof MessageListener) {
			getMessageManager().removeMessageListener((MessageListener) this);
		}
	}
}
