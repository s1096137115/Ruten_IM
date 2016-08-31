package com.avengers.publicim.data;

/**
 * Created by D-IT-MAX2 on 2016/3/1.
 */
public class Constants {

	//room-base
	public static final String CHAT_SERVER_URL = "http://10.17.1.40:8000";
	//normal
//	public static final String CHAT_SERVER_URL = "http://10.17.1.30:8000";
	//max-local
//	public static final String CHAT_SERVER_URL = "http://10.0.1.7:8000";

	public static final String SAVE_DB_SIMPLE_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public class Socket{
		public static final String EVENT_REQUEST = "request";
		public static final String EVENT_RESPONSE = "response";
		public static final String EVENT_JOIN = "join";
		public static final String EVENT_SEND_MESSAGE = "SendMessage";
		public static final String EVENT_SEND_ROOM_MESSAGE = "SendRoomMessage";
		public static final String EVENT_RECEIVE_MESSAGE = "ReceiveMessage";
		public static final String EVENT_RECEIVE_ROOM_MESSAGE = "ReceiveRoomMessage";
		public static final String EVENT_SEND_PRESENCE = "SendPresence";
		public static final String EVENT_RECEIVE_PRESENCE = "ReceivePresence";
		public static final String EVENT_GET_SYNC_DATA = "getSyncData";
		public static final String EVENT_SET_ROSTER = "setRoster";
		public static final String EVENT_GET_ROSTER = "getRoster";
		public static final String EVENT_SEND_INVITE = "SendInvite";
		public static final String EVENT_RECEIVE_INVITE = "ReceiveInvite";
		public static final String EVENT_GET_ROOM = "getRoom";
		public static final String EVENT_CREATE_ROOM = "createRoom";
		public static final String EVENT_SET_ROOM_MEMBER_ROLE = "setRoomMemberRole";
		public static final String EVENT_GET_USER = "getUser";
		public static final String EVENT_GET_MESSAGE = "getMessage";
	}
}
