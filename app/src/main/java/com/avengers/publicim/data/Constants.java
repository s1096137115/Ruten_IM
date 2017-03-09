package com.avengers.publicim.data;

/**
 * Created by D-IT-MAX2 on 2016/3/1.
 */
public class Constants {

	//room-base
	public static final String CHAT_SERVER_URL = "http://10.17.1.40:8000";
//	public static final String CHAT_SERVER_URL = "http://172.18.0.71:8040";
	//normal
//	public static final String CHAT_SERVER_URL = "http://10.17.1.30:8000";
	//max-local
//	public static final String CHAT_SERVER_URL = "http://10.0.1.7:8000";

	//3rd-party
	public static final String THIRD_PARTY_SERVER_URL = "http://10.17.0.3:8080/";

	public class Date{
		public static final String LONG = "yyyy/M/d HH:mm";
		public static final String SHORT = "ahh:mm";
		public static final String WEEK = "M/d E";
		public static final String YEAR = "yyyy";
		public static final String YEAR_WEEK = "yyyy/M/d E";
	}

	public class Socket{
		//send-receive
		public static final String EVENT_JOIN = "join";
		public static final String EVENT_SEND_MESSAGE = "SendMessage";
		public static final String EVENT_RECEIVE_MESSAGE = "ReceiveMessage";
		public static final String EVENT_SEND_INVITE = "SendInvite";
		public static final String EVENT_RECEIVE_INVITE = "ReceiveInvite";
		public static final String EVENT_SEND_PRESENCE = "SendPresence";
		public static final String EVENT_RECEIVE_PRESENCE = "ReceivePresence";
		public static final String EVENT_SEND_MESSAGE_READ = "SendMessageRead";
		public static final String EVENT_RECEIVE_MESSAGE_READ = "ReceiveMessageRead";

		public static final String EVENT_REQUEST = "request";
		public static final String EVENT_RESPONSE = "response";
		//request-response
		public static final String EVENT_SET_ROSTER = "setRoster";
		public static final String EVENT_GET_ROSTER = "getRoster";
		public static final String EVENT_GET_ROOM = "getRoom";
		public static final String EVENT_CREATE_ROOM = "createRoom";
		public static final String EVENT_SET_ROOM_MEMBER_ROLE = "setRoomMemberRole";
		public static final String EVENT_GET_USER = "getUser";
		public static final String EVENT_GET_MESSAGE = "getMessage";
		public static final String EVENT_REGISTER_PUSH = "registerPush";
		public static final String EVENT_UNREGISTER_PUSH = "unregisterPush";
		public static final String EVENT_SET_ROOM_OPTION = "setRoomOption";
	}
}
