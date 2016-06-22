package com.avengers.publicim.conponent;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.avengers.publicim.data.Constants;
import com.avengers.publicim.data.action.CreateGroup;
import com.avengers.publicim.data.action.GetRoster;
import com.avengers.publicim.data.action.GetSyncData;
import com.avengers.publicim.data.action.SetRoster;
import com.avengers.publicim.data.entities.Chat;
import com.avengers.publicim.data.entities.Group;
import com.avengers.publicim.data.entities.Invite;
import com.avengers.publicim.data.entities.Message;
import com.avengers.publicim.data.entities.Presence;
import com.avengers.publicim.data.entities.RosterEntry;
import com.avengers.publicim.data.entities.User;
import com.avengers.publicim.utils.GsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.avengers.publicim.conponent.IMApplication.getChatManager;
import static com.avengers.publicim.conponent.IMApplication.getMessageManager;
import static com.avengers.publicim.conponent.IMApplication.getProgress;
import static com.avengers.publicim.conponent.IMApplication.getRosterManager;

public class IMService extends Service {
	private IMBinder mBinder = new IMBinder();
	private Socket mSocket;
	private DbHelper mDB;
	private Handler mHandler = new Handler();
	private Group mGroup;

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

	//---------------------------------------------------------------------------------------------------------------------------------
	//Access DB
	//---------------------------------------------------------------------------------------------------------------------------------

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
		if(getRosterManager().getList().isEmpty()){
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

	//---------------------------------------------------------------------------------------------------------------------------------
	//Send to Socket
	//---------------------------------------------------------------------------------------------------------------------------------

	public void connect(){
		mSocket.connect();
	}

	public void disconnect(){
		mSocket.disconnect();
	}

	public void sendMessage(final Message message){
			final JSONObject obj = GsonUtils.toJSONObject(message);

			mSocket.emit(Constants.EVENT_SEND_MESSAGE, obj, new Ack() {
				@Override
				public void call(Object... args) {
					String errorMessage = (String) args[0];
					String mid = String.valueOf(args[1]);
					message.setMid(mid);
					addMessage(message);
					getChatManager().reload();
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

	public void sendInvite(final Invite invite){
		final JSONObject obj = GsonUtils.toJSONObject(invite);
		mSocket.emit(Constants.EVENT_SEND_INVITE, obj, new Ack() {
			@Override
			public void call(Object... args) {
				String errorMessage = (String) args[0];
				sendSetRoster(invite.getTo());
			}
		});
	}

	/**
	 * 加入好友
	 * @param user
	 */
	public void sendSetRoster(User user){
		SetRoster setRoster = new SetRoster();
		setRoster.setAction("setRoster");
		setRoster.setUser(user);
		setRoster.setRelationship(1);
		final JSONObject obj = GsonUtils.toJSONObject(setRoster);
		mSocket.emit("request", obj);
	}

	public void sendGetSyncData(){
		try {
			JSONObject obj = new JSONObject();
			obj.put("action", Constants.EVENT_GET_SYNC_DATA);
			mSocket.emit("request", obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendGetRoster(){
		try {
			JSONObject obj = new JSONObject();
			obj.put("action", Constants.EVENT_GET_ROSTER);
			mSocket.emit("request", obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendCreateGroup(String name){
		CreateGroup createGroup = new CreateGroup();
		createGroup.setAction(Constants.EVENT_CREATE_GROUP);
		createGroup.setName(name);
		mGroup = new Group("",name);
		final JSONObject obj = GsonUtils.toJSONObject(createGroup);
		mSocket.emit("request", obj);
	}

	//---------------------------------------------------------------------------------------------------------------------------------
	//Receive from Socket
	//---------------------------------------------------------------------------------------------------------------------------------

	public void processGetSyncData(String data){
		GetSyncData getSyncData = GsonUtils.fromJson(data, GetSyncData.class);
//		updateRoster(getSyncData.getRosterEntries());
		for(Message message : getSyncData.getMessages()){
			if(!message.fix()){
				continue;
			}
			if(!getChatManager().contains(message.getChatId())){
				addChat(new Chat(message.getChatId(),message.getChatId()));
			}
			mDB.insertMessage(message);
		}
		getMessageManager().change();
		getChatManager().reload();
	}

	public void processGetRoster(String data){
		GetRoster getRoster = GsonUtils.fromJson(data, GetRoster.class);
		updateRoster(getRoster.getRosterEntries());
	}

	public void processSetRoster(String data){
		SetRoster setRoster = GsonUtils.fromJson(data, SetRoster.class);
		sendGetRoster();
	}

	public void processCreateGroup(String data){
		CreateGroup createGroup = GsonUtils.fromJson(data, CreateGroup.class);
		mGroup.setGid(createGroup.getGid());
		if(createGroup.getError().isEmpty()){
			//todo save to db

			//todo dismiss dialog
			getProgress().dismiss();
//	    	mGroup = null;
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

	//---------------------------------------------------------------------------------------------------------------------------------
	//Socket Listener
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

						sendGetSyncData();
						sendGetRoster();
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
					processGetSyncData(args[0].toString());
				}else if(action.equals(Constants.EVENT_GET_ROSTER)){
					processGetRoster(args[0].toString());
				}else if(action.equals(Constants.EVENT_SET_ROSTER)){
					processSetRoster(args[0].toString());
				}else if(action.equals(Constants.EVENT_CREATE_GROUP)){
					processCreateGroup(args[0].toString());
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
				if(!getChatManager().contains(message.getChatId())){
					Chat chat = new Chat(message.getChatId(),message.getChatId());
					addChat(chat);
				}
				addMessage(message);
				getChatManager().reload();
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
			sendSetRoster(invite.getFrom());
		}
	};

	//---------------------------------------------------------------------------------------------------------------------------------
	//Sub class
	//---------------------------------------------------------------------------------------------------------------------------------

	public class IMBinder extends Binder {
		public IMService getService(){
			return IMService.this;
		}
	}
}
