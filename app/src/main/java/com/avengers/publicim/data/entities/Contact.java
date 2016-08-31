package com.avengers.publicim.data.entities;

import java.io.Serializable;

/**
 * Created by D-IT-MAX2 on 2016/6/21.
 */
public abstract class Contact implements Serializable {
	public class Type{
		public static final String CONTACT = "contact";
		public static final String ROSTER = "roster";
		public static final String GROUP = "group";
	}

	public abstract String getRid();

	public abstract String getName();
}
