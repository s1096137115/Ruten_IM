package com.avengers.publicim.data.Manager;

import android.content.Context;

import com.avengers.publicim.data.entities.Group;
import com.avengers.publicim.data.callback.GroupListener;

import java.util.List;


/**
 * Created by D-IT-MAX2 on 2016/6/21.
 */
public class GroupManager extends BaseManager<Group, GroupListener> {

	public GroupManager(Context context) {
		super(context);
	}


	@Override
	public List<Group> getList() {
		return mList;
	}

	@Override
	public Group getItem(String gid) {
		for (Group group: mList) {
			if(group.getGid().equals(gid)){
				return group;
			}
		}
		return null;
	}

	@Override
	public void setList(List<Group> list) {
		mList = list;
	}

	@Override
	public boolean contains(Group item) {
		for(Group group : mList){
			if(group.equals(item)){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean contains(String gid) {
		for(Group group : mList){
			if(group.getGid().equals(gid)){
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
		setList(mDB.getGroups());
		for (GroupListener listener : mListeners){
			listener.onGroupListener();
		}
	}
}
