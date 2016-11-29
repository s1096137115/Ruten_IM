package com.avengers.publicim.component;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.avengers.publicim.BuildConfig;
import com.avengers.publicim.data.Constants;
import com.avengers.publicim.data.Manager.MessageManager;
import com.avengers.publicim.data.Manager.RoomManager;
import com.avengers.publicim.data.Manager.RosterManager;
import com.avengers.publicim.data.action.CreateRoom;
import com.avengers.publicim.data.action.GetMessage;
import com.avengers.publicim.data.action.GetRoom;
import com.avengers.publicim.data.action.GetRoster;
import com.avengers.publicim.data.action.GetUser;
import com.avengers.publicim.data.action.RegisterPush;
import com.avengers.publicim.data.action.SetRoomMemberRole;
import com.avengers.publicim.data.action.SetRoomOption;
import com.avengers.publicim.data.action.SetRoster;
import com.avengers.publicim.data.entities.Invite;
import com.avengers.publicim.data.entities.Member;
import com.avengers.publicim.data.entities.Message;
import com.avengers.publicim.data.entities.MessageRead;
import com.avengers.publicim.data.entities.Option;
import com.avengers.publicim.data.entities.Presence;
import com.avengers.publicim.data.entities.Room;
import com.avengers.publicim.data.entities.RosterEntry;
import com.avengers.publicim.data.entities.SingleInvite;
import com.avengers.publicim.data.entities.User;
import com.avengers.publicim.data.event.ServiceEvent;
import com.avengers.publicim.data.listener.ServiceListener;
import com.avengers.publicim.utils.GsonUtils;
import com.avengers.publicim.utils.PreferenceHelper;
import com.avengers.publicim.utils.SystemUtils;
import com.avengers.publicim.view.LoginAccount;
import com.google.firebase.iid.FirebaseInstanceId;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class IMService extends Service {
	private static final String TAG = "IMService";
	private IMBinder mBinder = new IMBinder();
	private Socket mSocket;
	private DbHelper mDB;
	private Handler mHandler = new Handler();
	private Set<ServiceListener> mServiceListener = new CopyOnWriteArraySet<>();
	private static RosterManager mRosterManager;
	private static RoomManager mRoomManager;
	private static MessageManager mMessageManager;

	public IMService() {
		//todo 改為MVVM架構
		//todo db、socket功能抽出service
		//todo listener替換為rxbus
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mDB = DbHelper.getInstance(this);
		initSocket();
		initManager();
		setSocketListener(mSocket);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void initSocket(){
		try {
			IO.Options opts = new IO.Options();
			opts.forceNew = true;
//			opts.reconnection = false;
			opts.reconnectionAttempts = 3;
			opts.reconnectionDelay = 5000;
			mSocket = IO.socket(Constants.CHAT_SERVER_URL,opts);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void initManager(){
		mRosterManager = RosterManager.getInstance(IMApplication.getContext());
		mRoomManager = RoomManager.getInstance(IMApplication.getContext());
		mMessageManager = MessageManager.getInstance();
	}

	private void setSocketListener(Socket socket){
		socket.on(Socket.EVENT_CONNECT, onConnect);
		socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
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
		socket.on(Constants.Socket.EVENT_RECEIVE_PRESENCE, onReceivePresence);
		socket.on(Constants.Socket.EVENT_RECEIVE_INVITE, onReceiveInvite);
		socket.on(Constants.Socket.EVENT_RECEIVE_MESSAGE_READ, onReceiveMessageRead);
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
		long value = mDB.insertMessage(message);
		if(value == -1) return;
		ServiceEvent event = new ServiceEvent(ServiceEvent.Event.ADD_MESSAGE);
		mMessageManager.add(message);
		mRoomManager.notify(event);
	}

	public void addMessages(GetMessage getMessage){
		//確保成功寫入後才會callback到listener
		List<Message> insertedList = new ArrayList<>();
		for (Message message : getMessage.getMessages()) {
			long value = mDB.insertMessage(message);
			if(value != -1){
				insertedList.add(message);
			}
		}
		if(getMessage.getMessages().isEmpty()) {
			//type為ABOVE的話當載完再reload
			if(getMessage.getType().equals(GetMessage.Type.ABOVE)){
				ServiceEvent event = new ServiceEvent(ServiceEvent.Event.LOAD_MESSAGE);
				Bundle bundle = new Bundle();
				bundle.putString(Room.RID, getMessage.getRid());
				event.setBundle(bundle);
				mRoomManager.notify(event);
			}
			PreferenceHelper.UpdateStatus.setUpdateTime(System.currentTimeMillis());
		}else if(getMessage.getType().equals(GetMessage.Type.BELOW)){
			mMessageManager.add(insertedList, getMessage.getType());
			ServiceEvent event = new ServiceEvent(ServiceEvent.Event.ADD_MESSAGES);
			mRoomManager.notify(event);
			sendGetMessage(getMessage.getMessages().get(getMessage.getMessages().size() -1).getDate(), null, getMessage.getType());
			//todo 取得加入room之前的對話
		}else if(getMessage.getType().equals(GetMessage.Type.ABOVE)){
			sendGetMessage(getMessage.getMessages().get(getMessage.getMessages().size() -1).getDate(),
					getMessage.getMessages().get(0).getRid(), getMessage.getType());
		}
	}

	public void addRoom(Room room){
		mDB.insertRoom(room);
//		mRoomManager.update(Room.Type.ALL);
	}

	public void addRoster(RosterEntry entry){
		mDB.insertRoster(entry);
		mRosterManager.notify(new ServiceEvent(ServiceEvent.Event.GET_ROSTER));
	}

	public void updateRosterEntry(RosterEntry entry){
		if(mRosterManager.contains(entry.getUser())){
			mDB.updateRoster(entry);
			mRosterManager.notify(new ServiceEvent(ServiceEvent.Event.GET_ROSTER));
		}
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
		List<RosterEntry> listOld = mRosterManager.getList(RosterEntry.Type.ROSTER);
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
		mRosterManager.notify(new ServiceEvent(ServiceEvent.Event.GET_ROSTER));
	}

	public void updateMember(Room room, List<Member> listNew){
		List<Member> listOld = mDB.getMembers(room);
		//在開始比較Member之前要先幫新的Member加上rid
		for (Member member: listNew) {
			member.setRid(room.getRid());
		}
		List<Member>[] list2 = categorize(listNew, listOld);
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

	public void updateRooms(List<Room> listNew){
		//比對時值不得為null
		for (Room room: listNew) {
			if(room.getOption() == null)
			room.setOption(new Option(Option.Mute.TURN_OFF));
		}

		List<Room> listOld = mRoomManager.getList(Room.Type.ALL);
		List<Room>[] list = categorize(listNew,listOld);
		if(list[MODIFY_NEW].isEmpty() && list[MODIFY_OLD].isEmpty()) return;
		for (Room room : list[CHANGE_NEW]) {
			mDB.updateRoom(room);
			//在開始比較Member之前要先幫新的Member加上rid
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
			mDB.deleteMessages(room.getRid());
//			for (ServiceListener listener : mServiceListener) {
//				if(listener.getName().equals("ChatActivity")){
//					ServiceEvent event = new ServiceEvent(ServiceEvent.Event.CLOSE_ACTIVITY);
//					Bundle bundle = new Bundle();
//					bundle.putString(Room.RID, room.getRid());
//					event.setBundle(bundle);
//					listener.onServeiceResponse(event);
//				}
//			}
		}
		mRoomManager.notify(new ServiceEvent(ServiceEvent.Event.GET_ROOM));
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
						return ((Room) a).getRid().equals(((Room) b).getRid());
					}else if(b instanceof Member) {
						return ((Member) a).getUser().equals(((Member) b).getUser());
					}else{
						return false;
					}
				}
			});
		}
		return predicates;
	}

	public void updateMessage(Message message){
		mDB.updateMessage(message);
		ServiceEvent event = new ServiceEvent(ServiceEvent.Event.UPDATE_MESSAGE);
//		Bundle bundle = new Bundle();
//		bundle.putString(Room.RID, message.getRid());
//		bundle.putSerializable(ServiceEvent.Item.MESSAGE, message);
//		event.setBundle(bundle);
		mRoomManager.notify(event);
		mMessageManager.update(message);
	}

