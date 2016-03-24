package com.avengers.publicim.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avengers.publicim.R;
import com.avengers.publicim.data.entities.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/3/15.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.NormalTextViewHolder> {
	private final LayoutInflater mLayoutInflater;
	private final Context mContext;
	public List<Message> mMessages = new ArrayList<>();
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
	public ChatAdapter.NormalTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		NormalTextViewHolder holder = null;
		switch (viewType) {
			case TYPE_RECEIVE:
				holder = new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.view_msg_item_left, parent, false));
				break;
			case TYPE_SEND:
				holder = new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.view_msg_item_right, parent, false));
				break;
		}
		return holder;
	}

	@Override
	public void onBindViewHolder(ChatAdapter.NormalTextViewHolder holder, int position) {
		holder.mContent.setText(mMessages.get(position).getContent());
		String eventTime = mMessages.get(position).getDate().replace(" ","\n");
		holder.mDatetime.setText(eventTime);
	}

	@Override
	public int getItemCount() {
		return mMessages == null ? 0 : mMessages.size();
	}

//	@Override
//	public int getItemViewType(int position) {
		//todo if Message.from equals account ? TYPE_SEND : TYPE_RECEIVE
//		return mMessages.get(position).replynick.equals(PreferenceHelper.LoginStatus.mUserId) ? TYPE_SEND : TYPE_RECEIVE;
//	}

	public void refresh(){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				notifyDataSetChanged();
			}
		});
	}

	public static class NormalTextViewHolder extends RecyclerView.ViewHolder {
//		TextView mAccount;
		TextView mContent;
		TextView mDatetime;

		NormalTextViewHolder(View view) {
			super(view);
//			mAccount = (TextView)view.findViewById(R.id.account);
			mContent = (TextView)view.findViewById(R.id.msg);
			mDatetime = (TextView)view.findViewById(R.id.datetime);
		}
	}
}
