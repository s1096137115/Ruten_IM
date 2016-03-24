package com.avengers.publicim.data.entities;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

/**
 * Created by D-IT-MAX2 on 2016/3/9.
 */
public class User {
	public static final String UID = "uid";

	public static final String NAME = "name";

	@SerializedName("name")
	private String name;

	@SerializedName("identify")
	private String uid;

	public User(String uid, String name) {
		this.name = name;
		this.uid = uid;
	}

	public static User newInstance(String uid, String name){
		return new User(uid, name);
	}

	public static User newInstance(Cursor cursor){
		return new User(
				cursor.getString(cursor.getColumnIndexOrThrow(User.UID)),
				cursor.getString(cursor.getColumnIndexOrThrow(User.NAME))
		);
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}
