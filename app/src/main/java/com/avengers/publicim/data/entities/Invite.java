package com.avengers.publicim.data.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by D-IT-MAX2 on 2016/6/7.
 */
public class Invite implements Serializable {

	public class Type{
		public static final String FRIEND = "friend";
		public static final String ROOM = "room";
	}

	/**
	 * 傳送端
	 */
	@SerializedName("from")
	private User from;

	/**
	 * 接收端
	 */
	@SerializedName("to")
	private User to;

	/**
	 * 邀請類別
	 */
	@SerializedName("type")
	private String type;

	/**
	 * 房間資料
	 */
	@SerializedName("room")
	private Room room;

	/**
	 * 群組角色
	 */
	@SerializedName("role")
	private Integer role;

	/**
	 * 好友關係
	 */
	@SerializedName("relationship")
	private Integer relationship;

	/**
	 * 邀請群組的rid
	 */
	@SerializedName("rid")
	private String rid;

	@SerializedName("presence")
	private Presence presence;

	/**
	 * 邀請好友
	 * @param to
	 * @param type
	 */
	public Invite(User to, String type){
		this.to = to;
		this.type = type;
	}

	/**
	 * 邀請群組
	 * @param to
	 * @param type
	 * @param rid
	 */
	public Invite(User to, String type, String rid){
		this.to = to;
		this.type = type;
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

	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	public Integer getRelationship() {
		return relationship;
	}

	public void setRelationship(Integer relationship) {
		this.relationship = relationship;
	}

	public User getTo() {
		return to;
	}

	public void setTo(User to) {
		this.to = to;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
