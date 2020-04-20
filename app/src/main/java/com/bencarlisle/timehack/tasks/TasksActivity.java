package com.bencarlisle.timehack.tasks;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bencarlisle.timehack.main.DataControl;
import com.bencarlisle.timehack.R;
import com.bencarlisle.timehack.main.GeneralActivity;

public class TasksActivity extends GeneralActivity {

    private TasksHandler tasksHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        tasksHandler = new TasksHandler(new DataControl(this), this, 1);
    }

    public void deleteTask(View view) {
        tasksHandler.deleteTask(view.getId());
    }
}
