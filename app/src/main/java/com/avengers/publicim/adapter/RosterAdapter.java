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
import com.avengers.publicim.data.entities.Contact;
import com.avengers.publicim.data.entities.Group;
import com.avengers.publicim.data.entities.RosterEntry;

import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/3/15.
 */
public class RosterAdapter extends RecyclerView.Adapter<RosterAdapter.NormalTextViewHolder> {
	private final LayoutInflater mLayoutInflater;
	private final Context mContext;
	private List<? extends Contact> mRosterEntries;
	private Handler mHandler = new Handler();

	public RosterAdapter(Context context, List<? extends Contact> objects) {
		mRosterEntries = objects;
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public NormalTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.view_roster_item, parent, false));
	}

	@Override
	public void onBindViewHolder(NormalTextViewHolder holder, int position) {
		if(mRosterEntries.get(position) instanceof RosterEntry){
			holder.mID.setText(((RosterEntry) mRosterEntries.get(position)).getUser().getName());
		}else if(mRosterEntries.get(position) instanceof Group){
			holder.mID.setText(((Group) mRosterEntries.get(position)).getName());
		}
	}

	@Override
	public int getItemCount() {
		return mRosterEntries == null ? 0 : mRosterEntries.size();
	}

	public Contact getItem(int position){
		return mRosterEntries.get(position);
	}

	public void refresh(){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				notifyDataSetChanged();
			}
		});
	}

	public void update(List<? extends Contact> objects){
		mRosterEntries = objects;
	}

	public static class NormalTextViewHolder extends RecyclerView.ViewHolder {
		ImageView mIcon;
		TextView mID;

		NormalTextViewHolder(View view) {
			super(view);
			mIcon = (ImageView)view.findViewById(R.id.icon);
			mID = (TextView)view.findViewById(R.id.id);
		}
	}
}
