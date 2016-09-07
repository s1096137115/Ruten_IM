package com.avengers.publicim.data.callback;

import com.avengers.publicim.data.entities.Message;

import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/5/24.
 */
public interface MessageListener extends Listener{

	void onMessageAddition(Message message);

	void onMessagesAddition(List<Message> list);

	void onMessageUpdate(Message message);

	void onMessagesUpdate(List<Message> list);
}
