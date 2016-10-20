package com.avengers.publicim.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.avengers.publicim.component.IMApplication;
import com.avengers.publicim.data.Constants;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by D-IT-MAX2 on 2016/3/2.
 */
public class SystemUtils {

	/**
	 * 檢查app是否在前景
	 * @return
	 */
	public static boolean isAppOnForeground() {
		final Set<String> activePackages = new HashSet<>();
		final String appPagName = IMApplication.getContext().getPackageName();
		final ActivityManager mgr = (ActivityManager) IMApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
		final List<ActivityManager.RunningAppProcessInfo> processInfos = mgr.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
			if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				activePackages.addAll(Arrays.asList(processInfo.pkgList));
			}
		}
		for(String pkg : activePackages){
			if(pkg.equals(appPagName)){
				return true;
			}
		}
		return false;
	}

	/**
	 * 檢查是否有網路
	 * @return
	 */
	public static boolean hasInternetConnection() {
		ConnectivityManager cm = (ConnectivityManager) IMApplication.getContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean network = (activeNetwork != null && activeNetwork.isConnected());
		return network;
	}

	public static void hideVirtualKeyboard(Activity activity) {
		View view = activity.getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) IMApplication.getContext().
					getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public static String getDate(long timestamp,String type){
		SimpleDateFormat sdf;
		if(type.equals(Constants.Date.WEEK)){//週日期需先檢查是否同年
			sdf = new SimpleDateFormat(Constants.Date.YEAR, Locale.TAIWAN);
			String thisYear = sdf.format(new Date(System.currentTimeMillis()));
			String msgYear = sdf.format(new Date(timestamp));
			if(!thisYear.equals(msgYear)){
				type = Constants.Date.YEAR_WEEK;
			}
		}
		sdf = new SimpleDateFormat(type, Locale.TAIWAN);
		return sdf.format(new Date(timestamp));
	}
}
