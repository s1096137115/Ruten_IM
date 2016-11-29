package com.avengers.publicim.data.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/11/25.
 */

public class GroupInvite extends Invite {
	@SerializedName("to")
	private List<User> to;

	/**
	 * 邀請群組
	 * @param to
	 * @param type
	 * @param rid
	 */
	public GroupInvite(List<User> to, String type, String rid) {
		this.to = to;
		this.type = type;
		this.rid = rid;
	}

	public List<User> getTo() {
		return to;
	}

	public void setTo(List<User> to) {
		this.to = to;
	}
}
