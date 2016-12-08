package com.avengers.publicim.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avengers.publicim.R;
import com.avengers.publicim.activity.BaseActivity;
import com.avengers.publicim.activity.ChatActivity;
import com.avengers.publicim.adapter.InviteRosterAdapter;
import com.avengers.publicim.component.IMApplication;
import com.avengers.publicim.data.action.GetUser;
import com.avengers.publicim.data.entities.AdvUser;
import com.avengers.publicim.data.entities.Contact;
import com.avengers.publicim.data.entities.Invite;
import com.avengers.publicim.data.entities.RosterEntry;
import com.avengers.publicim.data.entities.SingleInvite;
import com.avengers.publicim.data.event.ServiceEvent;


/**
 * A placeholder fragment containing a simple view.
 */
public class InviteRosterFragment extends BaseFragment {
	private RecyclerView mRecyclerView;
	private InviteRosterAdapter mAdapter;
	private GetUser mGetUser;

	public static InviteRosterFragment newInstance(GetUser getUser) {
		InviteRosterFragment fragment = new InviteRosterFragment();
		Bundle args = new Bundle();
		args.putSerializable(GetUser.class.getSimpleName(), getUser);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mGetUser = (GetUser)getArguments().getSerializable(GetUser.class.getSimpleName());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_invite_roster, container, false);
		mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mAdapter = new InviteRosterAdapter(this, mGetUser.getUsers());
		mRecyclerView.setAdapter(mAdapter);
		mAdapter.setOnClickListener(onClick);
//		ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(OnItemClick);
		return view;
	}

	public void sendInvite(Invite invite){
		((BaseActivity)getActivity()).getProgress().setMessage("Waiting...");
		((BaseActivity)getActivity()).getProgress().show();
		mIMService.sendInvite(invite);
	}

	@Override
	public void onServeiceResponse(ServiceEvent event) {
		switch (event.getEvent()){
			case ServiceEvent.Event.CLOSE_DIALOG:
				((BaseActivity)getActivity()).getProgress().dismiss();
				getActivity().finish();
				break;
		}
	}

	@Override
	public String getName() {
		return null;
	}

	private View.OnClickListener onClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			InviteRosterAdapter.NormalTextViewHolder holder = (InviteRosterAdapter.NormalTextViewHolder)
					mRecyclerView.findContainingViewHolder(v);
			if(holder == null) return;
			int position = holder.getAdapterPosition();

			AdvUser user = ((InviteRosterAdapter) mRecyclerView.getAdapter()).getItem(position);
			if(holder.getmDetail().getText().toString().equals(IMApplication.getContext().getString(R.string.msg_get_user_already))){
				Contact contact = mRosterManager.getItem(RosterEntry.Type.ROSTER ,user.getName());
				Intent intent = new Intent(getContext(), ChatActivity.class);
				intent.putExtra(Contact.Type.CONTACT, contact);
				startActivity(intent);
				getActivity().finish();
			}else if(holder.getmDetail().getText().toString().equals(IMApplication.getContext().getString(R.string.msg_get_user_not_yet))){
				Invite invite = new SingleInvite(user, Invite.Type.FRIEND);
				sendInvite(invite);
			}
		}
	};
}
