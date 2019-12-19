package com.bencarlisle.timehack.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bencarlisle.timehack.R;
import com.bencarlisle.timehack.day.DayActivity;

public class MainActivity extends GeneralActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
