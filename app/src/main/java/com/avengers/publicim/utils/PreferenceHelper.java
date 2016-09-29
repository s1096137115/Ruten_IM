package com.avengers.publicim.utils;

import android.app.Activity;
import android.content.SharedPreferences;

import com.avengers.publicim.conponent.IMApplication;

/**
 * Created by D-IT-MAX2 on 2016/8/30.
 */
public class PreferenceHelper {
	private static final String UPDATE_STATUS_SHARED_PREFERENCES = "updateStatus";

	public static class UpdateStatus {
		private static final String PREFERENCES_FIELD_UPDATE_TIME = "updateTime";

		public static long getUpdateTime(){
			SharedPreferences spf = IMApplication.getContext().getSharedPreferences(
					UPDATE_STATUS_SHARED_PREFERENCES, Activity.MODE_PRIVATE);
			return spf.getLong(PREFERENCES_FIELD_UPDATE_TIME, 0L);
		}

		public static void setUpdateTime(long timestamp){
			SharedPreferences.Editor editor = IMApplication.getContext().getSharedPreferences(
					UPDATE_STATUS_SHARED_PREFERENCES, Activity.MODE_PRIVATE).edit();
			editor.putLong(PREFERENCES_FIELD_UPDATE_TIME, timestamp);
			editor.apply();
		}
	}
}
