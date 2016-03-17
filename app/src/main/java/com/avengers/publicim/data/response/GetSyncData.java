package com.avengers.publicim.data.response;

import com.google.gson.annotations.SerializedName;
import com.avengers.publicim.data.entities.Roster;

import java.util.ArrayList;

/**
 * Created by D-IT-MAX2 on 2016/3/15.
 */
public class GetSyncData{
	@SerializedName("action")
	private String action;

	@SerializedName("error")
	private String error;

	@SerializedName("roster")
	private ArrayList<Roster> rosters = new ArrayList<>();

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

	public ArrayList<Roster> getRosters() {
		return rosters;
	}

	public void setRosters(ArrayList<Roster> rosters) {
		this.rosters = rosters;
	}
}
