package com.avengers.publicim.data.action;

import com.google.gson.annotations.SerializedName;

/**
 * Created by D-IT-MAX2 on 2016/11/22.
 */
public class RegisterPush {
	public class System{
		public static final String ANDROID = "android";
		public static final String IOS = "ios";
	}

	@SerializedName("action")
	private String action;

	@SerializedName("error")
	private String error;

	@SerializedName("token")
	private String token;

	@SerializedName("system")
	private String system;

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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}
}
