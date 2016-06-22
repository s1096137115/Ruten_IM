package com.avengers.publicim.data.entities;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by D-IT-MAX2 on 2016/3/4.
 */
public class Group implements Serializable {
	public static final String TABLE_NAME = "group";

	public static final String GID = "gid";

	public static final String NAME = "name";

	public static final String ROLE = "role";

	public static final String CREATE_SQL =
			"CREATE TABLE " + TABLE_NAME + "("
					+ GID + " VARCHAR(50) PRIMARY KEY, "
					+ NAME + " VARCHAR(50), "
					+ ROLE + " VARCHAR(5) "
					+ ") ";


	@SerializedName("gid")
	private String gid;

	@SerializedName("name")
	private String name;

	@SerializedName("role")
	private String role;

	@SerializedName("invitor")
	private User invitor;

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

	public ContentValues getContentValues(){
		ContentValues values = new ContentValues();
		values.put(Group.GID, getGid());
		values.put(Group.NAME, getName());
		values.put(Group.ROLE, getRole());
		return values;
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

	public User getInvitor() {
		return invitor;
	}

	public void setInvitor(User invitor) {
		this.invitor = invitor;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
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
