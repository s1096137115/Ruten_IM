package com.avengers.publicim.data.action;

import com.avengers.publicim.data.entities.Room;
import com.avengers.publicim.data.entities.User;
import com.google.gson.annotations.SerializedName;

/**
 * Created by D-IT-MAX2 on 2016/7/1.
 */
public class SetRoomMemberRole {
	@SerializedName("action")
	private String action;

	@SerializedName("error")
	private String error;

	@SerializedName("rid")
	private String rid;

	@SerializedName("role")
	private Integer role;

	@SerializedName("target")
	private User target;

	@SerializedName("room")
	private Room room;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	public User getTarget() {
		return target;
	}

	public void setTarget(User target) {
		this.target = target;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
}
