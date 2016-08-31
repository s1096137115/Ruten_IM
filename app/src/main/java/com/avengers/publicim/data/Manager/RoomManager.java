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
	public Room getItem(String gid) {
		for (Room room : mList) {
			if(room.getRid().equals(gid)){
				return room;
			}
		}
		return null;
	}

	@Override
	public void setList(List<Room> list) {
		mList = list;
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
		for(Room room : mList){
			if(room.equals(item)){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean contains(String gid) {
		for(Room room : mList){
			if(room.getRid().equals(gid)){
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