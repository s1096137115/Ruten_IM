package com.avengers.publicim.data.entities;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by D-IT-MAX2 on 2016/11/23.
 */

public class Option implements Serializable {

	public static final String MUTE = "mute";

	public class Mute{
		public static final int TURN_OFF = 0;
		public static final int TURN_ON = 1;
	}

	@SerializedName("mute")
	private Integer mute;

	public Option(Integer mute){
		this.mute = mute;
	}

	public static Option newInstance(Cursor cursor){
		return new Option(
				cursor.getInt(cursor.getColumnIndexOrThrow(Option.MUTE))
		);
	}

	public Integer getMute() {
		return mute;
	}

	public void setMute(Integer mute) {
		this.mute = mute;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Option){
			Option option = (Option)o;
			return this.mute.equals(option.mute);
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + (PRIME * getMute().hashCode());
		return result;
	}
}
