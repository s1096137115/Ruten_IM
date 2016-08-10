package com.avengers.publicim.data.action;

import com.avengers.publicim.data.entities.Group;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/8/1.
 */
public class GetGroup {
	@SerializedName("action")
	private String action;

	@SerializedName("error")
	private String error;

	@SerializedName("group")
	private List<Group> groups;

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

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
}
