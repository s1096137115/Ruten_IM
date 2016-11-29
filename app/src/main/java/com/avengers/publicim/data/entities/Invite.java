package com.avengers.publicim.data.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by D-IT-MAX2 on 2016/6/7.
 */
public abstract class Invite implements Serializable {

	public class Type{
		public static final String FRIEND = "friend";
		public static final String ROOM = "room";
	}

	/**
	 * 傳送端
	 */
	@SerializedName("from")
	protected User from;

//	/**
//	 * 接收端
//	 */
//	@SerializedName("to")
//	private User to;

	/**
	 * 邀請類別
	 */
	@SerializedName("type")
	protected String type;

	/**
	 * 房間資料
	 */
	@SerializedName("room")
	protected Room room;

	/**
	 * 邀請群組的rid
	 */
	@SerializedName("rid")
	protected String rid;

	@SerializedName("presence")
	protected Presence presence;

	public String getRidOfInvite() {
		return rid;
	}

	public void setRidOfInvite(String rid) {
		this.rid = rid;
	}

	public User getFrom() {
		return from;
	}

	public void setFrom(User from) {
		this.from = from;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Presence getPresence() {
		return presence;
	}

	public void setPresence(Presence presence) {
		this.presence = presence;
	}
}
