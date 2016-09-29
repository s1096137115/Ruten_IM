package com.avengers.publicim.data.Manager;

import android.os.Handler;

import com.avengers.publicim.data.entities.Message;
import com.avengers.publicim.data.listener.MessageListener;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by D-IT-MAX2 on 2016/5/3.
 */
public class MessageManager {
	private Set<MessageListener> mMessageListeners = new CopyOnWriteArraySet<>();
	protected Handler mHandler = new Handler();

	public void add(final Message message){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				for (MessageListener listener : mMessageListeners){
					listener.onMessageAddition(message);
				}
			}
		});
	}

	public void add(final List<Message> list){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				for (MessageListener listener : mMessageListeners){
					listener.onMessagesAddition(list);
				}
			}
		});
	}

	public void update(final Message message){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				for (MessageListener listener : mMessageListeners){
					listener.onMessageUpdate(message);
				}
			}
		});
	}

	public void update(final List<Message> list){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				for (MessageListener listener : mMessageListeners){
					listener.onMessagesUpdate(list);
				}
			}
		});
	}

	public void addMessageListener(MessageListener listener){
		mMessageListeners.add(listener);
	}

	public void removeMessageListener(MessageListener listener){
		mMessageListeners.remove(listener);
	}
}
