package com.avengers.publicim.data.Manager;

import android.content.Context;

import com.avengers.publicim.data.entities.RosterEntry;
import com.avengers.publicim.data.entities.User;
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
	public List<RosterEntry> getList() {
		return mList;
	}

	@Override
	public RosterEntry getItem(String value) {
		for (RosterEntry entry : mList) {
			if(entry.getUser().getName().equals(value)){
				return entry;
			}
		}
		return null;
	}

	@Override
	public void setList(List<RosterEntry> list) {
		mList = list;
	}

	@Override
	public boolean contains(RosterEntry item) {
		return contains(item.getUser());
	}

	public boolean contains(User user) {
		for(RosterEntry entry : mList){
			if(entry.getUser().equals(user)){
				return true;
			}
		}
		return false;
	}

	/**
	 * 不完全比對，只比對名稱
	 * @param value
	 * @return
	 */
	@Override
	public boolean contains(String value) {
		for(RosterEntry entry : mList){
			if(entry.getUser().getName().equals(value)){
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
		setList(mDB.getLocalRoster());
		for (RosterListener listener : mListeners){
			listener.onRosterUpdate();
		}
	}
}
