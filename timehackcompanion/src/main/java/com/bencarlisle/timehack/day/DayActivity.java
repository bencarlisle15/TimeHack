package com.bencarlisle.timehack.day;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.bencarlisle.timehack.main.WearVoiceActivity;
import com.bencarlisle.timehack.tasks.TasksActivity;
import com.bencarlisle.timelibrary.main.GeneralActivity;
import com.bencarlisle.timelibrary.R;

public class DayActivity extends GeneralActivity {
    private transient CalendarControl calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        calendar = new CalendarControl(this);
    }

    public void deleteEvent(View view) {
        calendar.deleteEvent(view);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (event.getRepeatCount() == 0) {
            if (keyCode == KeyEvent.KEYCODE_STEM_1) {
                startActivity(new Intent(this, WearVoiceActivity.class));
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_STEM_2) {
                startActivity(new Intent(this, TasksActivity.class));
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
