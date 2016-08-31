package com.avengers.publicim.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.avengers.publicim.R;
import com.avengers.publicim.adapter.InviteAdapter;
import com.avengers.publicim.data.callback.ServiceEvent;
import com.avengers.publicim.data.entities.Contact;
import com.avengers.publicim.data.entities.Invite;
import com.avengers.publicim.data.entities.RosterEntry;

import java.util.List;

import static com.avengers.publicim.conponent.IMApplication.getProgress;
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
        getContact();
        setToolbar();
        setRecyclerView(mInviteAdapter);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<RosterEntry> list = ((InviteAdapter) mRecyclerView.getAdapter()).getSelectedList();
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

    private void getContact(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mContact = (Contact)bundle.getSerializable(Contact.Type.CONTACT);
        }
    }

    @Override
    public void onServeiceResponse(ServiceEvent event) {
        if(this == event.toListener()){
            switch (event.getEvent()){
                case ServiceEvent.EVENT_CLOSE_DIALOG:
                    getProgress().dismiss();
                    finish();
                    break;
            }
        }
    }
}
