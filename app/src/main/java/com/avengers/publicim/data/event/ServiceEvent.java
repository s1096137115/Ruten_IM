package com.avengers.publicim.data.event;

/**
 * Created by D-IT-MAX2 on 2016/7/5.
 */
public class ServiceEvent extends BaseEvent {

	public static class Event {
		public static final int CLOSE_DIALOG = 0;
		public static final int CLOSE_ACTIVITY = 1;

		public static final int ADD_MESSAGE = 10;
		public static final int ADD_MESSAGES = 11;
		public static final int UPDATE_MESSAGE = 12;
		public static final int LOAD_MESSAGE = 12;

		public static final int GET_ROOM = 20;
		public static final int DELETE_ROOM = 21;


		public static final int GET_GROUP = 3;

		public static final int GET_ROSTER = 40;
	}

	public ServiceEvent(int event){
		super(event);
	}

}
