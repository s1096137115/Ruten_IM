package com.avengers.publicim.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.avengers.publicim.R;
import com.avengers.publicim.adapter.MemberAdapter;
import com.avengers.publicim.data.event.ServiceEvent;
import com.avengers.publicim.data.entities.Contact;
import com.avengers.publicim.data.entities.Room;

import static com.avengers.publicim.conponent.IMApplication.getRoomManager;

public class MemberActivity extends BaseActivity {
	private RecyclerView mRecyclerView;
	private Contact mContact;
	private Room mRoom;
	private MemberAdapter mMemberAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_member);
		getData();
		setToolbar();
		mMemberAdapter = new MemberAdapter(this, mRoom.getMembers());
		setRecyclerView(mMemberAdapter);
	}

	public void setToolbar(){
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if(mContact != null){
			getSupportActionBar().setTitle(mContact.getName());
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
			mRoom = getRoomManager().getItem(Room.Type.ALL ,mContact.getRid());
		}
	}

	@Override
	public void onServeiceResponse(ServiceEvent event) {

	}

	@Override
	public String getName() {
		return null;
	}
}
