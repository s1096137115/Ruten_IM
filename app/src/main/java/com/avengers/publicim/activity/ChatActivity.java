package com.avengers.publicim.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.avengers.publicim.R;

public class ChatActivity extends BaseActivity {
	RecyclerView mRecyclerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		assert fab != null;
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
//				ChatAdapter ra = new ChatAdapter(ChatActivity.this, mIMService.getRosters());
//				mRecyclerView.setAdapter(ra);
//				ra.notifyDataSetChanged();
			}
		});
	}

}
