package com.bencarlisle.timehack.tasks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.bencarlisle.timehack.R;
import com.bencarlisle.timehack.events.EventsActivity;
import com.bencarlisle.timehack.main.WearVoiceRecognitionActivity;

public class TasksActivity extends Activity {

    private TasksHandler tasksHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tasks_layout);
        tasksHandler = new TasksHandler(this);
    }

    public void deleteTask(View view) {
        tasksHandler.deleteTask(view.getId());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (event.getRepeatCount() == 0) {
            if (keyCode == KeyEvent.KEYCODE_STEM_1) {
                startActivity(new Intent(this, WearVoiceRecognitionActivity.class));
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_STEM_2) {
                startActivity(new Intent(this, EventsActivity.class));
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onStart() {
        tasksHandler.startPolling(1);
        super.onStart();
    }

    @Override
    public void onStop () {
        tasksHandler.stopPolling();
        super.onStop();
    }
}
