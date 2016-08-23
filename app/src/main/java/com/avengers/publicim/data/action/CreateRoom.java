package com.avengers.publicim.data.action;

import com.avengers.publicim.data.entities.Room;
import com.google.gson.annotations.SerializedName;

/**
 * Created by D-IT-MAX2 on 2016/6/17.
 */
public class CreateRoom {
	@SerializedName("action")
	private String action;

	@SerializedName("error")
	private String error;

	@SerializedName("name")
	private String name;

	@SerializedName("type")
	private String type;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
}
