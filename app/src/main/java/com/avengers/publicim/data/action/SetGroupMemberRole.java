package com.avengers.publicim.data.action;

import com.avengers.publicim.data.entities.User;
import com.google.gson.annotations.SerializedName;

/**
 * Created by D-IT-MAX2 on 2016/7/1.
 */
public class SetGroupMemberRole {
	@SerializedName("action")
	private String action;

	@SerializedName("error")
	private String error;

	@SerializedName("gid")
	private String gid;

	@SerializedName("role")
	private Integer role;

	@SerializedName("user")
	private User user;

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

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
