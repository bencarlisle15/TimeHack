package com.bencarlisle.timelibrary.tasks;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.bencarlisle.timelibrary.R;
import com.bencarlisle.timelibrary.main.DataControllable;
import com.bencarlisle.timelibrary.main.Helper;
import com.bencarlisle.timelibrary.main.Task;
import com.bencarlisle.timelibrary.main.ViewAdapter;

public class TasksHandler extends TasksModel {

    private ViewAdapter viewAdapter;
    private Activity activity;

    public TasksHandler(DataControllable dataControllable, Activity activity, int secondsToWait) {
        super(dataControllable);
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
                    if (tasks.get(i).getId() == id) {
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
        taskView.setId(task.getId());
        viewAdapter.add(taskView);
    }
}
