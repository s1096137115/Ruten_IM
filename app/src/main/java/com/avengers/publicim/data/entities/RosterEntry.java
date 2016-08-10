package com.avengers.publicim.data.entities;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by D-IT-MAX2 on 2016/3/3.
 */
public class RosterEntry implements Serializable, Contact {
	public static final String TABLE_NAME = "roster";

	public static final String RELATIONSHIP = "relationship";

	public static final String CREATE_SQL =
			"CREATE TABLE " + TABLE_NAME + "("
					+ User.UID + " VARCHAR(50), "
					+ User.NAME + " VARCHAR(30), "
					+ Presence.DESCRIBE + " VARCHAR(30), "
					+ Presence.PHOTO + " VARCHAR(30), "
					+ Presence.STATUS + " VARCHAR(30), "
					+ RELATIONSHIP + " VARCHAR(30), "
					+ "PRIMARY KEY (" + User.UID + "," + User.NAME + ") "
					+ ") ";

	/**
	 * 黑名單
	 */
	public static final int RELATION_BLACKLIST = -1;
	/**
	 * 陌生人/刪除好友
	 */
	public static final int RELATION_STRANGER = 0;
	/**
	 * 已受邀
	 */
	public static final int RELATION_INVITEES = 1;
	/**
	 * 好友
	 */
	public static final int RELATION_FRIEND = 2;

	@SerializedName("relationship")// 可以讓你的field名稱與API不同
	private Integer relationship;

	@SerializedName("presence")
	private Presence presence;

	@SerializedName("user")
	private User user;

	public RosterEntry(User user, Presence presence, Integer relationship) {
		this.presence = presence;
		this.relationship = relationship;
		this.user = user;
	}

	public static RosterEntry newInstance(Cursor cursor){
		return new RosterEntry(
				User.newInstance(cursor), Presence.newInstance(cursor),
				cursor.getInt(cursor.getColumnIndexOrThrow(RosterEntry.RELATIONSHIP))
		);
	}

	public ContentValues getContentValues(){
		ContentValues cv = new ContentValues();
		cv.put(User.UID, user.getUid());
		cv.put(User.NAME, user.getName());
		cv.put(Presence.DESCRIBE, presence.getDescribe());
		cv.put(Presence.PHOTO, presence.getPhoto());
		cv.put(Presence.STATUS, presence.getStatus());
		cv.put(RELATIONSHIP, relationship);
		return cv;
	}

	public Presence getPresence() {
		return presence;
	}

	public void setPresence(Presence presence) {
		this.presence = presence;
	}

	public Integer getRelationship() {
		return relationship;
	}

	public void setRelationship(Integer relationship) {
		this.relationship = relationship;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String getId() {
		return user.getUid();
	}

	@Override
	public String getName() {
		return user.getName();
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof RosterEntry) {
			RosterEntry entry = (RosterEntry) o;
			return this.user.equals(entry.user)
					&& this.presence.equals(entry.presence)
					&& this.relationship.equals(entry.relationship);
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int PRIME = 81;
		int result = 1;
		result = PRIME * result + (PRIME * getUser().hashCode())
				+ getPresence().hashCode() + getRelationship().hashCode();
		return result;
	}
}
