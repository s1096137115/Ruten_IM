package com.avengers.publicim.data.entities;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by D-IT-MAX2 on 2016/8/1.
 */
public class Member implements Serializable{
	public static final String TABLE_NAME = "member";

	public static final String ROLE = "role";

	public static final String USER = "user";

	public static final String INVITOR = "invitor";

	public static final String GROUP_ID = "group_id";

	public static final String CREATE_SQL =
			"CREATE TABLE " + TABLE_NAME + "("
					+ ROLE + " VARCHAR(5), "
					+ USER + " VARCHAR(30), "
					+ INVITOR + " VARCHAR(30), "
					+ GROUP_ID + " VARCHAR(50), "
//					+ "FOREIGN KEY (" + GROUP_ID + ") REFERENCES " + Group.TABLE_NAME + "(" + Group.GID + ") "
					+ "PRIMARY KEY (" + USER + "," + GROUP_ID + ") "
					+ ") ";


	@SerializedName("role")
	private String role;

	@SerializedName("user")
	private String user;

	@SerializedName("invitor")
	private String invitor;


	private String groupId;

	private Member(String user, String invitor, String role, String groupId) {
		this.user = user;
		this.invitor = invitor;
		this.role = role;
		this.groupId = groupId;
	}

	public static Member newInstance(Cursor cursor){
		return new Member(
				cursor.getString(cursor.getColumnIndexOrThrow(USER)),
				cursor.getString(cursor.getColumnIndexOrThrow(INVITOR)),
				cursor.getString(cursor.getColumnIndexOrThrow(ROLE)),
				cursor.getString(cursor.getColumnIndexOrThrow(GROUP_ID))
		);
	}

	public ContentValues getContentValues(){
		ContentValues values = new ContentValues();
		values.put(ROLE, getRole());
		values.put(USER, getUser());
		values.put(INVITOR, getInvitor());
		values.put(GROUP_ID, getGroupId());
		return values;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getInvitor() {
		return invitor;
	}

	public void setInvitor(String invitor) {
		this.invitor = invitor;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Member) {
			Member member = (Member) o;;
			return this.role.equals(member.role)
					&& this.user.equals(member.user)
					&& this.invitor.equals(member.invitor);
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int PRIME = 97;
		int result = 1;
		result = PRIME * result + (PRIME * getRole().hashCode())
				+ getUser().hashCode() + getInvitor().hashCode();
		return result;
	}
}
