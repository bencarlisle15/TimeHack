package com.bencarlisle.timehack.tasks;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.bencarlisle.timehack.R;
import com.bencarlisle.timehack.main.DataControl;
import com.bencarlisle.timelibrary.main.Helper;
import com.bencarlisle.timelibrary.main.Task;

class WidgetTaskHandler extends TasksModel {
    private AppWidgetManager appWidgetManager;
    private int appWidgetId;
    private Context context;

    WidgetTaskHandler(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        super(new DataControl(context));
        this.context = context;
        this.appWidgetManager = appWidgetManager;
        this.appWidgetId = appWidgetId;
        startPolling(1);
    }

    public void deleteTask(int id) {
        synchronized (tasks) {
            dataControl.removeTask(id);
            TasksRemoteViewsFactory.removeView(id);
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getId() == id) {
                    tasks.remove(i);
                    break;
                }
            }
        }
        updateWidget();
    }

    public void clearViews() {
        synchronized (tasks) {
            for (int i = 0; i < tasks.size(); i++) {
                TasksRemoteViewsFactory.removeView(tasks.get(i).getId());
            }
        }
        updateWidget();
    }

    public void addTaskView(Task task) {
        RemoteViews newTask = new RemoteViews(context.getPackageName(), R.layout.task);
        newTask.setTextViewText(R.id.description, task.getDescription());
        newTask.setTextViewText(R.id.due_date, Helper.convertDateToString(task.getDueDate()));
        newTask.setTextViewText(R.id.hours_left, String.valueOf(task.getHoursLeft()));
        newTask.setTextViewText(R.id.task_priority, String.valueOf(task.getPriority()));
        newTask.setOnClickPendingIntent(R.id.task, getPendingSelfIntent("deleteTask" + task.getId()));
        TasksRemoteViewsFactory.addView(task.getId(), newTask);
        updateWidget();
    }

    private void updateWidget() {
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.tasks);
    }

    private PendingIntent getPendingSelfIntent(String action) {
        Intent intent = new Intent(context, TasksWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}