package com.bencarlisle.timehack.events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.bencarlisle.timehack.main.WearVoiceRecognitionActivity;
import com.bencarlisle.timehack.tasks.TasksActivity;
import com.bencarlisle.timelibrary.R;

public class EventsActivity extends Activity {
    private transient EventsHandler calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        calendar = new EventsHandler(this);
    }

    @Override
    public void onStart() {
        calendar.startPolling(1);
        super.onStart();
    }

    public void deleteEvent(View view) {
        calendar.deleteEvent(view);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (event.getRepeatCount() == 0) {
            if (keyCode == KeyEvent.KEYCODE_STEM_1) {
                startActivity(new Intent(this, WearVoiceRecognitionActivity.class));
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_STEM_2) {
                startActivity(new Intent(this, TasksActivity.class));
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onStop () {
        calendar.stopPolling();
        super.onStop();
    }
}
