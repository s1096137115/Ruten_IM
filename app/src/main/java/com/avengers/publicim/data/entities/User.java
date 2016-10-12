package com.avengers.publicim.data.entities;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by D-IT-MAX2 on 2016/3/9.
 */
public class User implements Serializable, Parcelable {
	public static final String UID = "uid";

	public static final String NAME = "name";

	@SerializedName("name")
	private String name;

	@SerializedName("identify")
	private String uid;

	public User(String uid, String name) {
		this.name = name;
		this.uid = uid;
	}

	public static User newInstance(String uid, String name){
		return new User(uid, name);
	}

	public static User newInstance(Cursor cursor){
		return new User(
				cursor.getString(cursor.getColumnIndexOrThrow(User.UID)),
				cursor.getString(cursor.getColumnIndexOrThrow(User.NAME))
		);
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof User) {
			User user = (User) o;
			return this.uid.equals(user.uid) && this.name.equals(user.name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int PRIME = 37;
		int result = 1;
		result = PRIME * result + (PRIME * getUid().hashCode()) + getName().hashCode();
		return result;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.name);
		dest.writeString(this.uid);
	}

	protected User(Parcel in) {
		this.name = in.readString();
		this.uid = in.readString();
	}

	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		@Override
		public User createFromParcel(Parcel source) {
			return new User(source);
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
	};
}
