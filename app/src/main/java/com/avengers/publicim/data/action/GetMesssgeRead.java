package com.avengers.publicim.data.action;

import com.avengers.publicim.data.entities.Message;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/8/15.
 */
public class GetMesssgeRead {
	@SerializedName("action")
	private String action;

	@SerializedName("error")
	private String error;

	@SerializedName("date")
	private long date;

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

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
}
