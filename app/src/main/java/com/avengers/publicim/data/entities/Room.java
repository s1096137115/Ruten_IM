package com.avengers.publicim.data.entities;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/3/4.
 */
public class Room extends Contact implements Serializable {
	public static final String TABLE_NAME = "room";

	public static final String RID = "rid";

	public static final String NAME = "name";

	public static final String OWNER = "owner";

	public static final String TYPE = "type";

	/**
	 * no stored in DB
	 */
	public static final String DATE = "date";

	/**
	 * no stored in DB
	 */
	public static final String UNREAD = "unread";

	public static final String CREATE_SQL =
			"CREATE TABLE " + TABLE_NAME + "("
					+ RID + " VARCHAR(50) PRIMARY KEY, "
					+ NAME + " VARCHAR(50), "
					+ OWNER + " VARCHAR(30), "
					+ TYPE + " VARCHAR(20) "
					+ ") ";

	public class Role{
		/**
		 * 黑名單
		 */
		public static final int BLACKLIST = -1;
		/**
		 * 離開
		 */
		public static final int EXIT = 0;
		/**
		 * 已受邀
		 */
		public static final int INVITEES = 1;
		/**
		 * 禁止發言
		 */
		public static final int BAN_SPEAK = 2;
		/**
		 * 成員
		 */
		public static final int MEMBER = 3;
		/**
		 * 管理者
		 */
		public static final int MANAGER = 4;
	}

	public class Type{
		public static final String SINGLE = "single";

		public static final String GROUP = "group";

		public static final String MULTIPLE = "multiple";

		/**
		 * local type - has messages of rooms
		 */
		public static final String CHAT = "chat";

		/**
		 * local type - all of rooms
		 */
		public static final String ALL = "all";
	}

	@SerializedName("rid")
	private String rid;

	@SerializedName("name")
	private String name;

	@SerializedName("owner")
	private String owner;

	@SerializedName("member")
	private List<Member> members;

	@SerializedName("type")
	private String type;

	private long date;

	private Message lastMsg;

	private int unread;

	public Room(String rid, String name,String owner, String type) {
		this.rid = rid;
		this.name = name;
		this.owner = owner;
		this.type = type;
	}

	public static Room newInstance(Cursor cursor){
		return new Room(
				cursor.getString(cursor.getColumnIndexOrThrow(RID)),
				cursor.getString(cursor.getColumnIndexOrThrow(NAME)),
				cursor.getString(cursor.getColumnIndexOrThrow(OWNER)),
				cursor.getString(cursor.getColumnIndexOrThrow(TYPE))
		);
	}

	public ContentValues getContentValues(){
		ContentValues values = new ContentValues();
		values.put(RID, rid);
		values.put(NAME, name);
		values.put(OWNER, owner);
		values.put(TYPE, type);
		return values;
	}

	@Override
	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Member> getMembers() {
		return members;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public Message getLastMsg() {
		return lastMsg;
	}

	public void setLastMsg(Message lastMsg) {
		this.lastMsg = lastMsg;
	}

	public int getUnread() {
		return unread;
	}

	public void setUnread(int unread) {
		this.unread = unread;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 額外的資訊來自其他DB，不會存取在room的DB上
	 * @param cursor
	 */
	public void setInfo(Cursor cursor){
		setLastMsg(Message.newInstance(cursor));
		int unread = cursor.getInt(cursor.getColumnIndex(UNREAD));
		Long date = cursor.getLong(cursor.getColumnIndex(DATE));
		setUnread(unread);
		setDate(date);
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Room) {
			Room room = (Room) o;
			return this.rid.equals(room.rid)
					&& this.name.equals(room.name) && this.name.equals(room.name)
					&& this.owner.equals(room.owner) && this.type.equals(room.type)
					&& CollectionUtils.isEqualCollection(this.members, room.members);
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int PRIME = 121;
		int result = 1;
		result = PRIME * result + (PRIME * getRid().hashCode()) + getName().hashCode()
				+ getOwner().hashCode() + getType().hashCode() + getMembers().hashCode();
		return result;
	}
}
