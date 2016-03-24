package com.avengers.publicim.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.avengers.publicim.R;
import com.avengers.publicim.adapter.RosterAdapter;
import com.avengers.publicim.conponent.IMApplication;

import io.socket.client.Socket;

public class MainActivity extends BaseActivity {
	Socket mSocket;
	RecyclerView mRecyclerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setAdapter(new RosterAdapter(this, IMApplication.getRoster().getEntries()));

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		assert fab != null;
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mIMService.connect();
			}
		});

		FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
		assert fab2 != null;
		fab2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
//				RosterAdapter ra = new RosterAdapter(MainActivity.this, mIMService.getRosters());
//				mRecyclerView.setAdapter(ra);
//				ra.notifyDataSetChanged();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
//			mIMService.disconnect();
			mIMService.sendMessage("hello");
			return true;
		}

		return super.onOptionsItemSelected(item);
	}


}
