package com.avengers.publicim.data.callback;

import android.os.Bundle;

/**
 * Created by D-IT-MAX2 on 2016/7/5.
 */
public class ServiceEvent implements Event {

	public static final int EVENT_CLOSE_DIALOG = 0;
	public static final int EVENT_CLOSE_ACTIVITY = 1;

	private int mCurrentEvent;
	private ServiceListener mCurrentListener;
	private Bundle mBundle;

	public ServiceEvent(int event, ServiceListener listener){
		mCurrentEvent = event;
		mCurrentListener = listener;
	}

	public int getEvent(){
		return mCurrentEvent;
	}

	public ServiceListener getListener(){
		return mCurrentListener;
	}

	public Bundle getBundle() {
		return mBundle;
	}

	public void setBundle(Bundle bundle) {
		this.mBundle = bundle;
	}
}
