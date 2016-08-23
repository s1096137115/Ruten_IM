package com.avengers.publicim.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avengers.publicim.R;
import com.avengers.publicim.conponent.DbHelper;
import com.avengers.publicim.conponent.IMApplication;
import com.avengers.publicim.data.entities.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/3/15.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private final LayoutInflater mLayoutInflater;
	private final Context mContext;
	private List<Message> mMessages = new ArrayList<>();
	private Handler mHandler = new Handler();

	/**
	 * 发送的消息
	 */
	private static final int TYPE_SEND = 0;
	/**
	 * 收到的消息
	 */
	private static final int TYPE_RECEIVE = 1;

	public ChatAdapter(Context context, List<Message> objects) {
		mMessages = objects;
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		RecyclerView.ViewHolder holder = null;
		switch (viewType) {
			case TYPE_RECEIVE:
				holder = new TheOtherViewHolder(mLayoutInflater.inflate(R.layout.view_msg_item_left, parent, false));
				break;
			case TYPE_SEND:
				holder = new SelfHolder(mLayoutInflater.inflate(R.layout.view_msg_item_right, parent, false));
				break;
		}
		return holder;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof TheOtherViewHolder) {
			((TheOtherViewHolder) holder).mID.setText(mMessages.get(position).getFrom().getName());
			if(mMessages.get(position).getRid().isEmpty()){
				((TheOtherViewHolder) holder).mID.setVisibility(View.GONE);
			}else{
				((TheOtherViewHolder) holder).mID.setVisibility(View.VISIBLE);
			}
			((TheOtherViewHolder) holder).mContent.setText(mMessages.get(position).getContent());
			String eventTime = mMessages.get(position).getDate().replace(" ","\n");
			((TheOtherViewHolder) holder).mDatetime.setText(eventTime);
		} else if (holder instanceof SelfHolder) {
			((SelfHolder) holder).mContent.setText(mMessages.get(position).getContent());
			String eventTime = mMessages.get(position).getDate().replace(" ","\n");
			((SelfHolder) holder).mDatetime.setText(eventTime);
		}
	}

	@Override
	public int getItemCount() {
		return mMessages == null ? 0 : mMessages.size();
	}

	@Override
	public int getItemViewType(int position) {
		String from = mMessages.get(position).getFrom().getName();
		String name = IMApplication.getUser().getName();
		return mMessages.get(position).getFrom().getName().equals(IMApplication.getUser().getName())
				? TYPE_SEND : TYPE_RECEIVE;
	}

	public void refresh(){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				notifyDataSetChanged();
			}
		});
	}

	public void update(List<Message> objects){
		mMessages = objects;
	}

	public List<Message> getData(){
		return mMessages;
	}

	public boolean hasUnread(){
		for (Message message : mMessages){
			if(message.getRead() == DbHelper.IntBoolean.FALSE){
				return true;
			}
		}
		return false;
	}

	public static class TheOtherViewHolder extends RecyclerView.ViewHolder {
		ImageView mImageView;
		TextView mID;
		TextView mContent;
		TextView mDatetime;

		TheOtherViewHolder(View view) {
			super(view);
			mImageView = (ImageView)view.findViewById(R.id.icon);
			mID = (TextView)view.findViewById(R.id.id);
			mContent = (TextView)view.findViewById(R.id.msg);
			mDatetime = (TextView)view.findViewById(R.id.datetime);
		}
	}

	public static class SelfHolder extends RecyclerView.ViewHolder {
		TextView mContent;
		TextView mDatetime;

		SelfHolder(View view) {
			super(view);
			mContent = (TextView)view.findViewById(R.id.msg);
			mDatetime = (TextView)view.findViewById(R.id.datetime);
		}
	}
}
