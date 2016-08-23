package com.avengers.publicim.data.entities;

/**
 * Created by D-IT-MAX2 on 2016/6/21.
 */
public abstract class Contact {
	public static final int TYPE_ROSTER = 0;
	public static final int TYPE_GROUP = 1;

	public abstract String getId();

	public abstract String getName();
}
