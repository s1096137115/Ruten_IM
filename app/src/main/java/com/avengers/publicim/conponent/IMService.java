package com.avengers.publicim.conponent;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.avengers.publicim.data.Constants;
import com.avengers.publicim.data.action.CreateGroup;
import com.avengers.publicim.data.action.GetRoster;
import com.avengers.publicim.data.action.GetSyncData;
import com.avengers.publicim.data.action.SetGroupMemberRole;
import com.avengers.publicim.data.action.SetRoster;
import com.avengers.publicim.data.callback.ServiceEvent;
import com.avengers.publicim.data.callback.ServiceListener;
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

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.avengers.publicim.conponent.IMApplication.getChatManager;
import static com.avengers.publicim.conponent.IMApplication.getGroupManager;
import static com.avengers.publicim.conponent.IMApplication.getMessageManager;
import static com.avengers.publicim.conponent.IMApplication.getProgress;
import static com.avengers.publicim.conponent.IMApplication.getRosterManager;

public class IMService extends Service {
	private IMBinder mBinder = new IMBinder();
	private Socket mSocket;
	private DbHelper mDB;
	private Handler mHandler = new Handler();
	private Group mGroup;
	private List<ServiceListener> mServiceListener = new ArrayList<>();

	public IMService() {
	}

	@Override
	public void onCreate() {
		super.onCreate();
		IMApplication app = (IMApplication) getApplication();
		mDB = DbHelper.getInstance(this);
		mSocket = app.getSocket();
		setSocketListener(mSocket);
	}

	private void setSocketListener(Socket socket){
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
		socket.on(Constants.Socket.EVENT_RESPONSE, onResponse);
		socket.on(Constants.Socket.EVENT_RECEIVE_MESSAGE, onReceiveMessage);
		socket.on(Constants.Socket.EVENT_RECEIVE_GROUP_MESSAGE, onReceiveMessage);
		socket.on(Constants.Socket.EVENT_RECEIVE_PRESENCE, onReceivePresence);
		socket.on(Constants.Socket.EVENT_RECEIVE_INVITE, onReceiveInvite);
	}

	public void addListener(ServiceListener listener){
		mServiceListener.add(listener);
	}

	public void removeListener(ServiceListener listener){
		mServiceListener.remove(listener);
	}

	//---------------------------------------------------------------------------------------------------------------------------------
	//Access DB
	//---------------------------------------------------------------------------------------------------------------------------------

