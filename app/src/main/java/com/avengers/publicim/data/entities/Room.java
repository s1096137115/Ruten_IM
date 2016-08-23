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
	public static final String TABLE_NAME = "group_data";

	public static final String RID = "rid";

	public static final String NAME = "name";

	public static final String ROLE = "role";

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
	}


	@SerializedName("rid")
	private String rid;

	@SerializedName("name")
	private String name;

	@SerializedName("owner")
	private String owner;

	@SerializedName("member")
	private List<Member> members;

	private int type;

	private String date;

	private Message lastMsg;

	private int unread;

	public Room(String rid, String name, String type) {
		this.rid = rid;
		this.name = name;
	}

	public static Room newInstance(Cursor cursor){
		return new Room(
				cursor.getString(cursor.getColumnIndexOrThrow(RID)),
				cursor.getString(cursor.getColumnIndexOrThrow(NAME)),
				cursor.getString(cursor.getColumnIndexOrThrow(TYPE))
		);
	}

	public ContentValues getContentValues(){
		ContentValues values = new ContentValues();
		values.put(RID, rid);
		values.put(NAME, name);
		values.put(TYPE, type);
		return values;
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	@Override
	public String getId() {
		return rid;
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	/**
	 * 額外的資訊來自Message，不會存取在chat的DB上
	 * @param cursor
	 */
	public void setInfo(Cursor cursor){
		setLastMsg(Message.newInstance(cursor));
		int unread = cursor.getInt(cursor.getColumnIndex(UNREAD));
		String date = cursor.getString(cursor.getColumnIndex(DATE)) == null ? "" : cursor.getString(cursor.getColumnIndex(DATE));
		setUnread(unread);
		setDate(date);
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Room) {
			Room room = (Room) o;
			return this.rid.equals(room.rid)
					&& this.name.equals(room.name) && this.name.equals(room.name)
					&& CollectionUtils.isEqualCollection(this.members, room.members);
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int PRIME = 121;
		int result = 1;
		result = PRIME * result + (PRIME * getRid().hashCode()) + getName().hashCode();
		return result;
	}
}
