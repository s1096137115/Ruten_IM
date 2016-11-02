package com.avengers.publicim.component.fcm;

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
			String context = data.get("context").toString();
			String rid = data.get("rid").toString();
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
		sendNotification(title, context);
//		updateBadge();
	}

	private void sendNotification(String title, String context) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, intent,
				PendingIntent.FLAG_ONE_SHOT);

		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(this)
						//.setLargeIcon(icon)
						.setDefaults(NotificationCompat.DEFAULT_ALL) //default sound` vibrate and light
						.setSmallIcon(R.mipmap.ic_launcher)
						.setContentTitle(title)
						.setAutoCancel(true)
						.setCategory(NotificationCompat.CATEGORY_MESSAGE)
						.setPriority(NotificationCompat.PRIORITY_MAX)
						.setStyle(new NotificationCompat.BigTextStyle()
								.bigText(context))
						.setContentIntent(pendingIntent)
						.setContentText(context);

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
