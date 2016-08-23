package com.avengers.publicim.data.action;

import com.avengers.publicim.data.entities.Message;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/8/15.
 */
public class GetMessgeRead {
	@SerializedName("action")
	private String action;

	@SerializedName("error")
	private String error;

	@SerializedName("date")
	private String date;

	@SerializedName("messages")
	private List<Message> messages;
}
