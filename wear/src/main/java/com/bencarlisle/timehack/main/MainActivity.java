package com.bencarlisle.timehack.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.bencarlisle.timehack.events.EventsActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, EventsActivity.class));
    }

}
