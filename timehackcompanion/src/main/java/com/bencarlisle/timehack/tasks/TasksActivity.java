package com.bencarlisle.timehack.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.bencarlisle.timehack.R;
import com.bencarlisle.timehack.day.DayActivity;
import com.bencarlisle.timehack.main.WearVoiceActivity;
import com.bencarlisle.timelibrary.main.GeneralActivity;

public class TasksActivity extends GeneralActivity {

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
                startActivity(new Intent(this, WearVoiceActivity.class));
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_STEM_2) {
                startActivity(new Intent(this, DayActivity.class));
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
