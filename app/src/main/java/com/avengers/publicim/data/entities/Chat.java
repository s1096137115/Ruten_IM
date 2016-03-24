package com.avengers.publicim.data.entities;

import android.database.Cursor;

/**
 * Created by D-IT-MAX2 on 2016/3/3.
 */
public class Chat {
	public static final String CID = "cid";

	public static final String TITLE = "title";

	private String cid;

	private String title;

	public Chat(String cid, String title) {
		this.cid = cid;
		this.title = title;
	}

	public static Chat newInstance(Cursor cursor){
		return new Chat(
				cursor.getString(cursor.getColumnIndexOrThrow(CID)),
				cursor.getString(cursor.getColumnIndexOrThrow(TITLE))
		);
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
