package com.avengers.publicim.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avengers.publicim.R;
import com.avengers.publicim.adapter.InviteRosterAdapter;
import com.avengers.publicim.data.action.GetUser;
import com.avengers.publicim.data.entities.Invite;
import com.avengers.publicim.data.event.ServiceEvent;

import static com.avengers.publicim.component.IMApplication.getProgress;

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
//		ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
//			@Override
//			public void onItemClicked(RecyclerView recyclerView, int position, View v) {
//				GetUser.AdvUser user = ((InviteRosterAdapter) recyclerView.getAdapter()).getItem(position);
//				getProgress().setMessage("Waiting...");
//				getProgress().show();
//				Invite invite = new Invite(user, Invite.Type.FRIEND);
//				mIMService.sendInvite(invite);
//			}
//		});
		return view;
	}

	public void sendInvite(Invite invite){
		getProgress().setMessage("Waiting...");
		getProgress().show();
		mIMService.sendInvite(invite);
	}

	@Override
	public void onServeiceResponse(ServiceEvent event) {
		switch (event.getEvent()){
			case ServiceEvent.Event.CLOSE_DIALOG:
				getProgress().dismiss();
				getActivity().finish();
				break;
		}
	}

	@Override
	public String getName() {
		return null;
	}
}
