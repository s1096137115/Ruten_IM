package com.avengers.publicim.data.Manager;

import android.content.Context;
import android.database.Cursor;

import com.avengers.publicim.data.callback.RoomListener;
import com.avengers.publicim.data.entities.Room;

import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/8/23.
 */
public class RoomManager extends BaseManager<Room, RoomListener>  {

	public RoomManager(Context context) {
		super(context);
	}

	@Override
	public List<Room> getList() {
		return mList;
	}

	@Override
	public Room getItem(String rid) {
		for (Room room : mList) {
			if(room.getRid().equals(rid)){
				return room;
			}
		}
		return null;
	}

	@Override
	public void setList(List<Room> list) {
		mList = list;
	}

	@Override
	public void setItem(Room item) {
		for (int i = 0; i < mList.size(); i++) {
			if(mList.get(i).getRid().equals(item.getRid())){
				mList.set(i, item);
			}
		}
	}

	public void setList(Cursor cursor){
		mList.clear();
		while(cursor.moveToNext()){
			Room room = Room.newInstance(cursor);
			room.setInfo(cursor);
			mList.add(room);
		}
	}

	@Override
	public boolean contains(Room item) {
		return mList.contains(item);
	}

	@Override
	public boolean contains(String rid) {
		for(Room room : mList){
			if(room.getRid().equals(rid)){
				return true;
			}
		}
		return false;
	}

	@Override
	public void sort() {

	}

	@Override
	public void reload() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				//background
//				setList(mDB.getRooms());
				setList(mDB.getContentOfRooms());
				for (Room room : mList) {
					room.setMembers(mDB.getMembers(room));
				}
				//UI
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						for (RoomListener listener : mListeners){
							listener.onRoomUpdate();
						}
					}
				});
			}
		}).start();
	}
}