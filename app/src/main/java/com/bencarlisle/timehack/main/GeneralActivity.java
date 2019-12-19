package com.bencarlisle.timehack.main;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.bencarlisle.timehack.day.DayActivity;
import com.bencarlisle.timehack.tasks.TasksActivity;

public abstract class GeneralActivity extends Activity {


    public void dayCalendar(View view) {
        if (!(this instanceof DayActivity)) {
            startActivity(new Intent(this, DayActivity.class));
        }
    }

    public void tasks(View view) {
        if (!(this instanceof TasksActivity)) {
            startActivity(new Intent(this, TasksActivity.class));
        }
    }
}
