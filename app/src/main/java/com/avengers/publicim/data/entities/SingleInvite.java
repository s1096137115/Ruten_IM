package com.avengers.publicim.data.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by D-IT-MAX2 on 2016/11/28.
 */

public class SingleInvite extends Invite {
	@SerializedName("to")
	private User to;

	/**
	 * 邀請好友
	 * @param to
	 * @param type
	 */
	public SingleInvite(User to, String type){
		this.to = to;
		this.type = type;
	}

	public User getTo() {
		return to;
	}

	public void setTo(User to) {
		this.to = to;
	}
}
