package com.avengers.publicim.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avengers.publicim.R;
import com.avengers.publicim.activity.ChatActivity;
import com.avengers.publicim.adapter.ContactAdapter;
import com.avengers.publicim.data.callback.GroupListener;
import com.avengers.publicim.data.callback.RosterListener;
import com.avengers.publicim.data.callback.ServiceEvent;
import com.avengers.publicim.data.entities.Contact;
import com.avengers.publicim.data.entities.Group;
import com.avengers.publicim.data.entities.RosterEntry;
import com.avengers.publicim.utils.ItemClickSupport;


public class RosterFragment extends BaseFragment implements RosterListener, GroupListener{
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	private String mParam1;
	private String mParam2;
	private RecyclerView mRecyclerView;
	private ContactAdapter mContactAdapter;

	public RosterFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_roster, container, false);
		mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mContactAdapter = new ContactAdapter(getContext());
		mRecyclerView.setAdapter(mContactAdapter);
//		mRecyclerView.addOnItemTouchListener(mOnItemTouchListener);
		ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
			@Override
			public void onItemClicked(RecyclerView recyclerView, int position, View v) {
				Contact item = ((ContactAdapter) recyclerView.getAdapter()).getItem(position);
				if(item instanceof ContactAdapter.Header){
					RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(position);
					((ContactAdapter) recyclerView.getAdapter()).rotationView(holder, position);
					((ContactAdapter) recyclerView.getAdapter()).expandHeader(position);
				}else if(item instanceof RosterEntry){
					String name = ((ContactAdapter)recyclerView.getAdapter()).getItem(position).getName();
					Intent intent = new Intent(getActivity(), ChatActivity.class);
					intent.putExtra(ChatActivity.ROSTER_NAME, name);
					startActivity(intent);
				}else if(item instanceof Group){
					String gid = ((ContactAdapter)recyclerView.getAdapter()).getItem(position).getId();
					Intent intent = new Intent(getActivity(), ChatActivity.class);
					intent.putExtra(ChatActivity.GROUP_ID, gid);
					startActivity(intent);
				}
			}
		});
		setFab(view);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		refresh();
	}

	public void setFab(View view){
		FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
		assert fab != null;
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				refresh();
				mIMService.sendGetSyncData();
			}
		});
	}

	public void refresh(){
		mContactAdapter.update();
		mContactAdapter.refresh();
	}

	@Override
	public void onRosterUpdate() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				refresh();
			}
		});
	}

	@Override
	public void onGroupListener() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				refresh();
			}
		});
	}

	@Override
	public void onServeiceResponse(ServiceEvent event) {

	}
}