//	public void updateMessageOfRead(Room room, int read){
//		mDB.updateMessageofRead(room, read);
//	}

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

	//---------------------------------------------------------------------------------------------------------------------------------
	//Send to Socket
	//---------------------------------------------------------------------------------------------------------------------------------

	public void connect(){
		mSocket.connect();
	}

	public boolean connected(){
		return mSocket.connected();
	}

	public void disconnect(){
		mSocket.disconnect();
	}

	public void sendMessage(final Message message){
		final JSONObject obj = GsonUtils.toJSONObject(message);
		mSocket.emit(Constants.Socket.EVENT_SEND_MESSAGE, obj, new Ack() {
			@Override
			public void call(Object... args) {
				if(args[0] != null){
					String errorMessage = args[0].toString();
					Log.d(TAG, errorMessage);
					return;
				}
				String mid = args[1].toString();
				long date = Long.valueOf(args[2].toString());
				message.setMid(mid);
				message.setDate(date);
				message.setFrom(LoginAccount.getInstance().getUser());
				addMessage(message);
			}
		});
	}

	public void sendMessageRead(String rid, long date){
		MessageRead messageRead = new MessageRead();
		messageRead.setRid(rid);
		messageRead.setDate(date);
		if(BuildConfig.DEBUG){
			String time = SystemUtils.getDate(date, Constants.Date.LONG);
			Log.d(TAG,time);
		}
		final JSONObject obj = GsonUtils.toJSONObject(messageRead);
		mSocket.emit(Constants.Socket.EVENT_SEND_MESSAGE_READ, obj, new Ack() {
			@Override
			public void call(Object... args) {
				if(args[0] != null){
					String errorMessage = args[0].toString();
					Log.d(TAG, errorMessage);
					return;
				}
			}
		});
	}

	public void sendPresence(Presence presence){
		try {
			final JSONObject obj = GsonUtils.toJSONObject(presence);
			final JSONObject obj2 = GsonUtils.toJSONObject(LoginAccount.getInstance().getUser());
			obj.put("from",obj2);
			mSocket.emit(Constants.Socket.EVENT_SEND_PRESENCE, obj, new Ack() {
				@Override
				public void call(Object... args) {
					if(args[0] != null){
						String errorMessage = args[0].toString();
						Log.d(TAG, errorMessage);
						return;
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 邀請好友、邀請至聊天室
	 * @param invite
	 */
	public void sendInvite(final Invite invite){
		final JSONObject obj = GsonUtils.toJSONObject(invite);
		mSocket.emit(Constants.Socket.EVENT_SEND_INVITE, obj, new Ack() {
			@Override
			public void call(Object... args) {
				if(args[0] != null){
					String errorMessage = args[0].toString();
					Log.d(TAG, errorMessage);
					Log.d(TAG, "sendInvite callback");
					return;
				}
				//由於callback給的資料太少，所以決定改由onReceiveInvite取得資料
//				if(invite.getType().equals(Invite.Type.FRIEND)){
//					addRoster(new RosterEntry(invite.getFrom(), invite.getPresence(), invite.getRelationship()));
//					Room room = GsonUtils.fromJson(args[1].toString(), Room.class);
//					addRoom(room);
//				}
				//群組邀請不會有callback，所以這裡只有邀請好友會觸發
				for (ServiceListener listener : mServiceListener) {
					listener.onServeiceResponse(new ServiceEvent(ServiceEvent.Event.CLOSE_DIALOG));
				}
			}
		});
	}

	/**
	 * 加入聊天室、離開聊天室、踢人、設定特定對象黑名單
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
		mSocket.emit(Constants.Socket.EVENT_REQUEST, obj);
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
		mSocket.emit(Constants.Socket.EVENT_REQUEST, obj);
	}

	/**
	 * 更改房間屬性
	 */
	public void sendSetRoom(){

	}

	public void sendGetUser(String type, String key){
		GetUser getUser = new GetUser();
		getUser.setAction(Constants.Socket.EVENT_GET_USER);
		getUser.setType(type);
		getUser.setKey(key);
		final JSONObject obj = GsonUtils.toJSONObject(getUser);
		mSocket.emit(Constants.Socket.EVENT_REQUEST, obj);
	}

	public void sendGetRoster(){
		try {
			JSONObject obj = new JSONObject();
			obj.put("action", Constants.Socket.EVENT_GET_ROSTER);
			mSocket.emit(Constants.Socket.EVENT_REQUEST, obj);
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
		mSocket.emit(Constants.Socket.EVENT_REQUEST, obj);
	}

	public void sendGetRoom(){
		try {
			JSONObject obj = new JSONObject();
			obj.put("action", Constants.Socket.EVENT_GET_ROOM);
			mSocket.emit(Constants.Socket.EVENT_REQUEST, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param date timestamp
	 * @param rid
	 */
	public void sendGetMessage(long date, String rid, String type){
		String str = SystemUtils.getDate(date, Constants.Date.LONG);
		GetMessage getMessage = new GetMessage();
		getMessage.setAction(Constants.Socket.EVENT_GET_MESSAGE);
		getMessage.setDate(date);
		getMessage.setRid(rid);
		getMessage.setType(type);
		final JSONObject obj = GsonUtils.toJSONObject(getMessage);
		mSocket.emit(Constants.Socket.EVENT_REQUEST, obj);
	}

	public void sendRegisterPush(boolean register){
		RegisterPush registerPush = new RegisterPush();
		registerPush.setAction(register ? Constants.Socket.EVENT_REGISTER_PUSH : Constants.Socket.EVENT_UNREGISTER_PUSH);
		registerPush.setToken(FirebaseInstanceId.getInstance().getToken());
		registerPush.setSystem(RegisterPush.System.ANDROID);
		final JSONObject obj = GsonUtils.toJSONObject(registerPush);
		mSocket.emit(Constants.Socket.EVENT_REQUEST, obj);
	}

	public void sendSetRoomOption(String rid, int mute){
		SetRoomOption setRoomOption = new SetRoomOption();
		setRoomOption.setAction(Constants.Socket.EVENT_SET_ROOM_OPTION);
		setRoomOption.setRid(rid);
		setRoomOption.setMute(mute);
		final JSONObject obj = GsonUtils.toJSONObject(setRoomOption);
		mSocket.emit(Constants.Socket.EVENT_REQUEST, obj);
	}

	//---------------------------------------------------------------------------------------------------------------------------------
	//Receive from Socket
	//---------------------------------------------------------------------------------------------------------------------------------

	public void receiveSetRoomOption(String data){
		SetRoomOption setRoomOption = GsonUtils.fromJson(data, SetRoomOption.class);
		if(setRoomOption.getError() != null){
			Log.d(TAG, setRoomOption.getError());
		}else{
			Room room = mRoomManager.getItem(Room.Type.ALL ,setRoomOption.getRid());
			room.getOption().setMute(setRoomOption.getMute());
			mDB.updateRoom(room);
			ServiceEvent event = new ServiceEvent(ServiceEvent.Event.GET_ROOM);
			Bundle bundle = new Bundle();
			bundle.putString(Room.RID, setRoomOption.getRid());
			event.setBundle(bundle);
			mRoomManager.notify(event);
		}
	}

	public void receiveRegisterPush(String data){
		RegisterPush registerPush = GsonUtils.fromJson(data, RegisterPush.class);
		if(registerPush.getError() != null){
			Log.d(TAG, registerPush.getError());
		}else{
			if(registerPush.getAction().equals(Constants.Socket.EVENT_REGISTER_PUSH)){
				PreferenceHelper.LoginStatus.setPush(PreferenceHelper.LoginStatus.PUSH_REGISTER);
			}else if(registerPush.getAction().equals(Constants.Socket.EVENT_UNREGISTER_PUSH)){
				PreferenceHelper.LoginStatus.setPush(PreferenceHelper.LoginStatus.PUSH_UNREGISTER);
			}
		}
	}

	public void receiveSetRoomMemberRole(String data){
		SetRoomMemberRole setRoomMemberRole = GsonUtils.fromJson(data, SetRoomMemberRole.class);
		updateMember(setRoomMemberRole.getRoom(), setRoomMemberRole.getRoom().getMembers());
		for (Member member: setRoomMemberRole.getRoom().getMembers()) {
			if(member.getRole().equals(Room.Role.EXIT)
					&& member.getUser().equals(LoginAccount.getInstance().getUser().getName())){
				Room room = mRoomManager.getItem(Room.Type.GROUP, setRoomMemberRole.getRoom().getRid());
				deleteRoom(room);
				for (ServiceListener listener : mServiceListener) {
					ServiceEvent event = new ServiceEvent(ServiceEvent.Event.CLOSE_DIALOG);
					Bundle bundle = new Bundle();
					bundle.putSerializable(Room.class.getSimpleName(), setRoomMemberRole.getRoom());
					event.setBundle(bundle);
					listener.onServeiceResponse(event);
				}
			}
		}
	}

	public void receiveGetRoster(String data){
		GetRoster getRoster = GsonUtils.fromJson(data, GetRoster.class);
		if(TextUtils.isEmpty(getRoster.getError())) updateRoster(getRoster.getRosterEntries());
	}

	public void receiveSetRoster(String data){
		SetRoster setRoster = GsonUtils.fromJson(data, SetRoster.class);
		sendGetRoster();
	}

	public void receiveGetUser(String data){
		GetUser getUser = GsonUtils.fromJson(data, GetUser.class);
		for (ServiceListener listener : mServiceListener) {
			ServiceEvent event = new ServiceEvent(ServiceEvent.Event.GET_USER);
			Bundle bundle = new Bundle();
			bundle.putSerializable(GetUser.class.getSimpleName(), getUser);
			event.setBundle(bundle);
			listener.onServeiceResponse(event);
		}
	}

	public void receiveGetRoom(String data){
		GetRoom getRoom = GsonUtils.fromJson(data, GetRoom.class);
		if(TextUtils.isEmpty(getRoom.getError())) updateRooms(getRoom.getRooms());
	}

	public void receiveCreateRoom(String data){
		CreateRoom createRoom = GsonUtils.fromJson(data, CreateRoom.class);
		if(TextUtils.isEmpty(createRoom.getError())){
			mDB.insertRoom(createRoom.getRoom());
			mDB.insertMember(createRoom.getRoom().getMembers().get(0));
			ServiceEvent event = new ServiceEvent(ServiceEvent.Event.CREATE_ROOM);
			Bundle bundle = new Bundle();
			bundle.putSerializable(Room.class.getSimpleName(), createRoom.getRoom());
			event.setBundle(bundle);
			mRoomManager.notify(event);
//			if(!createRoom.getRoom().getType().equals(Room.Type.SINGLE)){
//				for (ServiceListener listener : mServiceListener) {
//					listener.onServeiceResponse(new ServiceEvent(ServiceEvent.Event.CLOSE_DIALOG));
//				}
//			}
		}
	}

	public void receiveGetMessage(String data){
		GetMessage getMessage = GsonUtils.fromJson(data, GetMessage.class);
		if(TextUtils.isEmpty(getMessage.getError())) addMessages(getMessage);
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
			JSONObject obj = GsonUtils.toJSONObject(LoginAccount.getInstance().getUser());
			try {
				mSocket.emit(Constants.Socket.EVENT_JOIN, obj, new Ack() {
					@Override
					public void call(Object... args) {
						Log.d("acho", "ack");
						//todo handle errorMessage & state
						String errorMessage = (String)args[0];
						Boolean state = (Boolean)args[1];

						sendGetMessage(PreferenceHelper.UpdateStatus.getUpdateTime(), null, GetMessage.Type.BELOW);
						sendGetRoster();
						sendGetRoom();
						sendPresence(LoginAccount.getInstance().getPresence());
						if(PreferenceHelper.LoginStatus.getPush() == PreferenceHelper.LoginStatus.PUSH_NULL){
							sendRegisterPush(true);
						}

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
			Log.d("acho","connectError:");
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(IMApplication.getContext(),
							"connectError", Toast.LENGTH_LONG).show();
				}
			});
		}
	};

	private Emitter.Listener onDisconnect = new Emitter.Listener() {
		@Override
		public void call(Object... args) {
			Log.d("acho","disconnect:");
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(IMApplication.getContext(),
							"disconnect", Toast.LENGTH_LONG).show();
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
				if(action.equals(Constants.Socket.EVENT_GET_ROSTER)){
					receiveGetRoster(args[0].toString());
				}else if(action.equals(Constants.Socket.EVENT_SET_ROSTER)){
					receiveSetRoster(args[0].toString());
				}else if(action.equals(Constants.Socket.EVENT_CREATE_ROOM)){
					receiveCreateRoom(args[0].toString());
				}else if(action.equals(Constants.Socket.EVENT_GET_ROOM)){
					receiveGetRoom(args[0].toString());
				}else if(action.equals(Constants.Socket.EVENT_SET_ROOM_MEMBER_ROLE)){
					receiveSetRoomMemberRole(args[0].toString());
				}else if(action.equals(Constants.Socket.EVENT_GET_USER)){
					receiveGetUser(args[0].toString());
				}else if(action.equals(Constants.Socket.EVENT_GET_MESSAGE)){
					receiveGetMessage(args[0].toString());
				}else if(action.equals(Constants.Socket.EVENT_REGISTER_PUSH) ||
						action.equals(Constants.Socket.EVENT_UNREGISTER_PUSH)){
					receiveRegisterPush(args[0].toString());
				}else if(action.equals(Constants.Socket.EVENT_SET_ROOM_OPTION)){
					receiveSetRoomOption(args[0].toString());
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
			//原device不處理，其他device跟進
			if(message.getFrom().equals(LoginAccount.getInstance().getUser())) return;
			//沒有room的message不處理(本身退出群組時仍會收到系統訊息)
			if(mRoomManager.getItem(Room.Type.ALL, message.getRid()) == null) return;
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
				RosterEntry entry = mRosterManager.getItem(RosterEntry.Type.ROSTER ,from.getName());
				if(entry == null){
					String a = "";
				}
				if(entry.getPresence() == null){
					String a = "";
				}
				if(presence.getStatus() == null){
					String a = "";
				}
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
			SingleInvite invite = GsonUtils.fromJson(args[0].toString(), SingleInvite.class);

			//由於sendInvite的callback給的資料太少
			//所以不論是邀請或被邀請都在這做最後的sync
			if(invite.getType().equals(Invite.Type.FRIEND)){
				sendGetRoster();
				sendGetRoom();
			}else if(invite.getType().equals(Invite.Type.ROOM)){
				sendGetRoom();
			}
		}
	};

	private Emitter.Listener onReceiveMessageRead = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Log.d(TAG, args[0].toString());
			MessageRead messageRead = GsonUtils.fromJson(args[0].toString(), MessageRead.class);
			Member member = new Member();
			member.setUser(messageRead.getFrom().getName());
			member.setRid(messageRead.getRid());
			member.setRead_time(messageRead.getDate());
			mDB.updateMember(member);
			ServiceEvent event = new ServiceEvent(ServiceEvent.Event.GET_ROOM);
			Bundle bundle = new Bundle();
			bundle.putString(Room.RID, messageRead.getRid());
			event.setBundle(bundle);
			mRoomManager.notify(event);
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
