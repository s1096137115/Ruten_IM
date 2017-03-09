package com.avengers.publicim.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.avengers.publicim.component.IMApplication;
import com.avengers.publicim.data.manager.MessageManager;
import com.avengers.publicim.data.manager.RoomManager;
import com.avengers.publicim.data.manager.RosterManager;

/**
 * Created by D-IT-MAX2 on 2016/10/19.
 */

public abstract class BaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
	protected final LayoutInflater mLayoutInflater;
	protected final Context mContext;
	protected Handler mHandler;
	protected RosterManager mRosterManager;
	protected RoomManager mRoomManager;
	protected MessageManager mMessageManager;

	public BaseAdapter(Context context){
		mHandler = new Handler();
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		initManager();
	}

	private void initManager(){
		mRosterManager = RosterManager.getInstance(IMApplication.getContext());
		mRoomManager = RoomManager.getInstance(IMApplication.getContext());
		mMessageManager = MessageManager.getInstance();
	}
}
