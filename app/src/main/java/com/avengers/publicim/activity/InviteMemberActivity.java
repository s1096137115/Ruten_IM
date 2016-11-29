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
import android.widget.FrameLayout;

import com.avengers.publicim.R;
import com.avengers.publicim.adapter.CreateGroupAdapter;
import com.avengers.publicim.adapter.InviteMemberAdapter;
import com.avengers.publicim.data.entities.Contact;
import com.avengers.publicim.data.entities.GroupInvite;
import com.avengers.publicim.data.entities.Invite;
import com.avengers.publicim.data.entities.Member;
import com.avengers.publicim.data.entities.Room;
import com.avengers.publicim.data.entities.RosterEntry;
import com.avengers.publicim.data.entities.User;
import com.avengers.publicim.data.event.ServiceEvent;
import com.avengers.publicim.utils.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;

import static com.avengers.publicim.R.id.recyclerView;
import static com.avengers.publicim.component.IMApplication.getProgress;

public class InviteMemberActivity extends BaseActivity{
    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerView2;
    private EditText mSearchText;
    private Button mButton;
    private InviteMemberAdapter mInviteAdapter;
    private CreateGroupAdapter mCreateApater;
    private FrameLayout mPreview;
    private Contact mContact;
    private Room mRoom;
    private List<RosterEntry> mEntries = new ArrayList<>();
    public List<RosterEntry> mSelects = new ArrayList<>();
    private List<RosterEntry> mSearchs = new ArrayList<>();
    private int mRequestCode;

