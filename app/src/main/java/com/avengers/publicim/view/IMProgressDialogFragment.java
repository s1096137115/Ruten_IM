package com.avengers.publicim.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;

/**
 * Created by D-IT-MAX2 on 2016/6/14.
 */
public class IMProgressDialogFragment extends AppCompatDialogFragment{

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setTitle("progress");
		return dialog;
	}
}
