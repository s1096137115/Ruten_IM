package com.avengers.publicim.adapter;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avengers.publicim.R;
import com.avengers.publicim.conponent.IMApplication;
import com.avengers.publicim.data.action.GetUser;
import com.avengers.publicim.data.entities.Invite;
import com.avengers.publicim.fragment.InviteRosterFragment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.avengers.publicim.conponent.IMApplication.getRosterManager;

/**
 * Created by D-IT-MAX2 on 2016/9/20.
 */

public class InviteRosterAdapter extends RecyclerView.Adapter<InviteRosterAdapter.NormalTextViewHolder>{
	private final LayoutInflater mLayoutInflater;
	private final Fragment mFragment;
	private Set<Integer> mSelected = new HashSet<>();
	private List<GetUser.AdvUser> mAdvUser;
	private Handler mHandler = new Handler();

	public InviteRosterAdapter(Fragment fragment, List<GetUser.AdvUser> list){
		mAdvUser = list;
		mFragment = fragment;
		mLayoutInflater = LayoutInflater.from(fragment.getContext());
	}

	@Override
	public NormalTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new InviteRosterAdapter.NormalTextViewHolder(mLayoutInflater.inflate(R.layout.view_roster_item_add, parent, false));
	}

	@Override
	public void onBindViewHolder(NormalTextViewHolder holder, final int position) {
		holder.mID.setText(mAdvUser.get(position).getName());
		holder.mIcon.setImageResource(R.drawable.ic_person_black_48dp);

		if(IMApplication.getUser().getName().equals(mAdvUser.get(position).getName())){
			holder.mDetail.setText(IMApplication.getContext().getString(R.string.msg_get_user_yourself));
			holder.mButton.setVisibility(View.GONE);
		}else if(getRosterManager().contains(mAdvUser.get(position))){
			holder.mDetail.setText(IMApplication.getContext().getString(R.string.msg_get_user_already));
			holder.mButton.setVisibility(View.GONE);
		}else{
			holder.mDetail.setText(IMApplication.getContext().getString(R.string.msg_get_user_not_yet));
			holder.mButton.setVisibility(View.VISIBLE);
		}

		holder.mButton.setOnClickListener(new View.OnClickListener() {
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
