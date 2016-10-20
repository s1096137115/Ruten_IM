package com.avengers.publicim.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.avengers.publicim.R;
import com.avengers.publicim.data.entities.RosterEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/3/15.
 */
public class InviteMemberAdapter extends BaseAdapter {
	private List<RosterEntry> mSelectedEntries = new ArrayList<>();
	private List<RosterEntry> mRosterEntries;

	public InviteMemberAdapter(Context context, List<RosterEntry> list) {
		super(context);
		mRosterEntries = list;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.view_roster_item, parent, false));
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder,final int position) {
		((NormalTextViewHolder)holder).mID.setText(mRosterEntries.get(position).getUser().getName());
		((NormalTextViewHolder)holder).mIcon.setImageResource(R.drawable.ic_person_black_48dp);
		((NormalTextViewHolder)holder).mCheckBox.setVisibility(View.VISIBLE);
		if(mSelectedEntries.contains(mRosterEntries.get(position))){
			((NormalTextViewHolder)holder).mCheckBox.setChecked(true);
		}else{
			((NormalTextViewHolder)holder).mCheckBox.setChecked(false);
		}
//		holder.mView.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if(mSelectedEntries.contains(mRosterEntries.get(position))){
//					holder.mCheckBox.setChecked(false);
//					mSelectedEntries.remove(mRosterEntries.get(position));
////					((InviteMemberActivity)mContext).removeSelect();
//				}else{
//					holder.mCheckBox.setChecked(true);
//					mSelectedEntries.add(mRosterEntries.get(position));
////					((InviteMemberActivity)mContext).addSelect();
//				}
//				((InviteMemberActivity)mContext).reloadSelect();
//			}
//		});
	}

	@Override
	public int getItemCount() {
		return mRosterEntries == null ? 0 : mRosterEntries.size();
	}

	public RosterEntry getItem(int position){
		return mRosterEntries.get(position);
	}

	public List<RosterEntry> getSelectedList(){
		return mSelectedEntries;
	}

	public void setSelectedList(List<RosterEntry> list) {
		mSelectedEntries = list;
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
		TextView mID;
		CheckBox mCheckBox;

		NormalTextViewHolder(View view) {
			super(view);
			mView = view.findViewById(R.id.view);
			mIcon = (ImageView)view.findViewById(R.id.icon);
			mID = (TextView)view.findViewById(R.id.id);
			mCheckBox = (CheckBox)view.findViewById(R.id.checkBox);
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

		public TextView getmID() {
			return mID;
		}

		public void setmID(TextView mID) {
			this.mID = mID;
		}

		public CheckBox getmCheckBox() {
			return mCheckBox;
		}

		public void setmCheckBox(CheckBox mCheckBox) {
			this.mCheckBox = mCheckBox;
		}
	}
}
