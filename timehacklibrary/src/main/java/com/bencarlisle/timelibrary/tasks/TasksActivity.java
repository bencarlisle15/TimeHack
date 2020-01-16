package com.bencarlisle.timelibrary.tasks;

import android.os.Bundle;
import android.view.View;

import com.bencarlisle.timelibrary.R;
import com.bencarlisle.timelibrary.main.GeneralActivity;

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
