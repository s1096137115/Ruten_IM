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
import com.avengers.publicim.activity.BaseActivity;
import com.avengers.publicim.activity.ChatActivity;
import com.avengers.publicim.adapter.RosterAdapter;
import com.avengers.publicim.data.entities.RosterEntry;
import com.avengers.publicim.data.listener.RosterListener;
import com.avengers.publicim.utils.ItemClickSupport;

import static com.avengers.publicim.conponent.IMApplication.getRosterManager;


public class RosterFragment extends BaseFragment implements RosterListener{
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	private String mParam1;
	private String mParam2;
	private RecyclerView mRecyclerView;
	private RosterAdapter mRosterAdapter;

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
		mRosterAdapter = new RosterAdapter(getContext(), getRosterManager().getEntries());
		mRecyclerView.setAdapter(mRosterAdapter);
//		mRecyclerView.addOnItemTouchListener(mOnItemTouchListener);
		ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
			@Override
			public void onItemClicked(RecyclerView recyclerView, int position, View v) {
				RosterEntry entry = ((RosterAdapter)recyclerView.getAdapter()).getItem(position);
				Intent intent = new Intent(getActivity(), ChatActivity.class);
				intent.putExtra(ChatActivity.ROSTER_NAME, entry.getUser().getName());
				startActivity(intent);
			}
		});
		setFab(view);
		return view;
	}

	public void setFab(View view){
		FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
		assert fab != null;
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
//				((BaseActivity)getActivity()).getBuilder().show();
				((BaseActivity)getActivity()).getBuilder().getDialog().dismiss();
				refresh();
			}
		});
	}

	public void refresh(){
		mRosterAdapter.update(getRosterManager().getEntries());
		mRosterAdapter.refresh();
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
}
