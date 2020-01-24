package com.bencarlisle.timehack.tasks;

import android.app.Activity;

import com.bencarlisle.timehack.main.SharedDataControl;

class TasksHandler extends com.bencarlisle.timelibrary.tasks.TasksHandler {

    TasksHandler(Activity activity) {
        super(new SharedDataControl(activity), activity, 10);
    }
}
