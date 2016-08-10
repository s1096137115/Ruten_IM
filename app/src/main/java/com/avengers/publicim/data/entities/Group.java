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
public class Group implements Serializable, Contact {
	public static final String TABLE_NAME = "group_data";

	public static final String GID = "gid";

	public static final String NAME = "name";

	public static final String ROLE = "role";

	public static final String CREATE_SQL =
			"CREATE TABLE " + TABLE_NAME + "("
					+ GID + " VARCHAR(50) PRIMARY KEY, "
					+ NAME + " VARCHAR(50) "
					+ ") ";

	/**
	 * 黑名單
	 */
	public static final int ROLE_BLACKLIST = -1;
	/**
	 * 離開
	 */
	public static final int ROLE_EXIT = 0;
	/**
	 * 已受邀
	 */
	public static final int ROLE_INVITEES = 1;
	/**
	 * 禁止發言
	 */
	public static final int ROLE_BAN_SPEAK = 2;
	/**
	 * 成員
	 */
	public static final int ROLE_MEMBER = 3;
	/**
	 * 管理者
	 */
	public static final int ROLE_MANAGER = 4;


	@SerializedName("gid")
	private String gid;

	@SerializedName("name")
	private String name;

	@SerializedName("owner")
	private String owner;

	@SerializedName("member")
	private List<Member> members;

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
		return values;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	@Override
	public String getId() {
		return gid;
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

	@Override
	public boolean equals(Object o) {
		if(o instanceof Group) {
			Group group = (Group) o;
			return this.gid.equals(group.gid)
					&& this.name.equals(group.name) && this.name.equals(group.name)
					&& CollectionUtils.isEqualCollection(this.members, group.members);
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int PRIME = 121;
		int result = 1;
		result = PRIME * result + (PRIME * getGid().hashCode()) + getName().hashCode();
		return result;
	}
}
