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
import com.avengers.publicim.data.entities.Member;
import com.avengers.publicim.data.entities.Room;

import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/3/15.
 */
public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>{
	private Handler mHandler = new Handler();
	private final LayoutInflater mLayoutInflater;
	private final Context mContext;
	private List<Room> mRooms;

	public ChatListAdapter(Context context, List<Room> objects){
		mRooms = objects;
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public ChatListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ChatListViewHolder(mLayoutInflater.inflate(R.layout.view_chat_list_item, parent, false));
	}

	@Override
	public void onBindViewHolder(ChatListViewHolder holder, int position) {
		holder.mRid = mRooms.get(position).getRid();
		int resId = mRooms.get(position).getType().equals(Room.Type.SINGLE) ?
				R.drawable.ic_person_black_48dp : R.drawable.ic_group_black_48dp;
		holder.mIcon.setImageResource(resId);
		String title = "";
		if(mRooms.get(position).getType().equals(Room.Type.SINGLE)){
			for (Member member : mRooms.get(position).getMembers()) {
				if(!member.getUser().equals(IMApplication.getUser().getName())){
					title = member.getUser();
				}
			}
		}else{
			title = mRooms.get(position).getName();
		}
		holder.mTitle.setText(title);
		holder.mDate.setText(mRooms.get(position).getDate());
		holder.mContent.setText(mRooms.get(position).getLastMsg().getContent());
		holder.mUnread.setText(String.valueOf(mRooms.get(position).getUnread()));
		int visible = mRooms.get(position).getUnread() == 0 ? View.GONE : View.VISIBLE;
		holder.mUnread.setVisibility(visible);
	}

	@Override
	public int getItemCount() {
		return mRooms == null ? 0 : mRooms.size();
	}

	public Room getItem(int position){
		return mRooms.get(position);
	}

	public void refresh(){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				notifyDataSetChanged();
			}
		});
	}

	public void update(List<Room> objects){
		mRooms = objects;
	}

	public static class ChatListViewHolder extends RecyclerView.ViewHolder {
		String mRid;
		ImageView mIcon;
		TextView mTitle;
		TextView mUnread;
		TextView mDate;
		TextView mContent;

		ChatListViewHolder(View view) {
			super(view);
			mIcon = (ImageView)view.findViewById(R.id.icon);
			mTitle = (TextView)view.findViewById(R.id.title);
			mUnread = (TextView)view.findViewById(R.id.unread);
			mDate = (TextView)view.findViewById(R.id.datetime);
			mContent = (TextView)view.findViewById(R.id.content);
		}

		public String getmRid() {
			return mRid;
		}

		public void setmRid(String mRid) {
			this.mRid = mRid;
		}

		public TextView getmContent() {
			return mContent;
		}

		public void setmContent(TextView mContent) {
			this.mContent = mContent;
		}

		public TextView getmDate() {
			return mDate;
		}

		public void setmDate(TextView mDate) {
			this.mDate = mDate;
		}

		public ImageView getmIcon() {
			return mIcon;
		}

		public void setmIcon(ImageView mIcon) {
			this.mIcon = mIcon;
		}

		public TextView getmTitle() {
			return mTitle;
		}

		public void setmTitle(TextView mTitle) {
			this.mTitle = mTitle;
		}

		public TextView getmUnread() {
			return mUnread;
		}

		public void setmUnread(TextView mUnread) {
			this.mUnread = mUnread;
		}
	}

}
