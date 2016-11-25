package com.avengers.publicim.data.action;

import com.avengers.publicim.data.entities.Option;
import com.avengers.publicim.data.entities.Room;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/8/1.
 */
public class GetRoom {
	@SerializedName("action")
	private String action;

	@SerializedName("error")
	private String error;

	@SerializedName("name")
	private String name;

	@SerializedName("room")
	private List<Room> rooms;

	@SerializedName("option")
	private Option option;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Room> getRooms() {
		return rooms;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}

	public Option getOption() {
		return option;
	}

	public void setOption(Option option) {
		this.option = option;
	}
}
