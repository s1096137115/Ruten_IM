package com.avengers.publicim.utils;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by D-IT-MAX2 on 2016/5/11.
 */
public class GsonUtils {
	static Gson gson = new Gson();

	public static <T> T fromJson(String json, Class<T> classOfT){
		return gson.fromJson(json, classOfT);
	}

	public static JSONObject toJSONObject(Object src){
		try {
			return new JSONObject(gson.toJson(src));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
