package com.avengers.publicim.view;

import com.avengers.publicim.data.entities.Presence;
import com.avengers.publicim.data.entities.RosterEntry;
import com.avengers.publicim.data.entities.User;
import com.avengers.publicim.utils.PreferenceHelper;
import com.avengers.publicim.utils.SystemUtils;

/**
 * Created by D-IT-MAX2 on 2016/11/1.
 */

public class LoginAccount {
	private static LoginAccount instance = null;
	private RosterEntry mEntry;

	public static synchronized LoginAccount getInstance(){
		if(instance == null){
			instance = new LoginAccount();
		}
		return instance;
	}

	public synchronized void clearInstance(){
		instance = null;
	}

	public RosterEntry getAccount(){
		if (mEntry == null) {
			String account = PreferenceHelper.LoginStatus.getAccount();
			if(!account.isEmpty()){
				mEntry = new RosterEntry(new User(SystemUtils.getAndroidID(), account),
						new Presence("","", Presence.Status.ONLINE), 0, "");
			}
		}
		return mEntry;
	}

	public void clearAccount(){
		mEntry = null;
	}

	public User getUser(){
		getAccount();
		return mEntry != null ? mEntry.getUser() : null;
	}

	public Presence getPresence(){
		getAccount();
		return mEntry.getPresence();
	}
}
