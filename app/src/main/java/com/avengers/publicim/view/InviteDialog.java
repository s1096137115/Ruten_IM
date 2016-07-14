package com.avengers.publicim.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatEditText;

import com.avengers.publicim.conponent.IMApplication;
import com.avengers.publicim.data.entities.Invite;
import com.avengers.publicim.data.entities.RosterEntry;
import com.avengers.publicim.data.entities.User;

/**
 * Created by D-IT-MAX2 on 2016/6/14.
 */
public class InviteDialog extends AppCompatDialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final AppCompatEditText input = new AppCompatEditText(getActivity());
		return new AlertDialog.Builder(getActivity())
				.setTitle("invite")
				.setView(input)
				.setPositiveButton("y", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(!input.getText().toString().isEmpty()){
					Invite invite = new Invite(IMApplication.getUser(),
							new User("", input.getText().toString()),
							Invite.TYPE_FRIEND, null, null, RosterEntry.RELATION_FRIEND);
//							mIMService.sendInvite(invite);
				}
			}
		}).create();
	}
}
