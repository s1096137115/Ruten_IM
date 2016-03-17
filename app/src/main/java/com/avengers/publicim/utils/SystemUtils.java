package com.avengers.publicim.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.avengers.publicim.conponent.RutenApplication;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
		final String appPagName = RutenApplication.getContext().getPackageName();
		final ActivityManager mgr = (ActivityManager) RutenApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
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
		ConnectivityManager cm = (ConnectivityManager) RutenApplication.getContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean network = (activeNetwork != null && activeNetwork.isConnected());
		return network;
	}
}
