package com.avengers.publicim.data.action;

import com.google.gson.annotations.SerializedName;

/**
 * Created by D-IT-MAX2 on 2016/6/17.
 */
public class CreateGroup {
	@SerializedName("action")
	private String action;

	@SerializedName("error")
	private String error;

	@SerializedName("name")
	private String name;

	@SerializedName("gid")
	private String gid;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
