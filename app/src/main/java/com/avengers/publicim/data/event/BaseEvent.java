package com.avengers.publicim.data.event;

import android.os.Bundle;

import com.avengers.publicim.data.listener.Listener;

/**
 * Created by D-IT-MAX2 on 2016/7/5.
 */
public abstract class BaseEvent {
	private int mCurrentEvent;
	private Listener mCurrentListener;
	private Bundle mBundle;

	public BaseEvent(int event){
		mCurrentEvent = event;
	}

	public int getEvent(){
		return mCurrentEvent;
	}

	public void setEvent(int event) {
		this.mCurrentEvent = event;
	}

	public Listener getListener(){
		return mCurrentListener;
	}

	public void setListener(Listener listener) {
		this.mCurrentListener = listener;
	}

	public Bundle getBundle() {
		return mBundle;
	}

	public void setBundle(Bundle bundle) {
		this.mBundle = bundle;
	}
}
