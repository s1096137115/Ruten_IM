package com.avengers.publicim.data.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by D-IT-MAX2 on 2016/3/3.
 */
public class RosterEntry extends Contact implements Parcelable {
	public static final String TABLE_NAME = "roster";

	public static final String RELATIONSHIP = "relationship";

	public static final String RID = "rid";

	public static final String CREATE_SQL =
			"CREATE TABLE " + TABLE_NAME + "("
					+ User.UID + " VARCHAR(50), "
					+ User.NAME + " VARCHAR(30), "
					+ Presence.DESCRIBE + " VARCHAR(30), "
					+ Presence.PHOTO + " VARCHAR(30), "
					+ Presence.STATUS + " VARCHAR(30), "
					+ RELATIONSHIP + " VARCHAR(30), "
					+ RID + " VARCHAR(50), "
					+ "PRIMARY KEY (" + User.UID + "," + User.NAME + ") "
					+ ") ";

	public class Releationship{
		/**
		 * 黑名單
		 */
		public static final int BLACKLIST = -1;
		/**
		 * 陌生人/刪除好友
		 */
		public static final int STRANGER = 0;
		/**
		 * 已受邀
		 */
		public static final int INVITEES = 1;
		/**
		 * 好友
		 */
		public static final int FRIEND = 2;
	}

	@SerializedName("relationship")// 可以讓你的field名稱與API不同
	private Integer relationship;

	@SerializedName("presence")
	private Presence presence;

	@SerializedName("user")
	private User user;

	@SerializedName("rid")
	private String rid;

	public RosterEntry(User user, Presence presence, Integer relationship, String rid) {
		this.user = user;
		this.presence = presence;
		this.relationship = relationship;
		this.rid = rid;
	}

	public static RosterEntry newInstance(Cursor cursor){
		return new RosterEntry(
				User.newInstance(cursor), Presence.newInstance(cursor),
				cursor.getInt(cursor.getColumnIndexOrThrow(RosterEntry.RELATIONSHIP)),
				cursor.getString(cursor.getColumnIndexOrThrow(RosterEntry.RID))
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
		cv.put(RID, rid);
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

	public void setRelationship(int relationship) {
		this.relationship = relationship;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String getName() {
		return user.getName();
	}

	@Override
	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof RosterEntry) {
			RosterEntry entry = (RosterEntry) o;
			return this.user.equals(entry.user)
					&& this.presence.equals(entry.presence)
					&& this.relationship.equals(entry.relationship)
					&& this.rid.equals(entry.rid);
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int PRIME = 81;
		int result = 1;
		result = PRIME * result + (PRIME * getUser().hashCode())
				+ getPresence().hashCode() + getRelationship().hashCode() + getRid().hashCode();
		return result;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(this.relationship);
		dest.writeParcelable(this.presence, flags);
		dest.writeParcelable(this.user, flags);
		dest.writeString(this.rid);
	}

	protected RosterEntry(Parcel in) {
		this.relationship = (Integer) in.readValue(Integer.class.getClassLoader());
		this.presence = in.readParcelable(Presence.class.getClassLoader());
		this.user = in.readParcelable(User.class.getClassLoader());
		this.rid = in.readString();
	}

	public static final Creator<RosterEntry> CREATOR = new Creator<RosterEntry>() {
		@Override
		public RosterEntry createFromParcel(Parcel source) {
			return new RosterEntry(source);
		}

		@Override
		public RosterEntry[] newArray(int size) {
			return new RosterEntry[size];
		}
	};
}
