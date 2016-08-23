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
import com.avengers.publicim.data.action.CreateRoom;
import com.avengers.publicim.data.action.GetRoom;
import com.avengers.publicim.data.action.GetRoster;
import com.avengers.publicim.data.action.SetRoomMemberRole;
import com.avengers.publicim.data.action.SetRoster;
import com.avengers.publicim.data.callback.ServiceEvent;
import com.avengers.publicim.data.callback.ServiceListener;
import com.avengers.publicim.data.entities.Chat;
import com.avengers.publicim.data.entities.Invite;
import com.avengers.publicim.data.entities.Member;
import com.avengers.publicim.data.entities.Message;
import com.avengers.publicim.data.entities.Presence;
import com.avengers.publicim.data.entities.Room;
import com.avengers.publicim.data.entities.RosterEntry;
import com.avengers.publicim.data.entities.User;
import com.avengers.publicim.utils.GsonUtils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
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
	private static final String TAG = "IMService";
	private IMBinder mBinder = new IMBinder();
	private Socket mSocket;
	private DbHelper mDB;
	private Handler mHandler = new Handler();
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
		socket.on(Constants.Socket.EVENT_RECEIVE_ROOM_MESSAGE, onReceiveMessage);
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
		if(getRosterManager().contains(entry.getUser())){
			mDB.updateRoster(entry);
		}
		getRosterManager().reload();
	}

	//listNew為新名單
	//listOld為舊名單
	private final int MODIFY_NEW = 0; //modifyNew為有修改的新名單
	private final int MODIFY_OLD = 1; //modifyOld為有修改的舊名單
	private final int CHANGE_NEW = 2; //changeNew為需要更新的新名單
	private final int CHANGE_OLD = 3; //changeOld為需要更新的舊名單
	private final int ADD = 4;        //add為需要新增的名單
	private final int REMOVE = 5;     //remove為需要移除的名單

	//主要是利用新舊名單的差集取得有更動的名單
	//再利用使用者名稱的異同區分出增刪或改
	public void updateRoster(List<RosterEntry> listNew){
		List<RosterEntry> listOld = getRosterManager().getList();
		List<RosterEntry>[] list = categorize(listNew,listOld);
		if(list[MODIFY_NEW].isEmpty() && list[MODIFY_OLD].isEmpty()) return;
		for (RosterEntry entry: list[CHANGE_NEW]) {
			mDB.updateRoster(entry);
		}
		for (RosterEntry entry: list[ADD]) {
			mDB.insertRoster(entry);
		}
		for (RosterEntry entry: list[REMOVE]) {
			mDB.deleteRoster(entry);
		}
		getRosterManager().reload();
	}

	public void updateRoom(List<Room> listNew){
		List<Room> listOld = getGroupManager().getList();
		List<Room>[] list = categorize(listNew,listOld);
		if(list[MODIFY_NEW].isEmpty() && list[MODIFY_OLD].isEmpty()) return;
		for (Room room : list[CHANGE_NEW]) {
			mDB.updateRoom(room);
			//在開始比較Member之前要先幫新的Member加上group_id
			for (Member member: room.getMembers()) {
				member.setRid(room.getRid());
			}
			for (Room room2 : list[CHANGE_OLD]) {
				if(room.getRid().equals(room2.getRid())){
					List<Member>[] list2 = categorize(room.getMembers(), room2.getMembers());
					for (Member member: list2[CHANGE_NEW]) {
						mDB.updateMember(member);
					}
					for (Member member: list2[ADD]) {
						mDB.insertMember(member);
					}
					for (Member member: list2[REMOVE]) {
						mDB.deleteMember(member);
					}
				}
			}
		}
		for (Room room : list[ADD]) {
			mDB.insertRoom(room);
			for (Member member: room.getMembers()) {
				member.setRid(room.getRid());
				mDB.insertMember(member);
			}
		}
		for (Room room : list[REMOVE]) {
			for (Member member: room.getMembers()) {
				member.setRid(room.getRid());
				mDB.deleteMember(member);
			}
			mDB.deleteRoom(room);
			mDB.deleteChat(room.getRid());
			mDB.deleteMessages(room.getRid());
		}
		getGroupManager().reload();
		getChatManager().reload();
		getMessageManager().change();
	}

	public <T> List[] categorize(List<T> listNew, List<T> listOld){
		List[] list = new ArrayList[6];
		list[MODIFY_NEW] = (List)CollectionUtils.subtract(listNew,listOld);
		list[MODIFY_OLD] = (List)CollectionUtils.subtract(listOld,listNew);
		list[CHANGE_NEW] = (List)CollectionUtils.select(list[MODIFY_NEW],
				PredicateUtils.anyPredicate(filterCondition(listOld)));
		list[CHANGE_OLD] = (List)CollectionUtils.select(list[MODIFY_OLD],
				PredicateUtils.anyPredicate(filterCondition(listNew)));
		list[ADD] = (List)CollectionUtils.subtract(list[MODIFY_NEW], list[CHANGE_NEW]);
		list[REMOVE] = (List)CollectionUtils.subtract(list[MODIFY_OLD], list[CHANGE_OLD]);
		return list;
	}

	/**
	 * change的過濾條件
	 * @param list
	 * @param <T>
	 * @return
	 */
	private <T> List<Predicate<T>> filterCondition(List<T> list){
		List<Predicate<T>> predicates = new ArrayList<>();
		for (final Object a:list) {
			predicates.add(new Predicate<T>() {
				@Override
				public boolean evaluate(T b) {
					if(b instanceof RosterEntry){
						return ((RosterEntry)a).getName().equals(((RosterEntry)b).getName());
					}else if(b instanceof Room) {
						return ((Room) a).getId().equals(((Room) b).getId());
					}else if(b instanceof Member) {
						boolean c = ((Member) a).getUser().equals(((Member) b).getUser());
						return ((Member) a).getUser().equals(((Member) b).getUser());
					}else{
						return false;
					}
				}
			});
		}
		return predicates;
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

	public void updateMessageOfRead(Room room, int read){
		mDB.updateMessageofRead(room, read);
	}

	public void updateMessageOfMid(){

	}

	public void deleteRoom(Room room){
		if(room == null) return;
		for (Member member: room.getMembers()) {
			mDB.deleteMember(member);
		}
		mDB.deleteRoom(room.getRid());
		mDB.deleteMessages(room.getRid());
	}

	public void deleteRosterEntry(RosterEntry entry){
		if(entry != null) deleteRosterEntry(entry.getName());
	}

	public void deleteRosterEntry(String name){
		mDB.deleteRoster(name);
		mDB.deleteChat(name);
		mDB.deleteMessages(name);
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
		mSocket.emit(Constants.Socket.EVENT_SEND_MESSAGE, obj, new Ack() {
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
			}
		});
	}

	/**
	 * 加入聊天室、離開聊天室、踢人
	 * @param room
	 * @param target
	 * @param role
	 */
	public void sendSetRoomMemberRole(Room room, User target, int role){
		SetRoomMemberRole setRoomMemberRole = new SetRoomMemberRole();
		setRoomMemberRole.setAction(Constants.Socket.EVENT_SET_ROOM_MEMBER_ROLE);
		setRoomMemberRole.setRid(room.getRid());
		setRoomMemberRole.setTarget(target);
		setRoomMemberRole.setRole(role);
		final JSONObject obj = GsonUtils.toJSONObject(setRoomMemberRole);
		mSocket.emit("request", obj);
	}

	/**
	 * 改變Roster狀態(加入好友改為使用{@link #sendInvite(Invite)})
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

	/**
	 * 更改房間屬性
	 */
	public void sendSetRoom(){

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

	public void sendCreateRoom(String name, String type){
		CreateRoom createRoom = new CreateRoom();
		createRoom.setAction(Constants.Socket.EVENT_CREATE_ROOM);
		createRoom.setName(name);
		createRoom.setType(type);
		final JSONObject obj = GsonUtils.toJSONObject(createRoom);
		mSocket.emit("request", obj);
	}

	public void sendGetRoom(){
		try {
			JSONObject obj = new JSONObject();
			obj.put("action", Constants.Socket.EVENT_GET_ROOM);
			mSocket.emit("request", obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//---------------------------------------------------------------------------------------------------------------------------------
	//Receive from Socket
	//---------------------------------------------------------------------------------------------------------------------------------

	public void receiveSetRoomMemberRole(String data){
		SetRoomMemberRole setRoomMemberRole = GsonUtils.fromJson(data, SetRoomMemberRole.class);
		if(setRoomMemberRole.getRole().equals(Room.Role.MEMBER)){
//			mDB.insertRoom(mRoom);
//			mRoom = null;
		}else if(setRoomMemberRole.getRole().equals(Room.Role.EXIT)){
			Room room = getGroupManager().getItem(setRoomMemberRole.getRid());
			deleteRoom(room);
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

	public void receiveGetRoom(String data){
		GetRoom getRoom = GsonUtils.fromJson(data, GetRoom.class);
		updateRoom(getRoom.getRooms());
	}

	public void receiveCreateRoom(String data){
		CreateRoom createRoom = GsonUtils.fromJson(data, CreateRoom.class);
		if(TextUtils.isEmpty(createRoom.getError())){
			mDB.insertRoom(createRoom.getRoom());
			getGroupManager().reload();
			getProgress().dismiss();
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
				Log.d(TAG, args[0].toString());
				JSONObject jsonObj  = new JSONObject(args[0].toString());
				String action = jsonObj.get("action").toString();
				if(action.equals(Constants.Socket.EVENT_GET_SYNC_DATA)){
//					receiveGetSyncData(args[0].toString());
				}else if(action.equals(Constants.Socket.EVENT_GET_ROSTER)){
					receiveGetRoster(args[0].toString());
				}else if(action.equals(Constants.Socket.EVENT_SET_ROSTER)){
					receiveSetRoster(args[0].toString());
				}else if(action.equals(Constants.Socket.EVENT_CREATE_ROOM)){
					receiveCreateRoom(args[0].toString());
				}else if(action.equals(Constants.Socket.EVENT_GET_ROOM)){
					receiveGetRoom(args[0].toString());
				}else if(action.equals(Constants.Socket.EVENT_SET_ROOM_MEMBER_ROLE)){
					receiveSetRoomMemberRole(args[0].toString());
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
			Log.d(TAG, args[0].toString());
			Message message = GsonUtils.fromJson(args[0].toString(), Message.class);
			if(message.getFrom().equals(IMApplication.getUser())) return;
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
				Log.d(TAG, args[0].toString());
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
			Log.d(TAG, args[0].toString());
			Invite invite = GsonUtils.fromJson(args[0].toString(), Invite.class);
			if(!invite.getTo().getName().equals(IMApplication.getUser().getName())) return;

			//目前預設的行為接收到邀請即接受(好友、群組)
			if(invite.getType().equals(Invite.Type.FRIEND)){
				sendSetRoster(invite.getFrom(), RosterEntry.Releationship.FRIEND);
			}else if(invite.getType().equals(Invite.Type.ROOM)){
				//todo add a room

				sendSetRoomMemberRole(invite.getRoom(), invite.getFrom(), Room.Role.MEMBER);
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
