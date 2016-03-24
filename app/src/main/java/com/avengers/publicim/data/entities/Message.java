package com.avengers.publicim.data.entities;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

/**
 * Created by D-IT-MAX2 on 2016/3/4.
 */
public class Message {
	public static final String TABLE_NAME = "message";

	public static final String MID = "mid";

	public static final String FROM_ID = "from_id";

	public static final String FROM_NAME = "from_name";

	public static final String TO_ID = "to_id";

	public static final String TO_NAME = "to_name";

	public static final String TYPE = "type";

	public static final String CONTENT = "content";

	public static final String DATE = "date";

	public static final String CHAT_ID = "chat_id";

	public static final String CREATE_SQL =
			"CREATE TABLE " + TABLE_NAME + "("
					+ MID + " VARCHAR(30) PRIMARY KEY, "
					+ FROM_ID + " VARCHAR(30), "
					+ FROM_NAME + " VARCHAR(30), "
					+ TO_ID + " VARCHAR(30), "
					+ TO_NAME + " VARCHAR(30), "
					+ TYPE + " VARCHAR(30), "
					+ CONTENT + " TEXT, "
					+ DATE + " VARCHAR(30), "
					+ CHAT_ID + " VARCHAR(50), "
					+ "FOREIGN KEY (" + CHAT_ID + ") REFERENCES " + ChatManager.TABLE_NAME + "(" + Chat.CID + ") "
					+ ") ";

	@SerializedName("mid")// 可以讓你的field名稱與API不同
	private String mid;

	@SerializedName("from")
	private User from;

	@SerializedName("to")
	private User to;

	@SerializedName("type")
	private String type;

	@SerializedName("context")
	private String content;

	@SerializedName("date")
	private String date;

	private String chatId;

	public Message(String mid, User from, User to, String type, String content, String date, String chatId) {
		this.mid = mid;
		this.from = from;
		this.to = to;
		this.type = type;
		this.content = content;
		this.date = date;
		this.chatId = chatId;
	}

	public static Message newInstance(Cursor cursor){
		User from = User.newInstance(
				cursor.getString(cursor.getColumnIndexOrThrow(Message.FROM_ID)),
				cursor.getString(cursor.getColumnIndexOrThrow(Message.FROM_NAME))
		);
		User to = User.newInstance(
				cursor.getString(cursor.getColumnIndexOrThrow(Message.TO_ID)),
				cursor.getString(cursor.getColumnIndexOrThrow(Message.TO_NAME))
		);

		return new Message(
				cursor.getString(cursor.getColumnIndexOrThrow(Message.MID)),
				from,
				to,
				cursor.getString(cursor.getColumnIndexOrThrow(Message.TYPE)),
				cursor.getString(cursor.getColumnIndexOrThrow(Message.CONTENT)),
				cursor.getString(cursor.getColumnIndexOrThrow(Message.DATE)),
				cursor.getString(cursor.getColumnIndexOrThrow(Message.CHAT_ID))
		);
	}

	public ContentValues getContentValues(){
		ContentValues values = new ContentValues();
		values.put(Message.MID, getMid());
		values.put(Message.FROM_ID, getFrom().getUid());
		values.put(Message.FROM_NAME, getFrom().getName());
		values.put(Message.TO_ID, getTo().getUid());
		values.put(Message.TO_NAME, getTo().getName());
		values.put(Message.TYPE, getType());
		values.put(Message.CONTENT, getContent());
		values.put(Message.DATE, getDate());
		return values;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public User getFrom() {
		return from;
	}

	public void setFrom(User from) {
		this.from = from;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
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

	public class Type{
		public static final String TEXT = "text";
		public static final String IMAGE = "image";
	}
}
