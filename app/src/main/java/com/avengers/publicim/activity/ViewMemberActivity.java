package com.avengers.publicim.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.avengers.publicim.R;
import com.avengers.publicim.adapter.MemberAdapter;
import com.avengers.publicim.data.entities.Contact;
import com.avengers.publicim.data.entities.Room;
import com.avengers.publicim.data.event.ServiceEvent;

public class ViewMemberActivity extends BaseActivity {
	private RecyclerView mRecyclerView;
	private Contact mContact;
	private Room mRoom;
	private MemberAdapter mMemberAdapter;
	private Button mButton;
	public static final int INVITE_ROSTER = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_member);
		getData();
		setToolbar();
		mMemberAdapter = new MemberAdapter(this, mRoom.getMembers());
		mButton = (Button)findViewById(R.id.button);
		mButton.setOnClickListener(onClickListener);
		setRecyclerView(mMemberAdapter);
	}

	public void setToolbar(){
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if(mContact != null){
			getSupportActionBar().setTitle(getString(R.string.action_view_member) +
					"(" + mRoom.getMembers().size() + ")");
		}
	}

	private void setRecyclerView(RecyclerView.Adapter adapter){
		mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setAdapter(adapter);
	}

	private void getData(){
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mContact = (Contact)bundle.getSerializable(Contact.Type.CONTACT);
			mRoom = mRoomManager.getItem(Room.Type.ALL ,mContact.getRid());
		}
	}

	@Override
	public void onServeiceResponse(ServiceEvent event) {

	}

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(ViewMemberActivity.this, InviteMemberActivity.class);
			intent.putExtra(Contact.Type.CONTACT, mContact);
			intent.putExtra(InviteMemberActivity.REQUEST_CODE, InviteMemberActivity.INVITE);
			startActivityForResult(intent, INVITE_ROSTER);
		}
	};

	@Override
	public String getName() {
		return null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case INVITE_ROSTER:
				if(resultCode == RESULT_OK) {
					Intent returnIntent = new Intent();
					setResult(RESULT_OK, returnIntent);
					finish();
				}
				break;
		}
	}
}
