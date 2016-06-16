package com.avengers.publicim.data.entities;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by D-IT-MAX2 on 2016/3/4.
 */
public class Group implements Serializable {
	public static final String GID = "gid";

	public static final String NAME = "name";

	@SerializedName("name")
	private String name;

	@SerializedName("gid")
	private String gid;

	public Group(String gid, String name) {
		this.gid = gid;
		this.name = name;
	}

	public static Group newInstance(Cursor cursor){
		return new Group(
				cursor.getString(cursor.getColumnIndexOrThrow(Group.GID)),
				cursor.getString(cursor.getColumnIndexOrThrow(Group.NAME))
		);
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

	@Override
	public boolean equals(Object o) {
		if(o instanceof Group) {
			Group group = (Group) o;
			return this.gid.equals(group.gid) && this.name.equals(group.name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int PRIME = 138;
		int result = 1;
		result = PRIME * result + (PRIME * getGid().hashCode()) + getName().hashCode();
		return result;
	}
}