	public void addMessage(Message message){
		mDB.insertMessage(message);
		getMessageManager().change();
		getChatManager().reload();
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

	public void updateRosterEntry(RosterEntry entry){
		if(getRosterManager().getList().isEmpty()){
			mDB.insertRoster(entry);
		}else{
			if(getRosterManager().contains(entry.getUser())){
				mDB.updateRoster(entry);
			}else{
				mDB.insertRoster(entry);
			}
		}
		getRosterManager().reload();
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

	public void deleteGroup(Group group){
		if(group != null) deleteGroup(group.getGid());
	}

	public void deleteGroup(String gid){
		mDB.deleteGroup(gid);
		mDB.deleteChat(gid);
		mDB.deleteMessage(gid);
		getGroupManager().reload();
		getChatManager().reload();
		getMessageManager().change();
	}

	public void deleteRosterEntry(RosterEntry entry){
		if(entry != null) deleteRosterEntry(entry.getName());
	}

	public void deleteRosterEntry(String name){
		mDB.deleteRoster(name);
		mDB.deleteChat(name);
		mDB.deleteMessage(name);
		getRosterManager().reload();
		getChatManager().reload();
		getMessageManager().change();
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
		String event = TextUtils.isEmpty(message.getGid()) ? Constants.Socket.EVENT_SEND_MESSAGE : Constants.Socket.EVENT_SEND_GROUP_MESSAGE;

		mSocket.emit(event, obj, new Ack() {
			@Override
			public void call(Object... args) {
				String errorMessage = (String) args[0];
				String mid = String.valueOf(args[1]);
				message.setMid(mid);
				addMessage(message);
			}
		});
	}

	public void sendPresence(Presence presence){
		try {
			final JSONObject obj = GsonUtils.toJSONObject(presence);
			final JSONObject obj2 = GsonUtils.toJSONObject(IMApplication.getUser());
			obj.put("from",obj2);
			mSocket.emit(Constants.Socket.EVENT_SEND_PRESENCE, obj, new Ack() {
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
		mSocket.emit(Constants.Socket.EVENT_SEND_INVITE, obj, new Ack() {
			@Override
			public void call(Object... args) {
				String errorMessage = (String) args[0];
				if(invite.getType().equals(Invite.TYPE_FRIEND)){
					sendSetRoster(invite.getTo(), invite.getRelationship());
				}
			}
		});
	}

	public void sendSetGroupMemberRole(Group group, int role){
		SetGroupMemberRole setGroupMemberRole = new SetGroupMemberRole();
		setGroupMemberRole.setAction(Constants.Socket.EVENT_SET_GROUP_MEMBER_ROLE);
		setGroupMemberRole.setGid(group.getGid());
		setGroupMemberRole.setRole(role);
		final JSONObject obj = GsonUtils.toJSONObject(setGroupMemberRole);
		mSocket.emit("request", obj);
	}

	/**
	 * 加入好友
	 * @param user
	 * @param relationship
	 */
	public void sendSetRoster(User user, int relationship){
		SetRoster setRoster = new SetRoster();
		setRoster.setAction(Constants.Socket.EVENT_SET_ROSTER);
		setRoster.setUser(user);
		setRoster.setRelationship(relationship);
		final JSONObject obj = GsonUtils.toJSONObject(setRoster);
		mSocket.emit("request", obj);
	}

	public void sendGetSyncData(){
		try {
			JSONObject obj = new JSONObject();
			obj.put("action", Constants.Socket.EVENT_GET_SYNC_DATA);
			mSocket.emit("request", obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendGetRoster(){
		try {
			JSONObject obj = new JSONObject();
			obj.put("action", Constants.Socket.EVENT_GET_ROSTER);
			mSocket.emit("request", obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendCreateGroup(String name){
		CreateGroup createGroup = new CreateGroup();
		createGroup.setAction(Constants.Socket.EVENT_CREATE_GROUP);
		createGroup.setName(name);
		mGroup = new Group("",name);
		final JSONObject obj = GsonUtils.toJSONObject(createGroup);
		mSocket.emit("request", obj);
	}

	//---------------------------------------------------------------------------------------------------------------------------------
	//Receive from Socket
	//---------------------------------------------------------------------------------------------------------------------------------

	public void receiveGetSyncData(String data){
		GetSyncData getSyncData = GsonUtils.fromJson(data, GetSyncData.class);
//		updateRoster(getSyncData.getRosterEntries());
		//group的新增必須在message的新增之前
		for(Group group : getSyncData.getGroups()){
			mDB.insertGroup(group);
		}
		getGroupManager().reload();

		//message的新增 & chat的新增
		for(Message message : getSyncData.getMessages()){
			if(!message.fix()) continue;
			if(!getChatManager().contains(message.getChatId())){
				Chat chat = null;
				if(!message.getGid().isEmpty()){
					Group group = getGroupManager().getItem(message.getGid());
					chat = new Chat(message.getChatId(),group.getName(),Chat.TYPE_GROUP);
				}else{
					chat = new Chat(message.getChatId(),message.getChatId(),Chat.TYPE_ROSTER);
				}
//				addChat(chat);
				if(!getChatManager().contains(chat.getCid())){
					mDB.insertChat(chat);
				}
			}
			mDB.insertMessage(message);
		}
		getMessageManager().change();
		getChatManager().reload();
	}

	public void receiveSetGroupMemberRole(String data){
		SetGroupMemberRole setGroupMemberRole = GsonUtils.fromJson(data, SetGroupMemberRole.class);
		if(setGroupMemberRole.getRole().equals(Group.ROLE_MEMBER)){
			mDB.insertGroup(mGroup);
			mGroup = null;
		}else if(setGroupMemberRole.getRole().equals(Group.ROLE_EXIT)){
			Group group = getGroupManager().getItem(setGroupMemberRole.getGid());
			deleteGroup(group);
		}
		for (ServiceListener listener : mServiceListener) {
			listener.onServeiceResponse(new ServiceEvent(ServiceEvent.EVENT_CLOSE_DIALOG, listener));
		}
	}

	public void receiveGetRoster(String data){
		GetRoster getRoster = GsonUtils.fromJson(data, GetRoster.class);
		updateRoster(getRoster.getRosterEntries());
	}

	public void receiveSetRoster(String data){
		SetRoster setRoster = GsonUtils.fromJson(data, SetRoster.class);
		sendGetRoster();
	}

	public void receiveCreateGroup(String data){
		CreateGroup createGroup = GsonUtils.fromJson(data, CreateGroup.class);
		mGroup.setGid(createGroup.getGid());
		if(createGroup.getError().isEmpty()){
			//todo save to db
			mDB.insertGroup(mGroup);
			getGroupManager().reload();
			//todo dismiss dialog
			getProgress().dismiss();
	    	mGroup = null;
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
				mSocket.emit(Constants.Socket.EVENT_JOIN, obj, new Ack() {
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
				if(action.equals(Constants.Socket.EVENT_GET_SYNC_DATA)){
					receiveGetSyncData(args[0].toString());
				}else if(action.equals(Constants.Socket.EVENT_GET_ROSTER)){
					receiveGetRoster(args[0].toString());
				}else if(action.equals(Constants.Socket.EVENT_SET_ROSTER)){
					receiveSetRoster(args[0].toString());
				}else if(action.equals(Constants.Socket.EVENT_CREATE_GROUP)){
					receiveCreateGroup(args[0].toString());
				}else if(action.equals(Constants.Socket.EVENT_SET_GROUP_MEMBER_ROLE)){
					receiveSetGroupMemberRole(args[0].toString());
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
			if(message.getFrom().equals(IMApplication.getUser())) return;

			if(!message.fix()){
				return;
			}
			if(!getChatManager().contains(message.getChatId())){
				Chat chat = null;
				if(!message.getGid().isEmpty()){
					Group group = getGroupManager().getItem(message.getGid());
					chat = new Chat(message.getChatId(),group.getName(),Chat.TYPE_GROUP);
				}else{
					chat = new Chat(message.getChatId(),message.getChatId(),Chat.TYPE_ROSTER);
				}
//				addChat(chat);
				if(!getChatManager().contains(chat.getCid())){
					mDB.insertChat(chat);
				}
			}
			addMessage(message);
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
				RosterEntry entry = getRosterManager().getItem(from.getName());
				entry.getPresence().setStatus(presence.getStatus());
				updateRosterEntry(entry);
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
//			if(invite.getFrom().equals(IMApplication.getUser())) return;

			if(invite.getType().equals(Invite.TYPE_FRIEND)){
				sendSetRoster(invite.getFrom(), RosterEntry.RELATION_INVITEES);
			}else if(invite.getType().equals(Invite.TYPE_GROUP)){
				mGroup = invite.getGroup();
				sendSetGroupMemberRole(invite.getGroup(), Group.ROLE_MEMBER);
			}
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
