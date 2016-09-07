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
import com.avengers.publicim.adapter.ChatListAdapter;
import com.avengers.publicim.data.callback.RoomListener;
import com.avengers.publicim.data.callback.ServiceEvent;
import com.avengers.publicim.data.entities.Contact;
import com.avengers.publicim.data.entities.Room;
import com.avengers.publicim.utils.ItemClickSupport;

import static com.avengers.publicim.conponent.IMApplication.getRoomManager;


public class ChatListFragment extends BaseFragment implements RoomListener {
	private RecyclerView mRecyclerView;
	private ChatListAdapter mChatListAdapter;

	public ChatListFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_chat, container, false);
		mChatListAdapter = new ChatListAdapter(getContext(), getRoomManager().getList());
		mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mRecyclerView.setAdapter(mChatListAdapter);
//		mRecyclerView.addOnItemTouchListener(mOnItemTouchListener);
		ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
			@Override
			public void onItemClicked(RecyclerView recyclerView, int position, View v) {
				Room room = ((ChatListAdapter)recyclerView.getAdapter()).getItem(position);
				Intent intent = new Intent(getActivity(), ChatActivity.class);
				intent.putExtra(Contact.Type.CONTACT, room);
				startActivity(intent);
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
				mIMService.sendGetRoster();
				mIMService.sendGetRoom();
			}
		});
	}

	public void refresh(){
		mChatListAdapter.update(getRoomManager().getList());
		mChatListAdapter.refresh();
	}

	@Override
	public void onRoomUpdate() {
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

	@Override
	public String getName() {
		return null;
	}
}
