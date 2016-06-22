package com.avengers.publicim.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by D-IT-MAX2 on 2016/6/20.
 */
public class IMProgressDialog extends ProgressDialog {
	private Context mContext;

	public IMProgressDialog(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public void show() {
		if(isShowing()) dismiss();
		if(!((Activity)mContext).isFinishing()){
			super.show();
		}
	}

	@Override
	public void dismiss() {
		if(!((Activity)mContext).isFinishing()){
			super.dismiss();
		}
	}
}
