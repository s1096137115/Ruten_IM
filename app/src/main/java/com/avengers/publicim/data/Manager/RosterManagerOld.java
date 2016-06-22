package com.avengers.publicim.data.Manager;

import android.content.Context;

import com.avengers.publicim.conponent.DbHelper;
import com.avengers.publicim.data.entities.RosterEntry;
import com.avengers.publicim.data.entities.User;
import com.avengers.publicim.data.listener.RosterListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by D-IT-MAX2 on 2016/3/18.<p>
 * This is RosterEntry manager
 */
public class RosterManagerOld {
	private DbHelper mDB;

	private Map<User, RosterEntry> entries = new ConcurrentHashMap<>();

	private Set<RosterListener> mRosterListeners = new CopyOnWriteArraySet<>();

	public RosterManagerOld(Context context){
		mDB = DbHelper.getInstance(context);
	}

	public boolean isEmpty(){
		return entries.isEmpty();
	}

	public List<RosterEntry> getEntries() {
		List<RosterEntry> allEntries = new ArrayList<>();
		Set<String> set = new HashSet<>();
		for (RosterEntry entry : entries.values()) {
			//用於過慮相同user name的user
			if(set.add(entry.getUser().getName())){
				allEntries.add(entry);
			}
		}
		sort(allEntries);
		return allEntries;
	}

	/**
	 * 如果使用userName拿的話只會確定拿到相同{@link User#name}但不保證拿到相同{@link User#uid}的entry
	 * @param userName
	 * @return
	 */
	public RosterEntry getEntry(String userName) {
		for (RosterEntry entry : entries.values()) {
			if(entry.getUser().getName().equals(userName)){
				return entry;
			}
		}
		return null;
	}

	public List<RosterEntry> sort(List<RosterEntry> entries){
		Collections.sort(entries,
				new Comparator<RosterEntry>() {
					public int compare(RosterEntry o1, RosterEntry o2) {
						return o1.getUser().getName().compareTo(o2.getUser().getName());
					}
				});
		return entries;
	}

	public boolean contains(String userName) {
		for (RosterEntry entry : entries.values()) {
			if(entry.getUser().getName().equals(userName)){
				return true;
			}
		}
		return false;
	}

	public boolean contains(User user){
		return entries.containsKey(user);
	}

	public void reload(){
		setEntries(mDB.getLocalRoster());
		for (RosterListener listener : mRosterListeners){
			listener.onRosterUpdate();
		}
	}

	public void setEntries(List<RosterEntry> listEntries) {
		entries.clear();
		for (RosterEntry entry : listEntries){
			entries.put(entry.getUser(), entry);
		}
	}

	public void removeRosterListener(RosterListener listener) {
		mRosterListeners.remove(listener);
	}

	public void addRosterListener(RosterListener listener) {
		mRosterListeners.add(listener);
	}
}
