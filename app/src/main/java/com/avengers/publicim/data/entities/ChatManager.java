package com.avengers.publicim.data.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/3/22.
 */
public class ChatManager {
	public static final String TABLE_NAME = "chat";

	public static final String CREATE_SQL =
			"CREATE TABLE " + TABLE_NAME + "("
					+ Chat.CID + " VARCHAR(50) PRIMARY KEY, "
					+ Chat.TITLE + " VARCHAR(50), "
					+ "FOREIGN KEY (" + Chat.CID + ") REFERENCES " + RosterManager.TABLE_NAME + "(" + User.NAME + ") "
					+ ") ";

	public List<Chat> chats = new ArrayList<>();

	public List<Chat> getChats() {
		return chats;
	}

	public void setChats(List<Chat> chats) {
		this.chats = chats;
	}
}
