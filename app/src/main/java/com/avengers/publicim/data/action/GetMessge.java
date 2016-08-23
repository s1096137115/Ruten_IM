package com.avengers.publicim.data.action;

import com.avengers.publicim.data.entities.Message;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/8/15.
 */
public class GetMessge {
	@SerializedName("action")
	private String action;

	@SerializedName("error")
	private String error;

	@SerializedName("type")
	private String type;

	@SerializedName("date")
	private String date;

	@SerializedName("rid")
	private String rid;

	@SerializedName("messages")
	private List<Message> messages;
}
