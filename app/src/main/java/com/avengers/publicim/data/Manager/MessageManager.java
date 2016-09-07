package com.avengers.publicim.data.Manager;

import com.avengers.publicim.data.callback.MessageListener;
import com.avengers.publicim.data.entities.Message;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by D-IT-MAX2 on 2016/5/3.
 */
public class MessageManager {
	private Set<MessageListener> mMessageListeners = new CopyOnWriteArraySet<>();

	public void add(Message message){
		for (MessageListener listener : mMessageListeners){
			listener.onMessageAddition(message);
		}
	}

	public void add(List<Message> list){
		for (MessageListener listener : mMessageListeners){
			listener.onMessagesAddition(list);
		}
	}

	public void update(Message message){
		for (MessageListener listener : mMessageListeners){
			listener.onMessageUpdate(message);
		}
	}

	public void update(List<Message> list){
		for (MessageListener listener : mMessageListeners){
			listener.onMessagesUpdate(list);
		}
	}

	public void addMessageListener(MessageListener listener){
		mMessageListeners.add(listener);
	}

	public void removeMessageListener(MessageListener listener){
		mMessageListeners.remove(listener);
	}
}
