package com.avengers.publicim.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.avengers.publicim.R;
import com.avengers.publicim.adapter.InviteGroupAdapter;
import com.avengers.publicim.data.entities.Room;
import com.avengers.publicim.data.event.ServiceEvent;
import com.avengers.publicim.data.entities.Contact;
import com.avengers.publicim.data.entities.Invite;
import com.avengers.publicim.data.entities.RosterEntry;

import java.util.List;

import static com.avengers.publicim.conponent.IMApplication.getProgress;
import static com.avengers.publicim.conponent.IMApplication.getRoomManager;
import static com.avengers.publicim.conponent.IMApplication.getRosterManager;

public class InviteGroupActivity extends BaseActivity{
    private RecyclerView mRecyclerView;
    private Button mButton;
    private InviteGroupAdapter mInviteAdapter;
    private Contact mContact;
    private Room mRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_group);
        mButton = (Button)findViewById(R.id.button);
        mInviteAdapter = new InviteGroupAdapter(this , getRosterManager().getList(RosterEntry.Type.ROSTER));
        getData();
        setToolbar();
        setRecyclerView(mInviteAdapter);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<RosterEntry> list = ((InviteGroupAdapter) mRecyclerView.getAdapter()).getSelectedList();
                for (RosterEntry entry: list) {
                    getProgress().setMessage("Waiting...");
                    getProgress().show();
                    Invite invite = new Invite(entry.getUser(), Invite.Type.ROOM, mContact.getRid());
                    mIMService.sendInvite(invite);
                }
            }
        });
    }

    private void setToolbar(){
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
        if(this == event.getListener()){
            switch (event.getEvent()){
                case ServiceEvent.Event.CLOSE_DIALOG:
                    getProgress().dismiss();
                    finish();
                    break;
            }
        }
    }

    @Override
    public String getName() {
        return null;
    }
}
