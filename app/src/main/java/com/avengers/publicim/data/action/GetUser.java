package com.avengers.publicim.data.action;

import com.avengers.publicim.data.entities.AdvUser;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/8/25.
 */
public class GetUser implements Serializable{
	public class Type{
		public static final String ID = "id";
		public static final String PHONE = "phone";
		public static final String NICKNAME = "nickname";
	}

	@SerializedName("action")
	private String action;

	@SerializedName("error")
	private String error;

	@SerializedName("type")
	private String type;

	private String key;

	@SerializedName("user")
	private List<AdvUser> users;

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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<AdvUser> getUsers() {
		return users;
	}

	public void setUsers(List<AdvUser> users) {
		this.users = users;
	}
}
