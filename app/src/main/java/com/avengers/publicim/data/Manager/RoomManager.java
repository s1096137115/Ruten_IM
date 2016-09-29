package com.avengers.publicim.data.Manager;

import android.content.Context;
import android.support.annotation.NonNull;

import com.avengers.publicim.data.entities.Room;
import com.avengers.publicim.data.event.ServiceEvent;
import com.avengers.publicim.data.listener.RoomListener;

import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/8/23.
 */
public class RoomManager extends BaseManager<Room, RoomListener> {

	public RoomManager(Context context) {
		super(context);
	}

	@Override
	public List<Room> getList(@NonNull String type) {
		List<Room> rooms;
		if(type.equals(Room.Type.ALL)){
			rooms = mDB.getRooms();
		}else if(type.equals(Room.Type.CHAT)){
			rooms = mDB.getChatOfRooms();
		}else if(type.equals(Room.Type.SINGLE)){
			rooms = mDB.getRooms(Room.Type.SINGLE);
		}else if(type.equals(Room.Type.MULTIPLE)){
			rooms = mDB.getRooms(Room.Type.MULTIPLE);
		}else if(type.equals(Room.Type.GROUP)){
			rooms = mDB.getRooms(Room.Type.GROUP);
		}else{
			rooms = mDB.getRooms();
		}
		for (Room room : rooms) {
			room.setMembers(mDB.getMembers(room));
		}
		return rooms;
	}

	@Override
	public Room getItem(@NonNull String type, @NonNull String rid) {
		for (Room room : getList(type)) {
			if(room.getRid().equals(rid)){
				return room;
			}
		}
		return null;
	}

	@Override
	public void notify(@NonNull final ServiceEvent event) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				for (RoomListener listener : mListeners){
					event.setListener(listener);
					listener.onRoomUpdate(event);
				}
			}
		});
	}
}