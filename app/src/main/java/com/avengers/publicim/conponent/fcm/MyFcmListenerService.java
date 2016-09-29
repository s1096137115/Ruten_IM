package com.avengers.publicim.conponent.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.avengers.publicim.R;
import com.avengers.publicim.activity.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by D-IT-MAX2 on 2016/7/15.
 */
public class MyFcmListenerService extends FirebaseMessagingService {
	private static final String TAG = "MyFcmListenerService";
	private static final String KEY_DATA = "data";
	public static final int NOTIFICATION_ID = 1;

//	public static GcmPushListener mGcmPushListener = null;

//	public static void setGcmPushListener(GcmPushListener listener){
//		mGcmPushListener = listener;
//	}

	@Override
	public void onMessageReceived(RemoteMessage message){
		String from = message.getFrom();
		Map data = message.getData();


		if (from.startsWith("/topics/")) {
			// message received from some topic.
		} else {
			// normal downstream message.
		}

//		if(mGcmPushListener != null){
//			mGcmPushListener.onUpdate();
//		}

//		String msg = "";
//		try {
			String title = data.get("title").toString();
			String msg = data.get("message").toString();
			String url = data.get("url").toString();
//			if(data.size() == 1){//title msg url
//				//compatible 2.0
//				msg = data.get(KEY_DATA).toString();
//			}else{
//				//fcm
////				JSONObject jsonObj = new JSONObject(data.get(KEY_DATA).toString());
//				msg = jsonObj.get("msg").toString();
//			}
//		} catch (JSONException e) {
//			L.e(getClass(), e);
//		}
		sendNotification(msg);
//		updateBadge();
	}


	/**
	 * Create and show a simple notification containing the received GCM message.
	 *
	 * @param message GCM message received.
	 */
	private void sendNotification(String message) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, intent,
				PendingIntent.FLAG_ONE_SHOT);

		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(this)
						//.setLargeIcon(icon)
						.setDefaults(Notification.DEFAULT_ALL) //default sound` vibrate and light
//						.setSmallIcon(R.drawable.ic_launcher)
						.setContentTitle(getString(R.string.app_name))
						.setAutoCancel(true)
						.setStyle(new NotificationCompat.BigTextStyle()
								.bigText(message))
						.setContentIntent(pendingIntent)
						.setContentText(message);

		NotificationManager notificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(NOTIFICATION_ID , notificationBuilder.build());
	}

//	private void updateBadge(){
//		//更新icon badge
//		DbHelper dbHelper = new DbHelper(this);
//		int pushCount = PreferenceHelper.GCMPush.getPushCount() + 1;
//		ShortcutBadger.applyCount(this, dbHelper.getTotalUnreadCount() + pushCount);
//		PreferenceHelper.GCMPush.setPushCount(pushCount);
//	}
}
