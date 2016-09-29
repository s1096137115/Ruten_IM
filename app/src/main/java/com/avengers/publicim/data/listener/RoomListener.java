package com.avengers.publicim.data.listener;

import com.avengers.publicim.data.event.ServiceEvent;

/**
 * Created by D-IT-MAX2 on 2016/8/23.
 */
public interface RoomListener extends Listener{

	void onRoomUpdate(ServiceEvent event);
}
