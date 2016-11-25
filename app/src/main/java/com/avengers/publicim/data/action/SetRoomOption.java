package com.avengers.publicim.data.action;

import com.google.gson.annotations.SerializedName;

/**
 * Created by D-IT-MAX2 on 2016/11/23.
 */

public class SetRoomOption {
	@SerializedName("action")
	private String action;

	@SerializedName("error")
	private String error;

	@SerializedName("rid")
	private String rid;

	@SerializedName("mute")
	private Integer mute;

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

	public Integer getMute() {
		return mute;
	}

	public void setMute(Integer mute) {
		this.mute = mute;
	}
}
