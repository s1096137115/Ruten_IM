package com.avengers.publicim.conponent;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.avengers.publicim.data.Constants;
import com.avengers.publicim.data.entities.Chat;
import com.avengers.publicim.data.entities.Invite;
import com.avengers.publicim.data.entities.Message;
import com.avengers.publicim.data.entities.Presence;
import com.avengers.publicim.data.entities.RosterEntry;
import com.avengers.publicim.data.entities.User;
import com.avengers.publicim.data.response.GetSyncData;
import com.avengers.publicim.utils.GsonUtils;
import com.avengers.publicim.utils.SystemUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.avengers.publicim.conponent.IMApplication.getChatManager;
import static com.avengers.publicim.conponent.IMApplication.getMessageManager;
import static com.avengers.publicim.conponent.IMApplication.getRosterManager;

public class IMService extends Service {
	private IMBinder mBinder = new IMBinder();
	private Socket mSocket;
	private DbHelper mDB;
	private Handler mHandler = new Handler();

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
		socket.on(Socket.EVENT_RECONNECT, onConnectError);
		socket.on(Socket.EVENT_RECONNECT_ERROR, onConnectError);
		socket.on(Socket.EVENT_RECONNECT_FAILED, onConnectError);
		socket.on(Socket.EVENT_RECONNECT_ATTEMPT, onConnectError);
		socket.on(Socket.EVENT_RECONNECTING, onConnectError);
		socket.on(Constants.EVENT_RESPONSE, onResponse);
		socket.on(Constants.EVENT_RECEIVE_MESSAGE, onReceiveMessage);
		socket.on(Constants.EVENT_RECEIVE_PRESENCE, onReceivePresence);
		socket.on(Constants.EVENT_RECEIVE_INVITE, onReceiveInvite);
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

	public void sendMessage(final Message message){
			final JSONObject obj = GsonUtils.toJSONObject(message);

			mSocket.emit(Constants.EVENT_SEND_MESSAGE, obj, new Ack() {
				@Override
				public void call(Object... args) {
					String errorMessage = (String) args[0];
					String mid = String.valueOf(args[1]);
					message.setMid(mid);


					Chat chat;
					if(getChatManager().contains(message.getChatId())){
						chat = getChatManager().getChat(message.getChatId());
					}else{
						chat = new Chat(message.getChatId(),message.getChatId(),
								SystemUtils.getDateTime());
					}
					chat.setDate(message.getDate());
					addMessage(message);
					updateChat(chat);
				}
			});
	}

