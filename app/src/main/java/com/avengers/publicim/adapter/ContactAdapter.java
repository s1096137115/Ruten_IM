package com.avengers.publicim.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.avengers.publicim.R;
import com.avengers.publicim.data.entities.Contact;
import com.avengers.publicim.data.entities.Group;
import com.avengers.publicim.data.entities.Presence;
import com.avengers.publicim.data.entities.RosterEntry;

import java.util.ArrayList;
import java.util.List;

import static com.avengers.publicim.conponent.IMApplication.getGroupManager;
import static com.avengers.publicim.conponent.IMApplication.getRosterManager;

/**
 * Created by D-IT-MAX2 on 2016/6/23.
 */
public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private final LayoutInflater mLayoutInflater;
	private final Context mContext;
	private List<Contact> mContacts;
	private List<RosterEntry> mRoster;
	private List<Group> mGroups;
	private List<Header> mHeader;
	private Handler mHandler = new Handler();

	private static final int TYPE_HEADER = 0;
	private static final int TYPE_CONTACT = 1;
	private static final int TYPE_CONTACT_ADD = 2;

	private static final int GROUPS = 0;
	private static final int ROSTER = 1;

	public ContactAdapter(Context context) {
		mContacts = new ArrayList<>();
		mHeader = new ArrayList<>();
		mHeader.add(new Header(true,"群組"));
		mHeader.add(new Header(true,"好友"));
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		update();
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		RecyclerView.ViewHolder holder = null;
		switch (viewType) {
			case TYPE_HEADER:
				holder = new HeaderViewHolder(mLayoutInflater.inflate(R.layout.view_contact_category_item, parent, false));
				break;
			case TYPE_CONTACT:
				holder = new ContactViewHolder(mLayoutInflater.inflate(R.layout.view_roster_item, parent, false));
				break;
			case TYPE_CONTACT_ADD:
				holder = new ContactViewHolder(mLayoutInflater.inflate(R.layout.view_roster_item_add, parent, false));
				break;
		}
		return holder;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if(holder instanceof HeaderViewHolder) {
			((HeaderViewHolder) holder).title.setText(mContacts.get(position).getName());
		}else if(holder instanceof ContactViewHolder) {
			if(mContacts.get(position) instanceof RosterEntry){
				((ContactViewHolder) holder).icon.setImageResource(R.drawable.ic_person_black_48dp);
				int visibility = ((RosterEntry) mContacts.get(position)).getPresence()
						.getStatus() == Presence.STATUS_ONLINE ? View.VISIBLE : View.GONE;
				((ContactViewHolder) holder).status.setVisibility(visibility);
			}else{
				((ContactViewHolder) holder).icon.setImageResource(R.drawable.ic_group_black_48dp);
			}
			((ContactViewHolder) holder).id.setText((mContacts.get(position)).getName());
		}
	}

	public void rotationView(RecyclerView.ViewHolder holder, int position){
		Animation am;
		if(((Header)mContacts.get(position)).toggle){
			am = new RotateAnimation(0, 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		}else{
			am = new RotateAnimation(90, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		}
		am.setFillAfter(true);
		am.setDuration(200);
		((HeaderViewHolder)holder).viewToggle.startAnimation(am);
	}

	public void expandHeader(int position){
		if(((Header)mContacts.get(position)).toggle){
			if(mContacts.get(position).getName().equals("群組")){
				mContacts.removeAll(mGroups);
				notifyItemRangeRemoved(position + 1, mGroups.size());
			}else{
				mContacts.removeAll(mRoster);
				notifyItemRangeRemoved(position + 1, mRoster.size());
			}
			((Header)mContacts.get(position)).toggle = false;
		}else{
			if(mContacts.get(position).getName().equals("群組")){
				int index = mContacts.indexOf(mHeader.get(GROUPS));
				mContacts.addAll( index+1, mGroups);
				notifyItemRangeInserted(index+1, mGroups.size());
			}else{
				int index = mContacts.indexOf(mHeader.get(ROSTER));
				mContacts.addAll( index+1, mRoster);
				notifyItemRangeInserted(index+1, mRoster.size());
			}
			((Header)mContacts.get(position)).toggle = true;
		}
	}

	@Override
	public int getItemCount() {
		return mContacts == null ? 0 : mContacts.size();
	}

	@Override
	public int getItemViewType(int position) {
		if(mContacts.get(position) instanceof Header){
			return TYPE_HEADER;
		}else{
			return TYPE_CONTACT;
		}
	}

	public Contact getItem(int position){
		return mContacts.get(position);
	}

	public void refresh(){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				notifyDataSetChanged();
			}
		});
	}

	public void update(){
		mGroups = getGroupManager().getList();
		mRoster = getRosterManager().getList();
		mContacts.clear();
		mContacts.add(mHeader.get(GROUPS));
		mContacts.addAll(mGroups);
		mContacts.add(mHeader.get(ROSTER));
		mContacts.addAll(mRoster);
	}

	public static class ContactViewHolder extends RecyclerView.ViewHolder {
		ImageView icon;
		ImageView status;
		TextView id;

		ContactViewHolder(View view) {
			super(view);
			icon = (ImageView)view.findViewById(R.id.icon);
			status = (ImageView)view.findViewById(R.id.status);
			id = (TextView)view.findViewById(R.id.id);
		}
	}

	public static class HeaderViewHolder extends RecyclerView.ViewHolder {
		ImageView viewToggle;
		TextView title;
		View view;

		HeaderViewHolder(View view) {
			super(view);
			viewToggle = (ImageView)view.findViewById(R.id.view_toggle);
			title = (TextView)view.findViewById(R.id.title);
			this.view = view.findViewById(R.id.view);
		}
	}

	public static class Header implements Contact{
		public boolean toggle;
		public String title;

		public Header(boolean toggle, String title) {
			this.toggle = toggle;
			this.title = title;
		}

		@Override
		public String getId() {
			return null;
		}

		@Override
		public String getName() {
			return title;
		}
	}
}

