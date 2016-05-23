package com.avengers.publicim.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.avengers.publicim.R;
import com.avengers.publicim.fragment.ChatListFragment;
import com.avengers.publicim.fragment.RosterFragment;

import io.socket.client.Socket;

public class MainActivity extends BaseActivity {
	private Socket mSocket;
	private Toolbar mToolbar;
	private TabLayout mTabLayout;
	private ViewPager mViewPager;
	private ViewPagerAdapter mViewPagerAdapter;
	private RosterFragment mRosterFragment = new RosterFragment();
	private ChatListFragment mChatListFragment = new ChatListFragment();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		setViewPager();
		setToolBar();
		setTabLayout();
	}

	private void setViewPager(){
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		mViewPager.setOffscreenPageLimit(4);
		mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),this);
		mViewPager.setAdapter(mViewPagerAdapter);
		mViewPager.setCurrentItem(0);
	}

	private void setToolBar() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
	}

	private void setTabLayout() {
		mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
		mTabLayout.setupWithViewPager(mViewPager);
		for (int i = 0; i < mTabLayout.getTabCount(); i++) {
			TabLayout.Tab tab = mTabLayout.getTabAt(i);
			assert tab != null;
			if(i == 0){
				tab.setCustomView(mViewPagerAdapter.getSelectedTabView(i));
			}else{
				tab.setCustomView(mViewPagerAdapter.getUnselectedTabView(i));
			}
		}
//        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);

		mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
//				menuUpdate.setVisible(false);
//				menuEdit.setVisible(false);
//				menuUploadPhoto.setVisible(false);
				int position = tab.getPosition();
				tab.setCustomView(null);
				tab.setCustomView(mViewPagerAdapter.getSelectedTabView(position));
				mViewPager.setCurrentItem(position);
				String label = "";
//				switch (position) {
//					case MENU_MESSAGE:
//						menuUpdate.setVisible(true);
//						mEventTypeFragment.checkPushStatus();
//						if(mPhotoUploadFragment.getEditMode()) mActionMode.finish();
//						label = getString(R.string.action_message);
//						break;
//					case MENU_PHOTO:
//						menuEdit.setVisible(true);
////                        menuSelectPhoto.setVisible(true);
//						menuUploadPhoto.setVisible(true);
//						label = getString(R.string.action_photo);
//						break;
//					case MENU_SETTING:
//						if(mPhotoUploadFragment.getEditMode()) mActionMode.finish();
//						label = getString(R.string.action_account);
//						break;
//				}
//				Answers.getInstance().logCustom(new CustomEvent(getString(R.string.record_main_tab))
//						.putCustomAttribute(getString(R.string.record_event_type_click), label));
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {
				int position = tab.getPosition();
//                tab.setIcon(mViewPagerAdapter.getUnselectedTabIconId(position));
				tab.setCustomView(null);
				tab.setCustomView(mViewPagerAdapter.getUnselectedTabView(position));
			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {
			}
		});

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
			mIMService.connect();
//			mIMService.sendMessage("hello");
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * inner class ViewPagerAdapter
	 */
	public class ViewPagerAdapter extends FragmentPagerAdapter {
		final int PAGE_COUNT = 2;
		private Context mContext;
		private int[] imageResId = {
				R.drawable.ic_person_black_48dp,
				R.drawable.ic_chat_black_48dp,
				R.drawable.ic_person_white_48dp,
				R.drawable.ic_chat_white_48dp
		};
		private View[] tabView = new View[imageResId.length];

		public ViewPagerAdapter(FragmentManager fm, Context context) {
			super(fm);
			mContext = context;
			for (int i = 0; i < imageResId.length; i++) {
				View v = LayoutInflater.from(context).inflate(R.layout.view_tab_imageview, null);
				ImageView img = (ImageView)v.findViewById(R.id.imageView);
				img.setBackgroundResource(imageResId[i]);
				tabView[i] = v;
			}
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
				case 0:
					return mRosterFragment;
				case 1:
					return mChatListFragment;
				default:
					return mRosterFragment;
			}
		}

		@Override
		public int getCount() {
			return PAGE_COUNT;
		}

		public int getUnselectedTabIconId(int position){
			return imageResId[position];
		}

		public int getSelectedTabIconId(int position){
			return imageResId[PAGE_COUNT + position];
		}

		public View getUnselectedTabView(int position){
			return tabView[position];
		}

		public View getSelectedTabView(int position){
			return tabView[PAGE_COUNT + position];
		}
	}


}
