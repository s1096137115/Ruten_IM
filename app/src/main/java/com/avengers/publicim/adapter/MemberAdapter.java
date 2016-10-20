package com.avengers.publicim.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avengers.publicim.R;
import com.avengers.publicim.data.entities.Member;
import com.avengers.publicim.data.entities.Room;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/8/11.
 */
public class MemberAdapter extends BaseAdapter{
	private List<Member> mMembers;

	public MemberAdapter(Context context, List<Member> list){
		super(context);
		mMembers = new ArrayList<>();
		for (Member member :list) {
			if(member.getRole() >= Room.Role.MEMBER){
				mMembers.add(member);
			}
		}
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.view_roster_item, parent, false));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		((NormalTextViewHolder)holder).mID.setText(mMembers.get(position).getUser());
		((NormalTextViewHolder)holder).mIcon.setImageResource(R.drawable.ic_person_black_48dp);
	}

	@Override
	public int getItemCount() {
		return mMembers == null ? 0 : mMembers.size();
	}

	public static class NormalTextViewHolder extends RecyclerView.ViewHolder {
		View mView;
		ImageView mIcon;
		TextView mID;

		NormalTextViewHolder(View view) {
			super(view);
			mView = view.findViewById(R.id.view);
			mIcon = (ImageView)view.findViewById(R.id.icon);
			mID = (TextView)view.findViewById(R.id.id);
		}
	}
}
