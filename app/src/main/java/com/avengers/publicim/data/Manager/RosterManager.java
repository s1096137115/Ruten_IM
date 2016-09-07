package com.avengers.publicim.data.Manager;

import android.content.Context;

import com.avengers.publicim.data.callback.RosterListener;
import com.avengers.publicim.data.entities.RosterEntry;
import com.avengers.publicim.data.entities.User;

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
	public RosterEntry getItem(String name) {
		for (RosterEntry entry : mList) {
			if(entry.getUser().getName().equals(name)){
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
	public void setItem(RosterEntry item) {
		for (int i = 0; i < mList.size(); i++) {
			if(mList.get(i).getName().equals(item.getName())){
				mList.set(i, item);
			}
		}
	}

	@Override
	public boolean contains(RosterEntry item) {
		for(RosterEntry entry : mList){
			if(entry.equals(item)){
				return true;
			}
		}
		return false;
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
	 * @param name
	 * @return
	 */
	@Override
	public boolean contains(String name) {
		for(RosterEntry entry : mList){
			if(entry.getUser().getName().equals(name)){
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
		new Thread(new Runnable() {
			@Override
			public void run() {
				//background
				setList(mDB.getLocalRoster());
				//UI
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						for (RosterListener listener : mListeners){
							listener.onRosterUpdate();
						}
					}
				});
			}
		}).start();
	}
}
