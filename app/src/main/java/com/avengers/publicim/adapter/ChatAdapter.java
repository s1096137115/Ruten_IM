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
import com.avengers.publicim.component.IMApplication;
import com.avengers.publicim.data.Constants;
import com.avengers.publicim.data.entities.Member;
import com.avengers.publicim.data.entities.Message;
import com.avengers.publicim.data.entities.Room;
import com.avengers.publicim.data.entities.User;
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
	private boolean mFullLoad = false;
	private static final int LOAD_SIZE = 3000;

	/**
	 * 发送的消息
	 */
	private static final int TYPE_SEND = 0;
	/**
	 * 收到的消息
	 */
	private static final int TYPE_RECEIVE = 1;
	/**
	 * 系統訊息
	 */
	private static final int TYPE_SYSTEM = 2;
	/**
	 * 時間訊息
	 */
	private static final int TYPE_DATE = 3;

	public ChatAdapter(Context context, List<Message> messages, Room room) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		mRoom = room;
		loadUp(messages);
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
			case TYPE_SYSTEM:
				holder = new SystemHolder(mLayoutInflater.inflate(R.layout.view_msg_system, parent, false));
				break;
			case TYPE_DATE:
				holder = new DateHolder(mLayoutInflater.inflate(R.layout.view_msg_date, parent, false));
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
			((TheOtherViewHolder) holder).mContent.setText(mMessages.get(position).getContext());

			String eventTime = SystemUtils.getDate(mMessages.get(position).getDate(), Constants.Date.SHORT).replace(" ","\n");
			((TheOtherViewHolder) holder).mDatetime.setText(eventTime);
		} else if (holder instanceof SelfHolder) {
			((SelfHolder) holder).mContent.setText(mMessages.get(position).getContext());

			String eventTime = SystemUtils.getDate(mMessages.get(position).getDate(), Constants.Date.SHORT).replace(" ","\n");
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
		} else if (holder instanceof SystemHolder) {
			((SystemHolder) holder).mContent.setText(mMessages.get(position).getContext());

			String eventTime = SystemUtils.getDate(mMessages.get(position).getDate(), Constants.Date.SHORT).replace(" ","\n");
			((SystemHolder) holder).mDatetime.setText(eventTime);
		} else if (holder instanceof DateHolder) {
			((DateHolder) holder).mContent.setText(mMessages.get(position).getContext());
		}
	}

	@Override
	public int getItemCount() {
		return mMessages == null ? 0 : mMessages.size();
	}

	@Override
	public int getItemViewType(int position) {
		if(mMessages.get(position).getType().equals(Message.Type.LOG)){
			return TYPE_SYSTEM;
		}else if(mMessages.get(position).getType().equals(Message.Type.DATE)){
			return TYPE_DATE;
		}else{
			return mMessages.get(position).getFrom().getName().equals(IMApplication.getUser().getName())
					? TYPE_SEND : TYPE_RECEIVE;
		}
	}

	public void refresh(int delayMillis){
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				notifyDataSetChanged();
			}
		}, delayMillis);
	}

	public void update(List<Message> messages){
		mMessages = messages;
	}

	public void update(Room room){
		mRoom = room;
	}

	public boolean isFullLoad() {
		return mFullLoad;
	}

	public void setFullLoad(boolean mFullLoad) {
		this.mFullLoad = mFullLoad;
	}

	public int positionOfUnread(){
		Member myself = null;
		for (Member member: mRoom.getMembers()) {
			if(member.getUser().equals(IMApplication.getUser().getName())){
				myself = member;
				break;
			}
		}
		for (int i = 0; i < mMessages.size(); i++) {
			if(mMessages.get(i).getFrom().getName().equals(IMApplication.getUser().getName())) continue;
			if(mMessages.get(i).getType().equals(Message.Type.DATE)) continue;
			if(myself.getRead_time() <= mMessages.get(i).getDate()) return i;
		}
		return mMessages.size() -1;
	}

	public void loadUp(List<Message> messages){
		if(messages.size() < LOAD_SIZE) mFullLoad = true;
		mMessages.addAll(0, messages);
		if(mMessages.size() == messages.size()){//判斷是否是第一次載入
			addDateMsg(0, messages.size()-1);
		}else{
			addDateMsg(0, messages.size());//更新的範團包含銜接的地方
		}
		if(mFullLoad) addTopDate();
	}

	public void add(Message message){
		mMessages.add(message);
		addDateMsg(mMessages.size()-2, mMessages.size()-1);
		if(mMessages.size() == 1) addTopDate();
	}

	public List<Message> getData(){
		return mMessages;
	}

	/**
	 * 新增時間訊息，參數為區間
	 * @param upper
	 * @param lower
	 */
	private void addDateMsg(int upper, int lower){
		if(mMessages.size() < 2) return;
		for (int i = lower; i > upper; i--) {
			String previous = SystemUtils.getDate(mMessages.get(i-1).getDate(), Constants.Date.WEEK);
			String self = SystemUtils.getDate(mMessages.get(i).getDate(), Constants.Date.WEEK);
			if(!self.equals(previous)){
				mMessages.add(i, new Message("", new User("", "#local"), mRoom.getRid(), Message.Type.DATE, self, 0));
			}
		}
	}

	/**
	 * 新增置頂時間
	 */
	private void addTopDate(){
		if(mMessages.isEmpty()) return;
		String self = SystemUtils.getDate(mMessages.get(0).getDate(), Constants.Date.WEEK);
		mMessages.add(0, new Message("", new User("", "#local"), mRoom.getRid(), Message.Type.DATE, self, 0));
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
		TextView mContent;
		TextView mDatetime;

		public SystemHolder(View view) {
			super(view);
			mContent = (TextView)view.findViewById(R.id.msg);
			mDatetime = (TextView)view.findViewById(R.id.datetime);
		}
	}

	public static class DateHolder extends RecyclerView.ViewHolder {
		TextView mContent;

		public DateHolder(View view) {
			super(view);
			mContent = (TextView)view.findViewById(R.id.msg);
		}
	}
}
