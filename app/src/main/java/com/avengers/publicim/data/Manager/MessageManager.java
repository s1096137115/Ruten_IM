package com.avengers.publicim.data.Manager;

import com.avengers.publicim.data.callback.MessageListener;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by D-IT-MAX2 on 2016/5/3.
 */
public class MessageManager {
	private Set<MessageListener> mMessageListeners = new CopyOnWriteArraySet<>();

	public void change(){
		for (MessageListener listener : mMessageListeners){
			listener.onMessageUpdate();
		}
	}

	public void addMessageListener(MessageListener listener){
		mMessageListeners.add(listener);
	}

	public void removeMessageListener(MessageListener listener){
		mMessageListeners.remove(listener);
	}
}
