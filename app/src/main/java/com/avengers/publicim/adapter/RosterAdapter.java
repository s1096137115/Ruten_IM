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
import com.avengers.publicim.data.entities.Roster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/3/15.
 */
public class RosterAdapter extends RecyclerView.Adapter<RosterAdapter.NormalTextViewHolder> {
	private final LayoutInflater mLayoutInflater;
	private final Context mContext;
	public List<Roster> mMessages = new ArrayList<>();
	private Handler mHandler = new Handler();

	public RosterAdapter(Context context, List<Roster> objects) {
		mMessages = objects;
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public NormalTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.view_roster_item, parent, false));
	}

	@Override
	public void onBindViewHolder(NormalTextViewHolder holder, int position) {

	}

	@Override
	public int getItemCount() {
		return 0;
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
