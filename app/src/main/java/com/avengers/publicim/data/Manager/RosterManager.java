package com.avengers.publicim.data.Manager;

import android.content.Context;
import android.support.annotation.NonNull;

import com.avengers.publicim.data.entities.RosterEntry;
import com.avengers.publicim.data.entities.User;
import com.avengers.publicim.data.event.ServiceEvent;
import com.avengers.publicim.data.listener.RosterListener;

import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/6/22.
 */
public class RosterManager extends BaseManager<RosterEntry, RosterListener> {


	public RosterManager(Context context) {
		super(context);
	}

	@Override
	public List<RosterEntry> getList(@NonNull String type) {
		return mDB.getLocalRoster();
	}

	@Override
	public RosterEntry getItem(@NonNull String type, @NonNull String name) {
		for (RosterEntry entry : getList(RosterEntry.Type.ROSTER)) {
			if(entry.getUser().getName().equals(name)){
				return entry;
			}
		}
		return null;
	}

	public boolean contains(User user) {
		for(RosterEntry entry : getList(RosterEntry.Type.ROSTER)){
			if(entry.getUser().equals(user)){
				return true;
			}
		}
		return false;
	}

	public boolean contains(String name) {
		for(RosterEntry entry : getList(RosterEntry.Type.ROSTER)){
			if(entry.getUser().getName().equals(name)){
				return true;
			}
		}
		return false;
	}

	@Override
	public void notify(@NonNull final ServiceEvent event) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				for (RosterListener listener : mListeners){
					event.setListener(listener);
					listener.onRosterUpdate(event);
				}
			}
		});
	}
}
