package com.avengers.publicim.data.action;

import com.google.gson.annotations.SerializedName;

/**
 * Created by D-IT-MAX2 on 2016/8/12.
 */
public class SetRoom {
	@SerializedName("action")
	private String action;

	@SerializedName("error")
	private String error;

	@SerializedName("rid")
	private String rid;

	@SerializedName("name")
	private String name;

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

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}
}
