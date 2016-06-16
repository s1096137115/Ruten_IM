package com.avengers.publicim.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;


public class DialogBuilder extends AlertDialog.Builder {
    protected AlertDialog mDialog;
    protected Context mContext;

    public DialogBuilder(Context context) {
        super(context);
        mContext = context;
        mDialog = create();
    }

    @NonNull
    @Override
    public AlertDialog show() {
        if(mDialog.isShowing()) mDialog.dismiss();
        if(!((Activity)mContext).isFinishing()){
            mDialog = super.show();
        }
        return mDialog;
    }

    public AlertDialog getDialog(){
        return mDialog;
    }
}
