package com.avengers.publicim.data.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by D-IT-MAX2 on 2016/3/9.
 */
public class User {
	public static final String TABLE_NAME = "User";

	public static final String ID = "id";

	public static final String NAME = "name";

	public static final String CREATE_SQL =
			"CREATE TABLE " + TABLE_NAME + "("
					+ ID + " VARCHAR(30) PRIMARY KEY, "
					+ NAME + " VARCHAR(30) "
					+ ") ";

	@SerializedName("name")
	private String name;

	@SerializedName("identify")
	private String id;

	public User(String name, String id) {
		this.name = name;
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}
