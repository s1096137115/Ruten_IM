package com.avengers.publicim.data.entities;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by D-IT-MAX2 on 2016/3/18.<p>
 * This is RosterEntry manager
 */
public class RosterManager {
	public static final String TABLE_NAME = "roster";

	public static final String CREATE_SQL =
			"CREATE TABLE " + TABLE_NAME + "("
					+ User.UID + " VARCHAR(30), "
					+ User.NAME + " VARCHAR(50), "
					+ Presence.DESCRIBE + " VARCHAR(30), "
					+ Presence.PHOTO + " VARCHAR(30), "
					+ Presence.STATUS + " VARCHAR(30), "
					+ RosterEntry.RELATIONSHIP + " VARCHAR(30), "
					+ "PRIMARY KEY (" + User.UID + "," + User.NAME + ") "
					+ ") ";

	private Map<User, RosterEntry> entries = new ConcurrentHashMap<>();

//	public List<RosterEntry> entries = new ArrayList<>();

	public List<RosterEntry> getEntries() {
		List<RosterEntry> allEntries = new ArrayList<>();
		for (RosterEntry entry : entries.values()) {
			allEntries.add(entry);
		}
		return allEntries;
	}

	public RosterEntry getEntry(@NonNull User user) {
		if(entries.containsKey(user)){
			return entries.get(user);
		}
		return null;
	}

	public boolean contains(String userName) {
		for (RosterEntry entry : entries.values()) {
			if(entry.getUser().getName().equals(userName)){
				return true;
			}
		}
		return false;
	}

	public void setEntries(List<RosterEntry> listEntries) {
		entries.clear();
		for (RosterEntry entry : listEntries){
			entries.put(entry.getUser(), entry);
		}
	}
}
