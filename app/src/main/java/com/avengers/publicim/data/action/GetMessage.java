package com.avengers.publicim.data.action;

import com.avengers.publicim.data.entities.Message;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/8/15.
 */
public class GetMessage {
	public class Type{
		public static final String ABOVE = "above";
		public static final String BELOW = "below";
	}

	@SerializedName("action")
	private String action;

	@SerializedName("error")
	private String error;

	@SerializedName("type")
	private String type;

	@SerializedName("date")
	private long date;

	@SerializedName("rid")
	private String rid;

	@SerializedName("messages")
	private List<Message> messages;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
}
