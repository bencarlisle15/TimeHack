package com.bencarlisle.timehack.main;

import android.content.Intent;
import android.os.Bundle;

import com.bencarlisle.timehack.day.DayActivity;
import com.bencarlisle.timelibrary.main.Organizer;
import com.bencarlisle.timelibrary.main.GeneralActivity;

public class MainActivity extends GeneralActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, DayActivity.class));
        startService(new Intent(this, Organizer.class));
    }
}