    public static final String REQUEST_CODE = "requestCode";
    public static final String EXTRA = "extra";
    public static final int INVITE = 0;
    public static final int CREATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_member);
        mButton = (Button)findViewById(R.id.button);
        mSearchText = (EditText) findViewById(R.id.searchText);
        mPreview = (FrameLayout) findViewById(R.id.preview);
        getData();
        setToolbar();
        mInviteAdapter = new InviteMemberAdapter(this , getInviteRoster());
        mInviteAdapter.setSelectedList(mSelects);
        mCreateApater = new CreateGroupAdapter(this, mSelects);
        setPreviewVisiable();
        setRecyclerView(mInviteAdapter);
        setRecyclerView2(mCreateApater);
        mButton.setOnClickListener(onClickListener);
        mSearchText.addTextChangedListener(textWatcher);
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(onSelect);
        ItemClickSupport.addTo(mRecyclerView2).setOnItemClickListener(onPreview);
    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(mContact != null){
            getSupportActionBar().setTitle(mContact.getName());
        }
    }

    private void setPreviewVisiable(){
        if(mSelects.isEmpty()){
            mPreview.setVisibility(View.GONE);
            mButton.setText("邀請加入");
            mButton.setEnabled(false);
        }else{
            mPreview.setVisibility(View.VISIBLE);
            mButton.setText("邀請加入"+"( "+mSelects.size()+ " )");
            mButton.setEnabled(true);
        }
    }

    private void setRecyclerView(RecyclerView.Adapter adapter){
        mRecyclerView = (RecyclerView)findViewById(recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
    }

    private void setRecyclerView2(RecyclerView.Adapter adapter){
        mRecyclerView2 = (RecyclerView)findViewById(R.id.recyclerView2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView2.setLayoutManager(linearLayoutManager);
        mRecyclerView2.setHasFixedSize(true);
        mRecyclerView2.setAdapter(adapter);
    }

    private void getData(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mRequestCode = bundle.getInt(REQUEST_CODE);
            switch (mRequestCode){
                case INVITE:
                    mContact = (Contact)bundle.getSerializable(Contact.Type.CONTACT);
                    mRoom = mRoomManager.getItem(Room.Type.ALL ,mContact.getRid());
                    break;
            }
        }
    }

    private List<RosterEntry> getInviteRoster(){
        mEntries = mRosterManager.getList(RosterEntry.Type.ROSTER);
        List<RosterEntry> exists = new ArrayList<>();
        switch (mRequestCode) {
            case INVITE:
                for (RosterEntry entry : mEntries) {
                    for (Member member : mRoom.getMembers()) {
                        if (entry.getName().equals(member.getUser()) && !member.getRole().equals(Room.Role.EXIT)) {
                            exists.add(entry);
                            break;
                        }
                    }
                }
                mEntries.removeAll(exists);
                return mEntries;
            case CREATE:
                return mEntries;
        }
        return mEntries;
    }

    /**
     * 同步更新2個adapter的畫面
     */
    public void reloadSelect(){
        mInviteAdapter.refresh();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mCreateApater.getItemCount() !=0){
                    mRecyclerView2.smoothScrollToPosition(mCreateApater.getItemCount()-1);
                }
                setPreviewVisiable();
            }
        });
    }

    public void addSelect(){
        mInviteAdapter.refresh();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mRecyclerView2.smoothScrollToPosition(mCreateApater.getItemCount()-1);
                setPreviewVisiable();
            }
        });
    }

    public void removeSelect(){
        mInviteAdapter.refresh();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mInviteAdapter.refresh();
                setPreviewVisiable();
            }
        });
    }

    private void filter(String str){
        mSearchs.clear();
        if(str.isEmpty()){
            mInviteAdapter.update(mEntries);
            mInviteAdapter.refresh();
            return;
        }
        for (RosterEntry entry: mEntries) {
            if(entry.getName().toLowerCase().contains(str)){
                mSearchs.add(entry);
            }
        }
        mInviteAdapter.update(mSearchs);
        mInviteAdapter.refresh();
    }

    @Override
    public void onServeiceResponse(ServiceEvent event) {
        switch (event.getEvent()){
            case ServiceEvent.Event.CLOSE_DIALOG:
                getProgress().dismiss();
                finish();
                break;
        }
    }

    @Override
    public String getName() {
        return null;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (mRequestCode) {
                case INVITE:
                    List<RosterEntry> list = ((InviteMemberAdapter) mRecyclerView.getAdapter()).getSelectedList();
                    if(list.isEmpty()) return;
                    getProgress().setMessage("Waiting...");
                    getProgress().show();
                    List<User> users = new ArrayList<>();
                    for (RosterEntry entry: list) {
                        users.add(entry.getUser());
                    }
                    GroupInvite invite = new GroupInvite(users, Invite.Type.ROOM, mContact.getRid());
                    mIMService.sendInvite(invite);
                    break;
                case CREATE:
                    Intent intent = new Intent(InviteMemberActivity.this, CreateGroupActivity.class);
                    intent.putParcelableArrayListExtra(EXTRA, (ArrayList<? extends Parcelable>) mSelects);
                    startActivityForResult(intent, CREATE);
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case InviteMemberActivity.CREATE:
                if (data != null) {
                    mSelects = data.getExtras().getParcelableArrayList(EXTRA);
                    mCreateApater.update(mSelects);
                    mCreateApater.refresh();
                    mInviteAdapter.setSelectedList(mSelects);
                    mInviteAdapter.refresh();
                }
                break;
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            filter(s.toString().toLowerCase().trim());
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
                int index = mSelects.indexOf(mEntries.get(position));
                mSelects.remove(mEntries.get(position));
                mCreateApater.notifyItemRemoved(index);
            }else{
                mSelects.add(mEntries.get(position));
                holder.getmCheckBox().setChecked(true);
                mCreateApater.notifyItemInserted(mSelects.size() -1);
                mRecyclerView2.smoothScrollToPosition(mCreateApater.getItemCount()-1);
            }
            setPreviewVisiable();
        }
    };

    private ItemClickSupport.OnItemClickListener onPreview = new ItemClickSupport.OnItemClickListener() {
        @Override
        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
            int index = mEntries.indexOf(mSelects.get(position));
            mSelects.remove(position);
            mCreateApater.notifyItemRemoved(position);
            mInviteAdapter.notifyItemChanged(index);
            setPreviewVisiable();
        }
    };
}
