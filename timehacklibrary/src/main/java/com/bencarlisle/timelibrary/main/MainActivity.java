package com.bencarlisle.timelibrary.main;

import android.content.Intent;
import android.os.Bundle;

import com.bencarlisle.timelibrary.R;

public class MainActivity extends GeneralActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, Organizer.class));
        setContentView(R.layout.activity_main);
    }
}
