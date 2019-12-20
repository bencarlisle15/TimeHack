package com.bencarlisle.timehack.tasks;

import android.os.Bundle;
import android.view.View;

import com.bencarlisle.timehack.R;
import com.bencarlisle.timehack.main.GeneralActivity;

public class TasksActivity extends GeneralActivity {

    private TasksHandler tasksHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        tasksHandler = new TasksHandler(this);
    }

    public void deleteTask(View view) {
        tasksHandler.deleteTask(view.getId());
    }
}
