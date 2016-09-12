package com.avengers.publicim.data.listener;

import com.avengers.publicim.data.event.ServiceEvent;

/**
 * Created by D-IT-MAX2 on 2016/7/5.
 */
public interface ServiceListener extends Listener {

	void onServeiceResponse(ServiceEvent event);

	String getName();

}
