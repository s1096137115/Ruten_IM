package com.avengers.publicim.data.entities;

/**
 * 帶有presence的user
 * Created by D-IT-MAX2 on 2016/11/28.
 */
public final class AdvUser extends User {
	private Presence presence;

	public AdvUser(String uid, String name) {
		super(uid, name);
	}

	public Presence getPresence() {
		return presence;
	}

	public void setPresence(Presence presence) {
		this.presence = presence;
	}
}
