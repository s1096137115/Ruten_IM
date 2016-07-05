package com.avengers.publicim.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.avengers.publicim.R;
import com.avengers.publicim.adapter.InviteAdapter;
import com.avengers.publicim.conponent.IMApplication;
import com.avengers.publicim.data.entities.Contact;
import com.avengers.publicim.data.entities.Group;
import com.avengers.publicim.data.entities.Invite;
import com.avengers.publicim.data.entities.RosterEntry;

import java.util.List;

import static com.avengers.publicim.conponent.IMApplication.getGroupManager;
import static com.avengers.publicim.conponent.IMApplication.getRosterManager;

public class InviteActivity extends BaseActivity{
    private RecyclerView mRecyclerView;
    private Button mButton;
    private InviteAdapter mInviteAdapter;
    private Contact mContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        mButton = (Button)findViewById(R.id.button);
        mInviteAdapter = new InviteAdapter(this , getRosterManager().getList());
        getData();
        setToolbar();
        setRecyclerView(mInviteAdapter);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<RosterEntry> list = ((InviteAdapter) mRecyclerView.getAdapter()).getSelectedList();
                for (RosterEntry entry: list) {
                    Invite invite = new Invite(IMApplication.getUser(),entry.getUser(),
                            Invite.TYPE_GROUP, (Group)mContact, Invite.ROLE_INVITEES, null);
                    mIMService.sendInvite(invite);
                }
            }
        });
    }

    public void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(mContact != null){
            getSupportActionBar().setTitle(mContact.getName());
        }
    }

    public void setRecyclerView(RecyclerView.Adapter adapter){
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
    }

    public void getData(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String value = "";
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
}
