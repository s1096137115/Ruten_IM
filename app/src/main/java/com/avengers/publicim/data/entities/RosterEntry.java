package com.avengers.publicim.data.entities;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

/**
 * Created by D-IT-MAX2 on 2016/3/3.
 */
public class RosterEntry {
	public static final String RELATIONSHIP = "relationship";

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
}
