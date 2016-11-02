package com.avengers.publicim.adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avengers.publicim.R;
import com.avengers.publicim.component.IMApplication;
import com.avengers.publicim.data.action.GetUser;
import com.avengers.publicim.data.entities.Invite;
import com.avengers.publicim.fragment.InviteRosterFragment;
import com.avengers.publicim.view.LoginAccount;

import java.util.List;

/**
 * Created by D-IT-MAX2 on 2016/9/20.
 */

public class InviteRosterAdapter extends BaseAdapter{
	private final Fragment mFragment;
	private List<GetUser.AdvUser> mAdvUser;

	public InviteRosterAdapter(Fragment fragment, List<GetUser.AdvUser> list){
		super(fragment.getContext());
		mAdvUser = list;
		mFragment = fragment;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new InviteRosterAdapter.NormalTextViewHolder(mLayoutInflater.inflate(R.layout.view_roster_item_add, parent, false));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
		((NormalTextViewHolder)holder).mID.setText(mAdvUser.get(position).getName());
		((NormalTextViewHolder)holder).mIcon.setImageResource(R.drawable.ic_person_black_48dp);

		if(LoginAccount.getInstance().getUser().getName().equals(mAdvUser.get(position).getName())){
			((NormalTextViewHolder)holder).mDetail.setText(IMApplication.getContext().getString(R.string.msg_get_user_yourself));
			((NormalTextViewHolder)holder).mButton.setVisibility(View.GONE);
		}else if(mRosterManager.contains(mAdvUser.get(position))){
			((NormalTextViewHolder)holder).mDetail.setText(IMApplication.getContext().getString(R.string.msg_get_user_already));
			((NormalTextViewHolder)holder).mButton.setVisibility(View.GONE);
		}else{
			((NormalTextViewHolder)holder).mDetail.setText(IMApplication.getContext().getString(R.string.msg_get_user_not_yet));
			((NormalTextViewHolder)holder).mButton.setVisibility(View.VISIBLE);
		}

		((NormalTextViewHolder)holder).mButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Invite invite = new Invite(getItem(position), Invite.Type.FRIEND);
				((InviteRosterFragment)mFragment).sendInvite(invite);
			}
		});
	}

	@Override
	public int getItemCount() {
		return mAdvUser == null ? 0 : mAdvUser.size();
	}

	public GetUser.AdvUser getItem(int position){
		return mAdvUser.get(position);
	}

	public static class NormalTextViewHolder extends RecyclerView.ViewHolder {
		View mView;
		ImageView mIcon;
		TextView mID;
		TextView mDetail;
		Button mButton;

		NormalTextViewHolder(View view) {
			super(view);
			mView = view.findViewById(R.id.view);
			mIcon = (ImageView)view.findViewById(R.id.icon);
			mID = (TextView)view.findViewById(R.id.id);
			mDetail = (TextView)view.findViewById(R.id.detail);
			mButton = (Button)view.findViewById(R.id.btAdd);
		}
	}
}
