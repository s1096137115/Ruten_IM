package com.avengers.publicim.data.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by D-IT-MAX2 on 2016/3/9.
 */
public class Presence {
	@SerializedName("photo")
	private String photo;

	@SerializedName("status")
	private Integer status;

	@SerializedName("describe")
	private String describe;

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
