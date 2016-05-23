package com.avengers.publicim.data.Manager;

import android.database.Cursor;

import com.avengers.publicim.data.entities.Chat;
import com.avengers.publicim.data.listener.ChatListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by D-IT-MAX2 on 2016/3/22.
 */
public class ChatManager {
	private List<Chat> chats = new ArrayList<>();

//	private ChatListener mChatListener = null;

	private Set<ChatListener> mChatListeners = new CopyOnWriteArraySet<>();

	public ChatManager(){

	}

	public void setChats(Cursor cursor){
		chats.clear();
		while(cursor.moveToNext()){
			Chat chat = Chat.newInstance(cursor);
			chat.setInfo(cursor);
			chats.add(chat);
		}
		for(ChatListener listener : mChatListeners){
			listener.onChatUpdate();
		}
	}

	public List<Chat> getChats() {
		return chats;
	}

	public void setChats(List<Chat> chats) {
		this.chats = chats;
		for(ChatListener listener : mChatListeners){
			listener.onChatUpdate();
		}
	}

	public Chat getChat(String cid){
		for(Chat chat : chats){
			if(chat.getCid().equals(cid)){
				return chat;
			}
		}
//		return new Chat(cid, cid, SystemUtils.getDateTime());
		return null;
	}

	public boolean contains(String cid){
		for(Chat chat : chats){
			if(chat.getCid().equals(cid)){
				return true;
			}
		}
		return false;
	}

	public void addChatListener(ChatListener listener){
		mChatListeners.add(listener);
	}

	public void removeChatListener(ChatListener listener){
		mChatListeners.remove(listener);
	}

//	public void setChatListener(ChatListener listener){
//		mChatListener = listener;
//	}
//
//	public ChatListener getChatListener(){
//		return mChatListener;
//	}
}
