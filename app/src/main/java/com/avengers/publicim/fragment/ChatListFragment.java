package com.avengers.publicim.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avengers.publicim.R;
import com.avengers.publicim.activity.ChatActivity;
import com.avengers.publicim.adapter.ChatListAdapter;
import com.avengers.publicim.data.callback.ChatListener;
import com.avengers.publicim.data.callback.ServiceEvent;
import com.avengers.publicim.data.entities.Chat;
import com.avengers.publicim.utils.ItemClickSupport;

import static com.avengers.publicim.conponent.IMApplication.getChatManager;


public class ChatListFragment extends BaseFragment implements ChatListener{
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	private String mParam1;
	private String mParam2;
	private RecyclerView mRecyclerView;
	private ChatListAdapter mChatListAdapter;

	public ChatListFragment() {
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
		View view = inflater.inflate(R.layout.fragment_chat, container, false);
		mChatListAdapter = new ChatListAdapter(getContext(), getChatManager().getList());
		mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mRecyclerView.setAdapter(mChatListAdapter);
//		mRecyclerView.addOnItemTouchListener(mOnItemTouchListener);
		ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
			@Override
			public void onItemClicked(RecyclerView recyclerView, int position, View v) {
				Chat chat = ((ChatListAdapter)recyclerView.getAdapter()).getItem(position);
				Intent intent = new Intent(getActivity(), ChatActivity.class);
				String chatType = chat.getType() == Chat.TYPE_GROUP ? ChatActivity.GROUP_ID : ChatActivity.ROSTER_NAME;
				intent.putExtra(chatType, chat.getCid());
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
				getChatManager().reload();
				mIMService.sendGetRoster();
				mIMService.sendGetGroup();
			}
		});
	}

	public void refresh(){
		mChatListAdapter.update(getChatManager().getList());
		mChatListAdapter.refresh();
	}

	@Override
	public void onChatUpdate() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				refresh();
			}
		});
	}

	boolean move = false;
	int positionDown = -1;
	RecyclerView.OnItemTouchListener mOnItemTouchListener = new RecyclerView.OnItemTouchListener() {
		@Override
		public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
			View view;
			switch (e.getAction()){
				case MotionEvent.ACTION_DOWN:
					Log.d("test", "ACTION_DOWN:");
					move = false;
					view = rv.findChildViewUnder(e.getX(),e.getY());
					positionDown = rv.getChildAdapterPosition(view);
					break;
				case MotionEvent.ACTION_MOVE:
					move = true;
					break;
				case MotionEvent.ACTION_UP:
					Log.d("test", "ACTION_UP:");
					view = rv.findChildViewUnder(e.getX(),e.getY());
					int positionUp = rv.getChildAdapterPosition(view);
					if(view == null || positionDown == positionUp){
						Toast.makeText(getContext(), "cancel", Toast.LENGTH_SHORT).show();
						return false;
					}
					ChatListAdapter.ChatListViewHolder item =
							(ChatListAdapter.ChatListViewHolder)rv.findContainingViewHolder(view);
					Intent intent = new Intent(getActivity(), ChatActivity.class);
					intent.putExtra(ChatActivity.ROSTER_NAME, item.getmCid());
					startActivity(intent);
					break;
			}
			return false;
		}

		@Override
		public void onTouchEvent(RecyclerView rv, MotionEvent e) {
			Log.d("test", "onTouchEvent:");
		}

		@Override
		public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
			Log.d("test", "onRequestDisallowInterceptTouchEvent:");
		}
	};

	@Override
	public void onServeiceResponse(ServiceEvent event) {

	}
}
