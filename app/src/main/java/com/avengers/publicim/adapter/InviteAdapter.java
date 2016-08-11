package com.avengers.publicim.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.avengers.publicim.R;
import com.avengers.publicim.data.entities.RosterEntry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by D-IT-MAX2 on 2016/3/15.
 */
public class InviteAdapter extends RecyclerView.Adapter<InviteAdapter.NormalTextViewHolder> {
	private final LayoutInflater mLayoutInflater;
	private final Context mContext;
	private Set<Integer> mSelected = new HashSet<>();
	private List<RosterEntry> mSelectedEntries = new ArrayList<>();
	private List<RosterEntry> mRosterEntries;
	private Handler mHandler = new Handler();

	public InviteAdapter(Context context, List<RosterEntry> list) {
		mRosterEntries = list;
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public NormalTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.view_roster_item, parent, false));
	}

	@Override
	public void onBindViewHolder(final NormalTextViewHolder holder, final int position) {
		holder.mID.setText(mRosterEntries.get(position).getUser().getName());
		holder.mIcon.setImageResource(R.drawable.ic_person_black_48dp);
		holder.mCheckBox.setVisibility(View.VISIBLE);
		if(mSelected.contains(position)){
			holder.mCheckBox.setChecked(true);
		}else{
			holder.mCheckBox.setChecked(false);
		}
		holder.mView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mSelected.contains(position)){
					mSelected.remove(position);
					holder.mCheckBox.setChecked(false);
					mSelectedEntries.remove(mRosterEntries.get(position));
				}else{
					mSelected.add(position);
					holder.mCheckBox.setChecked(true);
					mSelectedEntries.add(mRosterEntries.get(position));
				}
			}
		});
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
	}
}
