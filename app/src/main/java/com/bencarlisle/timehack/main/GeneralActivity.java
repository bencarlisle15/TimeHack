package com.bencarlisle.timehack.main;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.bencarlisle.timehack.day.DayActivity;
import com.bencarlisle.timehack.returnables.ReturnablesActivity;
import com.bencarlisle.timehack.tasks.TasksActivity;

public abstract class GeneralActivity extends Activity {

    private final int REQ_CODE_SPEECH_INPUT = 100;

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
