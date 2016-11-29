package com.avengers.publicim.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avengers.publicim.R;
import com.avengers.publicim.data.entities.GroupInvite;
import com.avengers.publicim.adapter.InviteMemberAdapter;
import com.avengers.publicim.data.entities.Invite;
import com.avengers.publicim.data.entities.Member;
import com.avengers.publicim.data.entities.Room;
import com.avengers.publicim.data.entities.RosterEntry;
import com.avengers.publicim.data.entities.User;
import com.avengers.publicim.data.event.ServiceEvent;
import com.avengers.publicim.data.listener.RoomListener;
import com.avengers.publicim.utils.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;

import static com.avengers.publicim.activity.InviteMemberActivity.EXTRA;
import static com.avengers.publicim.component.IMApplication.getProgress;

public class CreateGroupActivity extends BaseActivity implements RoomListener {
	private List<RosterEntry> mSelects = new ArrayList<>();
	private RecyclerView mRecyclerView;
	private InviteMemberAdapter mInviteAdapter;
	private Button mButton;
	private EditText mGroupName;
	private Room mRoom;
	private List<RosterEntry> mEntries = new ArrayList<>();
	private TextView mGroupLimitText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_group);
		mButton = (Button)findViewById(R.id.button);
		mGroupName = (EditText)findViewById(R.id.textInput);
		mGroupLimitText = (TextView) findViewById(R.id.tv_member_limit);
		setToolbar();

		mSelects = (List<RosterEntry>) getIntent().getSerializableExtra(InviteMemberActivity.EXTRA);

		mEntries = mRosterManager.getList(RosterEntry.Type.ROSTER);

		mInviteAdapter = new InviteMemberAdapter(this , mRosterManager.getList(RosterEntry.Type.ROSTER));
		mInviteAdapter.setSelectedList(mSelects);
		setRecyclerView(mInviteAdapter);

		mGroupName.addTextChangedListener(textWatcher);

		mButton.setOnClickListener(onClickListener);

		ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(onSelect);
		updateView();

	}

	private void setToolbar(){
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
	}

	private void setRecyclerView(RecyclerView.Adapter adapter){
		mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setAdapter(adapter);
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
				List<User> users = new ArrayList<>();
				for (RosterEntry entry: mSelects) {
					users.add(entry.getUser());
				}
				GroupInvite invite = new GroupInvite(users, Invite.Type.ROOM, mRoom.getRid());
				mIMService.sendInvite(invite);

//				for (RosterEntry entry: mSelects) {
//					Invite invite = new Invite(entry.getUser(), Invite.Type.ROOM, mRoom.getRid());
//					mIMService.sendInvite(invite);
//				}
				break;
		}
	}

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String message = "";
			String title = getString(R.string.dialog_button_positive);
			if(mGroupName.getText().length() == 0) {
				message = getString(R.string.dialog_message_group_name_error);
				showMessageDialog(message, title);
				return;
			}

			if(mSelects.size() > 100) {
				message = getString(R.string.dialog_message_over_limit);
				showMessageDialog(message, title);
				return;
			}

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
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};

	private ItemClickSupport.OnItemClickListener onSelect = new ItemClickSupport.OnItemClickListener() {
		@Override
		public void onItemClicked(RecyclerView recyclerView, int position, View v) {
			InviteMemberAdapter.NormalTextViewHolder holder = (InviteMemberAdapter.NormalTextViewHolder)
					recyclerView.findViewHolderForAdapterPosition(position);
			if(mSelects.contains(mEntries.get(position))){
				holder.getmCheckBox().setChecked(false);
				mSelects.remove(mEntries.get(position));
			}else{
				mSelects.add(mEntries.get(position));
				holder.getmCheckBox().setChecked(true);
			}
			updateView();
		}
	};

	private void updateView() {
		int number = 100 - mSelects.size();
		mGroupLimitText.setText("此群組最多可邀請"+String.valueOf(number) +"位好友加入");
		mButton.setText("確認建立("+ mSelects.size() +")");
		if(mSelects.size() == 0) {
			mButton.setEnabled(false);
		} else {
			mButton.setEnabled(true);
		}
	}

	@Override
	public void onBackPressed() {

		Intent intent = new Intent();
		intent.putParcelableArrayListExtra(EXTRA, (ArrayList<? extends Parcelable>) mSelects);
		setResult(RESULT_OK, intent);
		finish();
	}

}
