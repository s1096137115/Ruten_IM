package com.avengers.publicim.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.avengers.publicim.R;
import com.avengers.publicim.data.action.GetUser;
import com.avengers.publicim.data.event.ServiceEvent;
import com.avengers.publicim.fragment.BlankFragment;
import com.avengers.publicim.fragment.InviteRosterFragment;
import com.avengers.publicim.view.SearchableEditText;

public class InviteRosterActivity extends BaseActivity {
	private SearchableEditText mSearchText;

	enum Result{
		FIND,NOT_FIND,EMPTY
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite_roster);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		mSearchText = (SearchableEditText) findViewById(R.id.searchText);
		mSearchText.setOnEditorActionListener(onEditorActionListener);
		mSearchText.setDrawableRightListener(onDrawableRightListener);
		initContent();
	}

	@Override
	public void onServeiceResponse(ServiceEvent event) {
		switch (event.getEvent()){
			case ServiceEvent.Event.GET_USER:
				GetUser getUser = (GetUser)event.getBundle().getSerializable(GetUser.class.getSimpleName());
				if(getUser.getUsers().isEmpty()){
					actionResponse(Result.NOT_FIND, getUser);
				}else{
					actionResponse(Result.FIND, getUser);
				}
				break;
		}
	}

	@Override
	public String getName() {
		return null;
	}

	private void actionResponse(final Result result, final GetUser getUser){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Fragment fragment = getSupportFragmentManager().findFragmentByTag(mSearchText.getText().toString());;
				if (fragment == null) {
					switch (result){
						case NOT_FIND:
							fragment = BlankFragment.newInstance(mSearchText.getText().toString());
							break;
						case FIND:
							fragment = InviteRosterFragment.newInstance(getUser);
							break;
					}
				}
				showContent(fragment);
			}
		});
	}

	private void actionSearch(){
		if(mSearchText.getText().length() != 0){
			Fragment fragment = getSupportFragmentManager().findFragmentByTag(mSearchText.getText().toString());
			if(fragment != null) {
				showContent(fragment);
			}else{
				mIMService.sendGetUser(GetUser.Type.ID, mSearchText.getText().toString());
			}
		}
	}

	private void initContent(){
		Fragment newFragment = BlankFragment.newInstance("");
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.content, newFragment, Result.EMPTY.name());
		ft.commit();
	}

	private void showContent(final Fragment fragment){
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.content, fragment, mSearchText.getText().toString());
//		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN); //给Fragment指定标准的转场动画
//		ft.addToBackStack(null);
		ft.commit();
	}

	private SearchableEditText.DrawableRightListener onDrawableRightListener = new SearchableEditText.DrawableRightListener() {
		@Override
		public void onDrawableRightClick(View view) {
			actionSearch();
		}
	};

	private TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			actionSearch();
			return false;
		}
	};

}
