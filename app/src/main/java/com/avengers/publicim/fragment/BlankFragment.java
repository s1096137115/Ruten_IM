package com.avengers.publicim.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avengers.publicim.R;
import com.avengers.publicim.conponent.IMApplication;
import com.avengers.publicim.data.event.ServiceEvent;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends BaseFragment {
	private static final String ARG_PARAM1 = "param1";

	private String mParam1 = "";
	private TextView mMsg;

	// TODO: Rename and change types and number of parameters
	public static BlankFragment newInstance(String param1) {
		BlankFragment fragment = new BlankFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view =  inflater.inflate(R.layout.fragment_blank, container, false);
		mMsg = (TextView)view.findViewById(R.id.msg);
		if(!mParam1.isEmpty()){
			mMsg.setVisibility(View.VISIBLE);
			mMsg.setText(IMApplication.getContext().getString(R.string.msg_get_user_not_find));
		}else{
			mMsg.setVisibility(View.GONE);
		}
		return view;
	}


	@Override
	public void onServeiceResponse(ServiceEvent event) {

	}

	@Override
	public String getName() {
		return null;
	}
}
