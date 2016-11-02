package com.avengers.publicim.utils;

import android.app.Activity;
import android.content.SharedPreferences;

import com.avengers.publicim.component.IMApplication;
import com.avengers.publicim.view.LoginAccount;

/**
 * Created by D-IT-MAX2 on 2016/8/30.
 */
public class PreferenceHelper {
	private static final String UPDATE_STATUS_SHARED_PREFERENCES = "updateStatus";
	private static final String LOGIN_STATUS_SHARED_PREFERENCES = "loginStatus";

	public static class UpdateStatus {
		private static String PREFERENCES_FIELD_UPDATE_TIME = "updateTime";

		public static long getUpdateTime(){
			if(LoginAccount.getInstance().getUser() != null){
				PREFERENCES_FIELD_UPDATE_TIME = LoginAccount.getInstance().getUser().getName() + "_updateTime";
			}
			SharedPreferences spf = IMApplication.getContext().getSharedPreferences(
					UPDATE_STATUS_SHARED_PREFERENCES, Activity.MODE_PRIVATE);
			return spf.getLong(PREFERENCES_FIELD_UPDATE_TIME, 0L);
		}

		public static void setUpdateTime(long timestamp){
			if(LoginAccount.getInstance().getUser() != null){
				PREFERENCES_FIELD_UPDATE_TIME = LoginAccount.getInstance().getUser().getName() + "_updateTime";
			}
			SharedPreferences.Editor editor = IMApplication.getContext().getSharedPreferences(
					UPDATE_STATUS_SHARED_PREFERENCES, Activity.MODE_PRIVATE).edit();
			editor.putLong(PREFERENCES_FIELD_UPDATE_TIME, timestamp);
			editor.apply();
		}
	}

	public static class LoginStatus {
		private static String PREFERENCES_FIELD_LOGIN_ACCOUNT = "loginAccount";

		public static String getAccount(){
			SharedPreferences spf = IMApplication.getContext().getSharedPreferences(
					LOGIN_STATUS_SHARED_PREFERENCES, Activity.MODE_PRIVATE);
			return spf.getString(PREFERENCES_FIELD_LOGIN_ACCOUNT, "");
		}

		public static void setAccount(String account){
			SharedPreferences.Editor editor = IMApplication.getContext().getSharedPreferences(
					LOGIN_STATUS_SHARED_PREFERENCES, Activity.MODE_PRIVATE).edit();
			editor.putString(PREFERENCES_FIELD_LOGIN_ACCOUNT, account);
			editor.apply();
		}

		public static void clearAccount(){
			SharedPreferences.Editor editor = IMApplication.getContext().getSharedPreferences(
					LOGIN_STATUS_SHARED_PREFERENCES, Activity.MODE_PRIVATE).edit();
			editor.clear();
			editor.apply();
		}
	}
}
