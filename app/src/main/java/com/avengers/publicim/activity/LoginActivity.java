package com.avengers.publicim.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avengers.publicim.R;
import com.avengers.publicim.component.IMApplication;
import com.avengers.publicim.data.entities.Presence;
import com.avengers.publicim.data.entities.RosterEntry;
import com.avengers.publicim.data.entities.User;
import com.avengers.publicim.utils.PreferenceHelper;


public class LoginActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		String account = PreferenceHelper.LoginStatus.getAccount();
		if(!account.isEmpty()){
			RosterEntry entry = new RosterEntry(new User("Android-Emulator", account),
					new Presence("","", Presence.Status.ONLINE), 0, "");
			IMApplication.setAccount(entry);
			startActivity(new Intent(LoginActivity.this, MainActivity.class));
			finish();
		}


		Button btLogin = (Button) findViewById(R.id.btLogin);
		btLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String account = ((EditText)findViewById(R.id.etAccount)).getText().toString();
				RosterEntry entry = new RosterEntry(new User("Android-Emulator", account),
						new Presence("","", Presence.Status.ONLINE), 0, "");
				IMApplication.setAccount(entry);
				PreferenceHelper.LoginStatus.setAccount(account);
				startActivity(new Intent(LoginActivity.this, MainActivity.class));
				finish();
			}
		});
	}
}