	public void sendPresence(Presence presence){
		try {
			final JSONObject obj = GsonUtils.toJSONObject(presence);
			final JSONObject obj2 = GsonUtils.toJSONObject(IMApplication.getUser());
			obj.put("from",obj2);
			mSocket.emit(Constants.EVENT_SEND_PRESENCE, obj, new Ack() {
				@Override
				public void call(Object... args) {
					String errorMessage = (String) args[0];
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendInvite(Invite invite){
		final JSONObject obj = GsonUtils.toJSONObject(invite);
		mSocket.emit(Constants.EVENT_SEND_INVITE, obj, new Ack() {
			@Override
			public void call(Object... args) {
				String errorMessage = (String) args[0];
			}
		});
	}

	public void addMessage(Message message){
		mDB.insertMessage(message);
		getMessageManager().change();
	}

	public void addChat(Chat chat){
		if(!getChatManager().contains(chat.getCid())){
			mDB.insertChat(chat);
			getChatManager().reload();
		}
	}

	public void addRoster(RosterEntry entry){
		mDB.insertRoster(entry);
	}

	public void updateRoster(List<RosterEntry> listEntries){
		if(getRosterManager().isEmpty()){
			for (RosterEntry entry : listEntries) {
				mDB.insertRoster(entry);
			}
		}else{
			for (RosterEntry entry : listEntries) {
				if(getRosterManager().contains(entry.getUser())){
					mDB.updateRoster(entry);
				}else{
					mDB.insertRoster(entry);
				}
			}
		}
		getRosterManager().reload();
	}

	public void updateChat(Chat chat){
		if(getChatManager().contains(chat.getCid())){
			mDB.updateChat(chat);
		}else{
			mDB.insertChat(chat);
		}
		getChatManager().reload();
	}

	public void updateMessage(Message message){
		mDB.updateMessage(message);
		getMessageManager().change();
	}

	public void updateMessageOfRead(Chat chat, int read){
		mDB.updateMessageofRead(chat, read);
	}

	public void updateMessageOfMid(){

	}

	public void sendAddRoster(){

	}

	public void sendRemoveRoster(){

	}

	public void sendSyncData(){
		try {
			JSONObject obj = new JSONObject();
			obj.put("action", Constants.EVENT_GET_SYNC_DATA);
			mSocket.emit("request", obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void processSyncData(String data){
		GetSyncData getSyncData = GsonUtils.fromJson(data, GetSyncData.class);
		updateRoster(getSyncData.getRosterEntries());
		for(Message message : getSyncData.getMessages()){
			if(!message.fix()){
				continue;
			}
			if(!getChatManager().contains(message.getChatId())){
				addChat(new Chat(message.getChatId(),message.getChatId(),message.getDate()));
			}
			mDB.insertMessage(message);
		}
		getMessageManager().change();
		getChatManager().reload();
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

	//---------------------------------------------------------------------------------------------------------------------------------
	//Socket Listener start
	//---------------------------------------------------------------------------------------------------------------------------------

	/**
	 * 登入事件
	 */
	private Emitter.Listener onConnect = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Log.d("acho","connect");
			JSONObject obj = GsonUtils.toJSONObject(IMApplication.getUser());
			try {
				mSocket.emit(Constants.EVENT_JOIN, obj, new Ack() {
					@Override
					public void call(Object... args) {
						Log.d("acho", "ack");
						//todo handle errorMessage & state
						String errorMessage = (String)args[0];
						Boolean state = (Boolean)args[1];

						sendSyncData();
						sendPresence(IMApplication.getPresence());
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 登入錯誤事件
	 */
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

	/**
	 * socket所emit出的request的action所對應的回應事件
	 */
	private Emitter.Listener onResponse = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			try {
				JSONObject jsonObj  = new JSONObject(args[0].toString());
				String action = jsonObj.get("action").toString();
				if(action.equals(Constants.EVENT_GET_SYNC_DATA)){
					processSyncData(args[0].toString());
				}else if(action.equals(Constants.EVENT_CREATE_GROUP)){

				}else if(action.equals(Constants.EVENT_SET_GROUP_MEMBER_ROLE)){

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 接收訊息事件
	 */
	private Emitter.Listener onReceiveMessage = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Message message = GsonUtils.fromJson(args[0].toString(), Message.class);
			if(!message.getFrom().equals(IMApplication.getUser())){
				if(!message.fix()){
					return;
				}
				Chat chat;
				if(!getChatManager().contains(message.getChatId())){
					chat = new Chat(message.getChatId(),message.getChatId(),message.getDate());
					addChat(chat);
				}else{
					chat = getChatManager().getChat(message.getChatId());
				}
				chat.setDate(message.getDate());
				addMessage(message);
				updateChat(chat);
			}
		}
	};

	/**
	 * 接收狀態事件
	 */
	private Emitter.Listener onReceivePresence = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			try {
				JSONObject jsonObj = new JSONObject(args[0].toString());
				String toStr = jsonObj.get("to").toString();
				User to = GsonUtils.fromJson(toStr, User.class);
				String fromStr = jsonObj.get("from").toString();
				User from = GsonUtils.fromJson(fromStr, User.class);
				Presence presence = GsonUtils.fromJson(args[0].toString(), Presence.class);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 接收邀請事件
	 */
	private Emitter.Listener onReceiveInvite = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Invite invite = GsonUtils.fromJson(args[0].toString(), Invite.class);
		}
	};

	//---------------------------------------------------------------------------------------------------------------------------------
	//Socket Listener end
	//---------------------------------------------------------------------------------------------------------------------------------

	public class IMBinder extends Binder {
		public IMService getService(){
			return IMService.this;
		}
	}
}
