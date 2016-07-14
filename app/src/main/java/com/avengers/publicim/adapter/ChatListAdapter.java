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
import com.avengers.publicim.data.entities.Chat;

import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/3/15.
 */
public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>{
	private Handler mHandler = new Handler();
	private final LayoutInflater mLayoutInflater;
	private final Context mContext;
	private List<Chat> mChats;

	public ChatListAdapter(Context context, List<Chat> objects){
		mChats = objects;
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public ChatListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ChatListViewHolder(mLayoutInflater.inflate(R.layout.view_chat_list_item, parent, false));
	}

	@Override
	public void onBindViewHolder(ChatListViewHolder holder, int position) {
		holder.mCid = mChats.get(position).getCid();
		int resId = mChats.get(position).getType() == Chat.TYPE_GROUP ?
				R.drawable.ic_group_black_48dp : R.drawable.ic_person_black_48dp;
		holder.mIcon.setImageResource(resId);
		holder.mTitle.setText(mChats.get(position).getTitle());
		holder.mDate.setText(mChats.get(position).getDate());
		holder.mContent.setText(mChats.get(position).getLastMsg().getContent());
		holder.mUnread.setText(String.valueOf(mChats.get(position).getUnread()));
		int visible = mChats.get(position).getUnread() == 0 ? View.GONE : View.VISIBLE;
		holder.mUnread.setVisibility(visible);
	}

	@Override
	public int getItemCount() {
		return mChats == null ? 0 : mChats.size();
	}

	public Chat getItem(int position){
		return mChats.get(position);
	}

	public void refresh(){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				notifyDataSetChanged();
			}
		});
	}

	public void update(List<Chat> objects){
		mChats = objects;
	}

	public static class ChatListViewHolder extends RecyclerView.ViewHolder {
		String mCid;
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

		public String getmCid() {
			return mCid;
		}

		public void setmCid(String mCid) {
			this.mCid = mCid;
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
