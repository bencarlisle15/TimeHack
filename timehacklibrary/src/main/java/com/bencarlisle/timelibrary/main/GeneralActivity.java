package com.bencarlisle.timelibrary.main;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.bencarlisle.timelibrary.day.DayActivity;
import com.bencarlisle.timelibrary.returnables.ReturnablesActivity;
import com.bencarlisle.timelibrary.tasks.TasksActivity;

public abstract class GeneralActivity extends Activity {

    public void dayCalendar(View view) {
        if (!(this instanceof DayActivity)) {
            startActivity(new Intent(this, DayActivity.class));
        }
    }

    public void returnables(View view) {
        if (!(this instanceof ReturnablesActivity)) {
            startActivity(new Intent(this, ReturnablesActivity.class));
        }
    }

    public void tasks(View view) {
        if (!(this instanceof TasksActivity)) {
            startActivity(new Intent(this, TasksActivity.class));
        }
    }


    @SuppressWarnings("WeakerAccess")
    public void start(View view) {
        startActivity(new Intent(this, VoiceRecognitionActivity.class));
    }
}
