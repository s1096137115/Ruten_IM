package com.avengers.publicim.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.avengers.publicim.activity.BaseActivity;
import com.avengers.publicim.conponent.DbHelper;
import com.avengers.publicim.conponent.IMService;
import com.avengers.publicim.data.listener.ChatListener;
import com.avengers.publicim.data.listener.MessageListener;
import com.avengers.publicim.data.listener.RosterListener;

import static com.avengers.publicim.conponent.IMApplication.getChatManager;
import static com.avengers.publicim.conponent.IMApplication.getMessageManager;
import static com.avengers.publicim.conponent.IMApplication.getRosterManager;

/**
 * Created by D-IT-MAX2 on 2016/5/10.
 */
public abstract class BaseFragment extends Fragment {
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
		registerListeners();
	}

	@Override
	public void onStop() {
		super.onStop();
		unregisterListeners();
	}

	public void onBackendConnected() {
		if(getActivity() != null){
			mIMService = ((BaseActivity)getActivity()).getIMService();
		}
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
}
