package com.bencarlisle.timehack.main;

import android.content.Intent;
import android.os.Bundle;

import com.bencarlisle.timehack.R;

public class MainActivity extends GeneralActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new Organizer().startScheduler(this);
		setContentView(R.layout.activity_main);
		DataControl dataControl = new DataControl(this);
		String token = dataControl.getToken();
		dataControl.close();
		if (token == null) {
			startActivity(new Intent(this, AuthenticateActivity.class));
		}
	}
}
