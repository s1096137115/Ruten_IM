package com.avengers.publicim.data.entities;

import android.content.ContentValues;
import android.database.Cursor;

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

	public static final String RID = "rid";

	public static final String TYPE = "type";

	public static final String CONTEXT = "context";

	public static final String DATE = "date";

	public static final String READ = "read";

	public static final String CREATE_SQL =
			"CREATE TABLE " + TABLE_NAME + "("
					+ MID + " VARCHAR(30) PRIMARY KEY, "
					+ FROM_ID + " VARCHAR(50), "
					+ FROM_NAME + " VARCHAR(50), "
					+ RID + " VARCHAR(50), "
					+ TYPE + " VARCHAR(30), "
					+ CONTEXT + " TEXT, "
					+ DATE + " VARCHAR(30), "
					+ READ + " INTEGER "
					+ ") ";

	public class Type{
		public static final String TEXT = "text";
		public static final String IMAGE = "image";
		public static final String LOG = "log";
	}

	@SerializedName("mid")// 可以讓你的field名稱與API不同
	private String mid;

	@SerializedName("from")
	private User from;

	@SerializedName("rid")
	private String rid;

	@SerializedName("type")
	private String type;

	@SerializedName("context")
	private String context;

	@SerializedName("date")
	private long date;

	@SerializedName("read")
	private Integer read;

	public Message(String mid, User from, String rid, String type, String context,
	               long date, int read) {
		this.mid = mid;
		this.from = from;
		this.rid = rid;
		this.type = type;
		this.context = context;
		this.date = date;
		this.read = read;
	}

	public static Message newInstance(Cursor cursor){
		User from = User.newInstance(
				cursor.getString(cursor.getColumnIndexOrThrow(Message.FROM_ID)),
				cursor.getString(cursor.getColumnIndexOrThrow(Message.FROM_NAME))
		);

		return new Message(
				cursor.getString(cursor.getColumnIndexOrThrow(Message.MID)),
				from,
				cursor.getString(cursor.getColumnIndexOrThrow(Message.RID)),
				cursor.getString(cursor.getColumnIndexOrThrow(Message.TYPE)),
				cursor.getString(cursor.getColumnIndexOrThrow(Message.CONTEXT)),
				cursor.getLong(cursor.getColumnIndexOrThrow(Message.DATE)),
				cursor.getInt(cursor.getColumnIndexOrThrow(Message.READ))
		);
	}

	public ContentValues getContentValues(){
		ContentValues values = new ContentValues();
		values.put(Message.MID, getMid());
		values.put(Message.FROM_ID, getFrom().getUid());
		values.put(Message.FROM_NAME, getFrom().getName());
		values.put(Message.RID, getRid());
		values.put(Message.TYPE, getType());
		values.put(Message.CONTEXT, getContext());
		values.put(Message.DATE, getDate());
		values.put(Message.READ, getRead());
		return values;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String content) {
		this.context = context;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
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

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getRead() {
		return read;
	}

	public void setRead(Integer read) {
		this.read = read;
	}
}
