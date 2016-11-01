package com.avengers.publicim.component.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by D-IT-MAX2 on 2016/7/15.
 */
public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

	private static final String TAG = "MyInstanceIDLS";

	/**
	 * Called if InstanceID token is updated. This may occur if the security of
	 * the previous token had been compromised. This call is initiated by the
	 * InstanceID provider.
	 */
	// [START refresh_token]
	@Override
	public void onTokenRefresh() {
		// Get updated InstanceID token.
		String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//		RutenApplication.setGcmId(refreshedToken);
		String a = "";
	}
	// [END refresh_token]
}
