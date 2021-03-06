package com.bencarlisle.timehack.tasks;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.bencarlisle.timehack.R;
import com.bencarlisle.timehack.main.DataControl;
import com.bencarlisle.timelibrary.main.Helper;
import com.bencarlisle.timelibrary.main.Task;
import com.bencarlisle.timehack.main.ViewAdapter;

public class TasksHandler extends TasksModel {

    private ViewAdapter viewAdapter;
    private Activity activity;

    public TasksHandler(DataControl dataControl, Activity activity, int secondsToWait) {
        super(dataControl);
        this.activity = activity;
        viewAdapter = new ViewAdapter(activity, R.id.tasks);
        ((ListView) activity.findViewById(R.id.tasks)).setAdapter(viewAdapter);
        startPolling(secondsToWait);
//        createTempEvent();
    }


    public void deleteTask(int id) {
        activity.runOnUiThread(() -> {
            dataControl.removeTask(id);
            synchronized (tasks) {
                for (int i = 0; i < tasks.size(); i++) {
                    if (tasks.get(i).hashCode() == id) {
                        viewAdapter.removeAt(i);
                        tasks.remove(i);
                        break;
                    }
                }
            }
        });
    }

    public void addTaskView(Task task) {
        activity.runOnUiThread(() -> addTaskViewRunnable(task));
    }

    private void addTaskViewRunnable(Task task) {
        View taskView = View.inflate(activity, R.layout.task, null);
        ((TextView) taskView.findViewById(R.id.description)).setText(task.getDescription());
        ((TextView) taskView.findViewById(R.id.due_date)).setText(Helper.convertDateToString(task.getDueDate()));
        ((TextView) taskView.findViewById(R.id.hours_left)).setText(String.valueOf(task.getHoursLeft()));
        ((TextView) taskView.findViewById(R.id.task_priority)).setText(String.valueOf(task.getPriority()));
        taskView.setId(task.hashCode());
        viewAdapter.add(taskView);
    }
}
