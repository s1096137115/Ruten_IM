package com.avengers.publicim.data.callback;

/**
 * Created by D-IT-MAX2 on 2016/7/5.
 */
public class ServiceEvent implements Event {

	public static final int EVENT_CLOSE_DIALOG = 0;

	private int mCurrentEvent;
	private ServiceListener mCurrentListener;

	public ServiceEvent(int event, ServiceListener listener){
		mCurrentEvent = event;
		mCurrentListener = listener;
	}

	public int getEvent(){
		return mCurrentEvent;
	}

	public ServiceListener toListener(){
		return mCurrentListener;
	}

}
