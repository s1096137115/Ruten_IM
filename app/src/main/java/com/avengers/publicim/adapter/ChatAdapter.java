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
import com.avengers.publicim.conponent.IMApplication;
import com.avengers.publicim.data.Constants;
import com.avengers.publicim.data.entities.Member;
import com.avengers.publicim.data.entities.Message;
import com.avengers.publicim.data.entities.Room;
import com.avengers.publicim.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/3/15.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private final LayoutInflater mLayoutInflater;
	private final Context mContext;
	private Handler mHandler = new Handler();
	private List<Message> mMessages = new ArrayList<>();
	private Room mRoom;

	/**
	 * 发送的消息
	 */
	private static final int TYPE_SEND = 0;
	/**
	 * 收到的消息
	 */
	private static final int TYPE_RECEIVE = 1;

	public ChatAdapter(Context context, List<Message> objects, Room room) {
		mMessages = objects;
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		mRoom = room;
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
		if(position == 1){
			mMessages.add(1,mMessages.get(1));
		}
		if (holder instanceof TheOtherViewHolder) {
			((TheOtherViewHolder) holder).mID.setText(mMessages.get(position).getFrom().getName());
			if(mMessages.get(position).getRid().isEmpty()){
				((TheOtherViewHolder) holder).mID.setVisibility(View.GONE);
			}else{
				((TheOtherViewHolder) holder).mID.setVisibility(View.VISIBLE);
			}
			((TheOtherViewHolder) holder).mContent.setText(mMessages.get(position).getContext());

			String eventTime = SystemUtils.getDate(mMessages.get(position).getDate(), Constants.SHORT_DATETIME).replace(" ","\n");
			((TheOtherViewHolder) holder).mDatetime.setText(eventTime);
		} else if (holder instanceof SelfHolder) {
			((SelfHolder) holder).mContent.setText(mMessages.get(position).getContext());

			String eventTime = SystemUtils.getDate(mMessages.get(position).getDate(), Constants.SHORT_DATETIME).replace(" ","\n");
			((SelfHolder) holder).mDatetime.setText(eventTime);

			((SelfHolder) holder).mRead.setVisibility(View.GONE);
			int count = 0;
			for (Member member:mRoom.getMembers()) {
				if(member.getUser().equals(IMApplication.getUser().getName())) continue;
				if(member.getRead_time() >= mMessages.get(position).getDate()){
					((SelfHolder) holder).mRead.setVisibility(View.VISIBLE);
					count ++;
					if(mRoom.getType().equals(Room.Type.SINGLE)){
						((SelfHolder) holder).mRead.setText(mContext.getString(R.string.msg_read));
					}else{
						((SelfHolder) holder).mRead.setText(mContext.getString(R.string.msg_read_count, count));
					}
				}
			}
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

	public void update(Room room){
		mRoom = room;
	}

	public void add(Message message){
		mMessages.add(message);
	}

	public List<Message> getData(){
		return mMessages;
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
		TextView mRead;

		SelfHolder(View view) {
			super(view);
			mContent = (TextView)view.findViewById(R.id.msg);
			mDatetime = (TextView)view.findViewById(R.id.datetime);
			mRead = (TextView)view.findViewById(R.id.read);
		}
	}

	public static class SystemHolder extends RecyclerView.ViewHolder {

		public SystemHolder(View view) {
			super(view);
		}
	}
}
