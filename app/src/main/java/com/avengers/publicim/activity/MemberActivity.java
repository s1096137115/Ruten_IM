package com.avengers.publicim.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.avengers.publicim.R;
import com.avengers.publicim.adapter.MemberAdapter;
import com.avengers.publicim.data.callback.ServiceEvent;
import com.avengers.publicim.data.entities.Contact;
import com.avengers.publicim.data.entities.Group;

import static com.avengers.publicim.conponent.IMApplication.getGroupManager;
import static com.avengers.publicim.conponent.IMApplication.getRosterManager;

public class MemberActivity extends BaseActivity {
	private RecyclerView mRecyclerView;
	private Contact mContact;
	private MemberAdapter mMemberAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_member);
		getContact();
		setToolbar();
		mMemberAdapter = new MemberAdapter(this, ((Group)mContact).getMembers());
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

	private void getContact(){
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			String value;
			if(bundle.getString(ChatActivity.ROSTER_NAME) != null){
				value = bundle.getString(ChatActivity.ROSTER_NAME);
				if(getRosterManager().contains(value)){
					mContact = getRosterManager().getItem(value);
				}
			}else if(bundle.getString(ChatActivity.GROUP_ID) != null){
				value = bundle.getString(ChatActivity.GROUP_ID);
				if(getGroupManager().contains(value)){
					mContact = getGroupManager().getItem(value);
				}
			}
		}
	}

	@Override
	public void onServeiceResponse(ServiceEvent event) {

	}
}
