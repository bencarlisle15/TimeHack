package com.bencarlisle.timehack.tasks;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.bencarlisle.timehack.R;

public class TasksHandler extends TaskModel {

    private TaskAdapter taskAdapter;
    private Activity activity;

    TasksHandler(Activity activity) {
        super(activity);
        this.activity = activity;
        taskAdapter = new TaskAdapter(activity, R.id.tasks);
        ((ListView) activity.findViewById(R.id.tasks)).setAdapter(taskAdapter);
        start();
    }


    public void deleteTask(int id) {
        activity.runOnUiThread(() -> {
            dataControl.removeTask(id);
            synchronized (tasks) {
                for (int i = 0; i < tasks.size(); i++) {
                    if (tasks.get(i).getId() == id) {
                        taskAdapter.removeAt(i);
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
        View taskView = activity.getLayoutInflater().inflate(R.layout.task, null);
        ((TextView) taskView.findViewById(R.id.description)).setText(task.getDescription());
        ((TextView) taskView.findViewById(R.id.due_date)).setText(convertToDate(task.getDueDate()));
        ((TextView) taskView.findViewById(R.id.hours_left)).setText(String.valueOf(task.getHoursLeft()));
        ((TextView) taskView.findViewById(R.id.task_priority)).setText(String.valueOf(task.getPriority()));
        taskView.setId(task.getId());
        taskAdapter.add(taskView);
    }
}
