package com.bencarlisle.timehack.main;

import android.content.Intent;
import android.os.Bundle;

import com.bencarlisle.timehack.R;

public class MainActivity extends GeneralActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, Organizer.class));
    }
}
