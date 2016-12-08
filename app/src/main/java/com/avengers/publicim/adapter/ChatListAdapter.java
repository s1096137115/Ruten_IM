package com.avengers.publicim.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avengers.publicim.R;
import com.avengers.publicim.component.IMApplication;
import com.avengers.publicim.data.Constants;
import com.avengers.publicim.data.entities.Member;
import com.avengers.publicim.data.entities.Room;
import com.avengers.publicim.utils.SystemUtils;
import com.avengers.publicim.view.LoginAccount;

import java.util.List;

import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * Created by D-IT-MAX2 on 2016/3/15.
 */
public class ChatListAdapter extends BaseAdapter{
	private List<Room> mRooms;

	public ChatListAdapter(Context context, List<Room> objects){
		super(context);
		mRooms = objects;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ChatListViewHolder(mLayoutInflater.inflate(R.layout.view_chat_list_item, parent, false));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		((ChatListViewHolder)holder).mRid = mRooms.get(position).getRid();

		String title = "";
		if(mRooms.get(position).getType().equals(Room.Type.SINGLE)){
			for (Member member : mRooms.get(position).getMembers()) {
				if(!member.getUser().equals(LoginAccount.getInstance().getUser().getName())){
					title = member.getUser();
				}
			}
		}else{
			title = mRooms.get(position).getName();
		}

//		int resId = mRooms.get(position).getType().equals(Room.Type.SINGLE) ?
//				R.drawable.ic_person_black_48dp : R.drawable.ic_group_black_48dp;
//		((ChatListViewHolder)holder).mIcon.setImageResource(resId);
		char first = title.charAt(0);
		((ChatListViewHolder) holder).mIcon.setTextAndColor(String.valueOf(first),
				IMApplication.getContext().getResources().getColor(R.color.colorPrimaryDark));

		((ChatListViewHolder)holder).mTitle.setText(title);
		((ChatListViewHolder)holder).mDate.setText(SystemUtils.getDate(mRooms.get(position).getDate(), Constants.Date.WEEK));
		((ChatListViewHolder)holder).mContent.setText(mRooms.get(position).getLastMsg().getContext());
		((ChatListViewHolder)holder).mUnread.setText(String.valueOf(mRooms.get(position).getUnread()));
		int visible = mRooms.get(position).getUnread() == 0 ? View.GONE : View.VISIBLE;
		((ChatListViewHolder)holder).mUnread.setVisibility(visible);
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
		AvatarImageView mIcon;
		TextView mTitle;
		TextView mUnread;
		TextView mDate;
		TextView mContent;

		ChatListViewHolder(View view) {
			super(view);
			mIcon = (AvatarImageView)view.findViewById(R.id.icon);
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

		public AvatarImageView getmIcon() {
			return mIcon;
		}

		public void setmIcon(AvatarImageView mIcon) {
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
