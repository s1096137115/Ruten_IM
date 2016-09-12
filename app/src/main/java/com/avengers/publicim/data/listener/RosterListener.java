package com.avengers.publicim.data.listener;

import com.avengers.publicim.data.event.ServiceEvent;

/**
 * Created by D-IT-MAX2 on 2016/5/10.
 */
public interface RosterListener extends Listener{

	void onRosterUpdate(ServiceEvent event);
}
