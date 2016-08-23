package com.avengers.publicim.data.Manager;

import android.content.Context;
import android.database.Cursor;

import com.avengers.publicim.data.callback.ChatListener;
import com.avengers.publicim.data.entities.Chat;

import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/6/22.
 */
public class ChatManager extends BaseManager<Chat, ChatListener> {

	public ChatManager(Context context) {
		super(context);
	}

	@Override
	public List<Chat> getList() {
		return mList;
	}

	@Override
	public Chat getItem(String cid) {
		for(Chat chat : mList){
			if(chat.getCid().equals(cid)){
				return chat;
			}
		}
		return null;
	}

	@Override
	public void setList(List<Chat> list) {
		mList = list;
	}

	public void setList(Cursor cursor){
		mList.clear();
		while(cursor.moveToNext()){
			Chat chat = Chat.newInstance(cursor);
			chat.setInfo(cursor);
			mList.add(chat);
		}
	}

	@Override
	public boolean contains(Chat item) {
		for(Chat chat : mList){
			if(chat.equals(item)){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean contains(String cid) {
		for(Chat chat : mList){
			if(chat.getCid().equals(cid)){
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
				setList(mDB.getContentOfRooms());
				//UI
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						for(ChatListener listener : mListeners){
							listener.onChatUpdate();
						}
					}
				});
			}
		}).start();
	}
}
