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
import com.avengers.publicim.data.entities.Message;
import com.avengers.publicim.data.entities.RosterEntry;
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
//		updateChat(new Chat("dog","dog", SystemUtils.getDateTime()));
//		updateChat(new Chat("cat","cat", SystemUtils.getDateTime()));
//		User user = new User("Android-2234", "dog");
//		User user2 = new User("Android-1234", "cat");
//		Message message = new Message(null, user, user2, Message.Type.TEXT, "test1", SystemUtils.getDateTime(),
//				user2.getName(), DbHelper.IntBoolean.FALSE);
//		mDB.insertMessage(message);
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
			Log.d("acho", "ack");

			mSocket.emit(Constants.EVENT_SEND_MESSAGE, obj, new Ack() {
				@Override
				public void call(Object... args) {
					Log.d("acho", "ack");
					String errorMessage = (String) args[0];
					String mid = String.valueOf(args[1]);
					message.setMid(mid);
					addMessage(message);

					Chat chat;
					if(getChatManager().contains(message.getChatId())){
						chat = getChatManager().getChat(message.getChatId());
					}else{
						chat = new Chat(message.getChatId(),message.getChatId(),
								SystemUtils.getDateTime());
					}
					chat.setDate(message.getDate());
					updateChat(chat);
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
			getChatManager().setChats(mDB.getContentOfChats());
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
//		getRosterManager().setEntries(listEntries);
		getRosterManager().setEntries(mDB.getLocalRoster());
	}

	public void updateChat(Chat chat){
		if(getChatManager().contains(chat.getCid())){
			mDB.updateChat(chat);
		}else{
			mDB.insertChat(chat);
		}
		getChatManager().setChats(mDB.getContentOfChats());
	}

	public void updateMessage(Message message){
		mDB.updateMessage(message);
		getMessageManager().change();
	}

	public void updateMessageOfMid(){

	}

	public void updateMessageOfRead(Chat chat, int read){
		mDB.updateMessageofRead(chat, read);
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

	private Emitter.Listener onConnect = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Log.d("acho","connect");
			//todo 取得帳號本身的User
			JSONObject obj = new JSONObject();
			try {
				obj.put("name", "dog");
				obj.put("identify", "Android-1234");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
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
				//todo 根據json的action對應class
				mGSD = GsonUtils.fromJson(args[0].toString(), GetSyncData.class);
				updateRoster(mGSD.getRosterEntries());
				Log.d("acho","after");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private Emitter.Listener onReceiveMessage = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {
			Message message = GsonUtils.fromJson(args[0].toString(), Message.class);
			if(!message.getFrom().equals(IMApplication.getUser())){
				message.fix();
				addMessage(message);
				Chat chat = new Chat(message.getChatId(),message.getChatId(),message.getDate());
				updateChat(chat);
			}
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
