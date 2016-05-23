package com.avengers.publicim.data.entities;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by D-IT-MAX2 on 2016/3/9.
 */
public class Presence implements Serializable {
	public static final String PHOTO = "photo";

	public static final String STATUS = "status";

	public static final String DESCRIBE = "describe";

	@SerializedName("photo")
	private String photo;

	@SerializedName("status")
	private Integer status;

	@SerializedName("describe")
	private String describe;

	public Presence(String describe, String photo, Integer status) {
		this.describe = describe;
		this.photo = photo;
		this.status = status;
	}

	public static Presence newInstance(Cursor cursor){
		return new Presence(
				cursor.getString(cursor.getColumnIndexOrThrow(Presence.DESCRIBE)),
				cursor.getString(cursor.getColumnIndexOrThrow(Presence.PHOTO)),
				cursor.getInt(cursor.getColumnIndexOrThrow(Presence.STATUS))
		);
	}

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
