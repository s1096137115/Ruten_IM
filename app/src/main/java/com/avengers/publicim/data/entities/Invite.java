package com.avengers.publicim.data.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by D-IT-MAX2 on 2016/6/7.
 */
public class Invite implements Serializable {

	/**
	 *
	 */
	public static final String TYPE_FRIEND = "friend";
	/**
	 *
	 */
	public static final String TYPE_GROUP = "group";

	/**
	 * 傳送端
	 */
	@SerializedName("from")
	private User from;

	/**
	 * 接收端
	 */
	@SerializedName("to")
	private User to;

	/**
	 * 邀請類別
	 */
	@SerializedName("type")
	private String type;

	/**
	 * 群組資料
	 */
	@SerializedName("group")
	private Group group;

	/**
	 * 群組角色
	 */
	@SerializedName("role")
	private Integer role;

	/**
	 * 好友關係
	 */
	@SerializedName("relationship")
	private Integer relationship;

	public Invite(User from, User to, String type, Group group, Integer role, Integer relationship) {
		this.from = from;
		this.group = group;
		this.relationship = relationship;
		this.role = role;
		this.to = to;
		this.type = type;
	}

	public User getFrom() {
		return from;
	}

	public void setFrom(User from) {
		this.from = from;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	public Integer getRelationship() {
		return relationship;
	}

	public void setRelationship(Integer relationship) {
		this.relationship = relationship;
	}

	public User getTo() {
		return to;
	}

	public void setTo(User to) {
		this.to = to;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
