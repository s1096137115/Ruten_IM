package com.avengers.publicim.conponent;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.avengers.publicim.data.Constants;
import com.avengers.publicim.data.entities.Message;
import com.avengers.publicim.data.entities.User;
import com.avengers.publicim.data.response.GetSyncData;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class IMService extends Service {
	private IMBinder mBinder = new IMBinder();
	private Socket mSocket;
	private DbHelper mDB;
	private Handler mHandler = new Handler();
	private GetSyncData mGSD;

	public IMService() {
	}

	@Override
	public void onCreate() {
		super.onCreate();
		IMApplication app = (IMApplication) getApplication();
		mDB = DbHelper.getInstance(this);
		mSocket = app.getSocket();
		setListener(mSocket);
	}

	private void setListener(Socket socket){
		socket.on(Socket.EVENT_CONNECT, onConnect);
		socket.on(Socket.EVENT_DISCONNECT, onConnectError);
		socket.on(Socket.EVENT_ERROR, onConnectError);
		socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
		socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
		socket.on(Constants.EVENT_RESPONSE, onResponse);
		socket.on(Constants.EVENT_RECEIVE_MESSAGE, onReceiveMessage);
	}

	public void connect(){
		mSocket.connect();
	}

	public void disconnect(){
		mSocket.disconnect();
	}

//	public ArrayList<RosterEntry> getRosters(){
//		return mGSD.getRosterEntries();
//	}

	public void sendMessage(String msg){
		try {
			User user = new User("Android-2234", "dog");
			User user2 = new User("Android-1234", "cat");

			long now = System.currentTimeMillis();
			SimpleDateFormat sdf = new SimpleDateFormat(Constants.SAVE_DB_SIMPLE_DATETIME_FORMAT);
			String nowTime = sdf.format(now);

			Message message = new Message(null, user, user2, Message.Type.TEXT, msg, nowTime, "001");
			String str = new Gson().toJson(message);
			JSONObject obj = new JSONObject(str);
			Log.d("acho", "ack");

			mSocket.emit(Constants.EVENT_SEND_MESSAGE, obj, new Ack() {
				@Override
				public void call(Object... args) {
					Log.d("acho", "ack");
				}
			});

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//網路斷線時重連時機
//		if(hasInternetConnection() && !isLogin()){//網路可用時 & Server已斷線
//			LoginTask task = new LoginTask(this, mToken);
//			runLoginTask(task,Config.LOGIN_BY_SERVICE);
//		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	//------------------------------------------------------------------------------------------
	//Socket Listener start
	//------------------------------------------------------------------------------------------

	private Emitter.Listener onConnect = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Log.d("acho","connect");
			JSONObject obj = new JSONObject();
			try {
				obj.put("name", "dog");
				obj.put("identify", "Android-1234");
				mSocket.emit(Constants.EVENT_JOIN, obj, new Ack() {
					@Override
					public void call(Object... args) {
						Log.d("acho", "ack");
						String errorMessage = (String)args[0];
						Boolean state = (Boolean)args[1];
						//todo handle errorMessage & state

						JSONObject obj = new JSONObject();
						try {
							obj.put("action", "getRoster");
							mSocket.emit("request", obj);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private Emitter.Listener onConnectError = new Emitter.Listener() {
		@Override
		public void call(Object... args) {
			Log.d("acho","connectError:" + args[0]);
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(IMApplication.getContext(),
							"error", Toast.LENGTH_LONG).show();
				}
			});
		}
	};

	private Emitter.Listener onResponse = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Log.d("acho", "response");
			try {
				Gson gson = new Gson();
				String json = args[0].toString();
				//todo 根據json的action對應class
				mGSD = gson.fromJson(json, GetSyncData.class);
				Log.d("acho","after");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private Emitter.Listener onReceiveMessage = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Log.d("acho","receive");
			try {
				Gson gson = new Gson();
				String json = args[0].toString();
				Message message = gson.fromJson(json, Message.class);
				Log.d("acho","after");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	//------------------------------------------------------------------------------------------
	//Socket Listener end
	//------------------------------------------------------------------------------------------

	public class IMBinder extends Binder {
		public IMService getService(){
			return IMService.this;
		}
	}
}
