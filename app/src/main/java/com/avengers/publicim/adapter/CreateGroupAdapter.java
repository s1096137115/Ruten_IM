package com.avengers.publicim.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avengers.publicim.R;
import com.avengers.publicim.data.entities.RosterEntry;

import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/10/3.
 */

public class CreateGroupAdapter extends BaseAdapter {
	private List<RosterEntry> mRosterEntries;

	public CreateGroupAdapter(Context context, List<RosterEntry> list) {
		super(context);
		mRosterEntries = list;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new CreateGroupAdapter.NormalTextViewHolder(mLayoutInflater.inflate(R.layout.view_roster_item_grid, parent, false));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		((NormalTextViewHolder)holder).mID.setText(mRosterEntries.get(position).getUser().getName());
		((NormalTextViewHolder)holder).mIcon.setImageResource(R.drawable.ic_person_black_48dp);
//		holder.mRemove.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mRosterEntries.remove(position);
//				if(mContext instanceof InviteMemberActivity){
////					((InviteMemberActivity)mContext).reloadSelect();
//					notifyItemRemoved(position);
//				}else{
//					refresh();
//				}
//			}
//		});
	}

	@Override
	public int getItemCount() {
		return mRosterEntries == null ? 0 : mRosterEntries.size();
	}

	public void refresh(){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				notifyDataSetChanged();
			}
		});
	}

	public void update(List<RosterEntry> objects){
		mRosterEntries = objects;
	}

	public static class NormalTextViewHolder extends RecyclerView.ViewHolder {
		View mView;
		ImageView mIcon;
		ImageView mRemove;
		TextView mID;

		NormalTextViewHolder(View view) {
			super(view);
			mView = view.findViewById(R.id.view);
			mIcon = (ImageView)view.findViewById(R.id.icon);
			mRemove = (ImageView)view.findViewById(R.id.remove);
			mID = (TextView)view.findViewById(R.id.id);
		}

		public View getmView() {
			return mView;
		}

		public void setmView(View mView) {
			this.mView = mView;
		}

		public ImageView getmIcon() {
			return mIcon;
		}

		public void setmIcon(ImageView mIcon) {
			this.mIcon = mIcon;
		}

		public ImageView getmRemove() {
			return mRemove;
		}

		public void setmRemove(ImageView mRemove) {
			this.mRemove = mRemove;
		}

		public TextView getmID() {
			return mID;
		}

		public void setmID(TextView mID) {
			this.mID = mID;
		}
	}
}
