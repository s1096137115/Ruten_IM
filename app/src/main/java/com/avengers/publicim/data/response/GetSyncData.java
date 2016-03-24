package com.avengers.publicim.data.response;

import com.avengers.publicim.data.entities.RosterEntry;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/3/15.
 */
public class GetSyncData{
	@SerializedName("action")
	private String action;

	@SerializedName("error")
	private String error;

	@SerializedName("roster")
	private List<RosterEntry> rosterEntries;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public List<RosterEntry> getRosterEntries() {
		return rosterEntries;
	}

	public void setRosterEntries(List<RosterEntry> rosterEntries) {
		this.rosterEntries = rosterEntries;
	}
}
