package com.avengers.publicim.data.entities;

import android.content.ContentValues;
import android.database.Cursor;

import com.avengers.publicim.conponent.DbHelper;
import com.avengers.publicim.conponent.IMApplication;
import com.avengers.publicim.utils.SystemUtils;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by D-IT-MAX2 on 2016/3/4.
 */
public class Message implements Serializable {
	public static final String TABLE_NAME = "message";

	public static final String MID = "mid";

	public static final String FROM_ID = "from_id";

	public static final String FROM_NAME = "from_name";

	public static final String TO_ID = "to_id";

	public static final String TO_NAME = "to_name";

	public static final String TYPE = "type";

	public static final String CONTENT = "content";

	public static final String DATE = "date";

	public static final String READ = "read";


	public static final String CHAT_ID = "chat_id";

	public static final String CREATE_SQL =
			"CREATE TABLE " + TABLE_NAME + "("
					+ MID + " VARCHAR(30) PRIMARY KEY, "
					+ FROM_ID + " VARCHAR(50), "
					+ FROM_NAME + " VARCHAR(30), "
					+ TO_ID + " VARCHAR(30), "
					+ TO_NAME + " VARCHAR(50), "
					+ TYPE + " VARCHAR(30), "
					+ CONTENT + " TEXT, "
					+ DATE + " VARCHAR(30), "
					+ READ + " INTEGER, "
					+ CHAT_ID + " VARCHAR(50) "
//					+ "FOREIGN KEY (" + CHAT_ID + ") REFERENCES " + Chat.TABLE_NAME + "(" + Chat.CID + ") "
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

	private Integer read;

	private String chatId;

	public Message(String mid, User from, User to, String type, String content,
	               String date, String chatId, int read) {
		this.mid = mid;
		this.from = from;
		this.to = to;
		this.type = type;
		this.content = content;
		this.date = date;
		this.chatId = chatId;
		this.read = read;
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
				cursor.getString(cursor.getColumnIndexOrThrow(Message.CHAT_ID)),
				cursor.getInt(cursor.getColumnIndexOrThrow(Message.READ))
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
		values.put(Message.CHAT_ID, getChatId());
		values.put(Message.READ, getRead());
		return values;
	}

	public boolean fix(){
		if(date == null) date = SystemUtils.getDateTime();
		if(read == null) read = DbHelper.IntBoolean.FALSE;
		if(chatId == null) chatId = from.getName().equals(IMApplication.getUser().getName())
				? to.getName() : from.getName();
		if(from.getName().equals("") || to.getName().equals("") || type.equals("") || content.equals("")){
			return false;
		}
		return true;
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

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	public Integer getRead() {
		return read;
	}

	public void setRead(Integer read) {
		this.read = read;
	}

	public class Type{
		public static final String TEXT = "text";
		public static final String IMAGE = "image";
	}
}
