package com.avengers.publicim.data.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by D-IT-MAX2 on 2016/3/4.
 */
public class Message {
	@SerializedName("mid")// 可以讓你的field名稱與API不同
	private Integer mid;

	@SerializedName("from")
	private User from;

	@SerializedName("to")
	private User to;

	@SerializedName("type")
	private String type;

	@SerializedName("context")
	private String context;

	@SerializedName("date")
	private String date;

	public Message(Integer mid ,User from ,User to ,String type ,String context ,String date) {
		this.mid = mid;
		this.from = from;
		this.to = to;
		this.type = type;
		this.context = context;
		this.date = date;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
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

	public Integer getMid() {
		return mid;
	}

	public void setMid(Integer mid) {
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
