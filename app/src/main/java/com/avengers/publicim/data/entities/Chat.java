package com.avengers.publicim.data.entities;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.Serializable;

/**
 * Created by D-IT-MAX2 on 2016/3/3.
 */
public class Chat implements Serializable {
	public static final String TABLE_NAME = "chat";

	public static final String CID = "cid";

	public static final String TITLE = "title";

	public static final String DATE = "date";

	/**
	 * not stored in DB
	 */
	public static final String UNREAD = "unread";

	public static final String CREATE_SQL =
			"CREATE TABLE " + TABLE_NAME + "("
					+ CID + " VARCHAR(50) PRIMARY KEY, "
					+ TITLE + " VARCHAR(50) "
//					+ DATE + " VARCHAR(30) "
//					+ "FOREIGN KEY (" + Chat.CID + ") REFERENCES " + RosterManager.TABLE_NAME + "(" + User.NAME + ") "
					+ ") ";

	private String cid;

	private String title;

	private String date;

	private Message lastMsg;

	private int unread;

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

	public ContentValues getContentValues(){
		ContentValues cv = new ContentValues();
		cv.put(CID, cid);
		cv.put(TITLE, title);
		return cv;
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Message getLastMsg() {
		return lastMsg;
	}

	public void setLastMsg(Message lastMsg) {
		this.lastMsg = lastMsg;
	}

	public int getUnread() {
		return unread;
	}

	public void setUnread(int unread) {
		this.unread = unread;
	}

	/**
	 * 額外的資訊不會存取在chat的DB上
	 * @param cursor
	 */
	public void setInfo(Cursor cursor){
		setLastMsg(Message.newInstance(cursor));
		int unread = cursor.getInt(cursor.getColumnIndex(UNREAD));
		String date = cursor.getString(cursor.getColumnIndex(DATE)) == null ? "" : cursor.getString(cursor.getColumnIndex(DATE));
		setUnread(unread);
		setDate(date);
	}
}
