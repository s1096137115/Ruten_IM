package com.avengers.publicim.data.action;

import com.avengers.publicim.data.entities.User;
import com.google.gson.annotations.SerializedName;

/**
 * Created by D-IT-MAX2 on 2016/6/17.
 */
public class SetRoster {
	@SerializedName("action")
	private String action;

	@SerializedName("error")
	private String error;

	@SerializedName("relationship")
	private Integer relationship;

	@SerializedName("target")
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

	public Integer getRelationship() {
		return relationship;
	}

	public void setRelationship(Integer relationship) {
		this.relationship = relationship;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
