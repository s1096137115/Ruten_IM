package com.avengers.publicim.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avengers.publicim.R;
import com.avengers.publicim.adapter.CreateGroupAdapter;
import com.avengers.publicim.data.entities.Invite;
import com.avengers.publicim.data.entities.Member;
import com.avengers.publicim.data.entities.Room;
import com.avengers.publicim.data.entities.RosterEntry;
import com.avengers.publicim.data.event.ServiceEvent;
import com.avengers.publicim.data.listener.RoomListener;
import com.avengers.publicim.utils.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;

import static com.avengers.publicim.activity.InviteMemberActivity.CREATE;
import static com.avengers.publicim.activity.InviteMemberActivity.EXTRA;
import static com.avengers.publicim.component.IMApplication.getProgress;

public class CreateGroupActivity extends BaseActivity implements RoomListener {
	private List<RosterEntry> mSelects = new ArrayList<>();
	private RecyclerView mRecyclerView;
	private CreateGroupAdapter mCreateApater;
	private Button mButton;
	private EditText mGroupName;
	private Room mRoom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_group);
		mButton = (Button)findViewById(R.id.button);
		mGroupName = (EditText)findViewById(R.id.textInput);
		setToolbar();
		mCreateApater = new CreateGroupAdapter(this, mSelects);
		setRecyclerView(mCreateApater);

		mGroupName.addTextChangedListener(textWatcher);

		mButton.setOnClickListener(onClickListener);
		mButton.setEnabled(false);

		ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(onRemove);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(CreateGroupActivity.this, InviteMemberActivity.class);
				intent.putExtra(InviteMemberActivity.REQUEST_CODE, CREATE);
				intent.putParcelableArrayListExtra(EXTRA, (ArrayList<? extends Parcelable>) mSelects);
				startActivityForResult(intent, CREATE);
			}
		});
	}

	private void setToolbar(){
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
	}

	private void setRecyclerView(RecyclerView.Adapter adapter){
		mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
		mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setAdapter(adapter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode){
			case InviteMemberActivity.CREATE:
				if(data != null){
					mSelects = data.getExtras().getParcelableArrayList(EXTRA);
					mCreateApater.update(mSelects);
					mCreateApater.refresh();
				}
		}
	}

	@Override
	public void onServeiceResponse(ServiceEvent event) {
		switch (event.getEvent()){
			case ServiceEvent.Event.CLOSE_DIALOG:
				mRoom = (Room) event.getBundle().get(Room.class.getSimpleName());
				Log.d("test", "r:" + mRoom.getMembers().size());
				int s = mSelects.size() +1;
				Log.d("test", "s:" + s);
				for (Member meber: mRoom.getMembers()) {
					Log.d("test", "name:" + meber.getUser());
				}
				if(mRoom.getMembers().size() == mSelects.size() +1){
					getProgress().dismiss();
					finish();
				}
				break;
		}
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void onRoomUpdate(ServiceEvent event) {
		switch (event.getEvent()) {
			case ServiceEvent.Event.CREATE_ROOM:
				mRoom = (Room) event.getBundle().get(Room.class.getSimpleName());
				if(mSelects.isEmpty()){
					getProgress().dismiss();
					finish();
					return;
				}
				for (RosterEntry entry: mSelects) {
					Invite invite = new Invite(entry.getUser(), Invite.Type.ROOM, mRoom.getRid());
					mIMService.sendInvite(invite);
				}
				break;
		}
	}

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			getProgress().setMessage("Waiting...");
			getProgress().show();
			mIMService.sendCreateRoom(mGroupName.getText().toString(), Room.Type.GROUP);
		}
	};

	private TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if(s.length() != 0){
				mButton.setEnabled(true);
			}else{
				mButton.setEnabled(false);
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};

	private ItemClickSupport.OnItemClickListener onRemove = new ItemClickSupport.OnItemClickListener() {
		@Override
		public void onItemClicked(RecyclerView recyclerView, int position, View v) {
			mSelects.remove(position);
			mCreateApater.notifyItemRemoved(position);
		}
	};
}
