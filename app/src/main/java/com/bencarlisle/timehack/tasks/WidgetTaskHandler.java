package com.bencarlisle.timehack.tasks;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.bencarlisle.timehack.R;

class WidgetTaskHandler extends TaskModel {
    private AppWidgetManager appWidgetManager;
    private int appWidgetId;
    private Context context;

    WidgetTaskHandler(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        super(context);
        this.context = context;
        this.appWidgetManager = appWidgetManager;
        this.appWidgetId = appWidgetId;
        start();
//        createTempEvent();
    }

    public void deleteTask(int id) {
        synchronized (tasks) {
            dataControl.removeTask(id);
            TaskRemoteViewsFactory.removeView(id);
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
                TaskRemoteViewsFactory.removeView(tasks.get(i).getId());
            }
        }
        updateWidget();
        Log.e("TASK", "CLEARED");
    }

    public void addTaskView(Task task) {
        RemoteViews newTask = new RemoteViews(context.getPackageName(), R.layout.task);
        newTask.setTextViewText(R.id.description, task.getDescription());
        newTask.setTextViewText(R.id.due_date, convertToDate(task.getDueDate()));
        newTask.setTextViewText(R.id.hours_left, String.valueOf(task.getHoursLeft()));
        newTask.setTextViewText(R.id.task_priority, String.valueOf(task.getPriority()));
        newTask.setOnClickPendingIntent(R.id.task, getPendingSelfIntent("deleteTask" + task.getId()));
        TaskRemoteViewsFactory.addView(task.getId(), newTask);
        updateWidget();
    }

    private void updateWidget() {
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.tasks);
    }

    private PendingIntent getPendingSelfIntent(String action) {
        Intent intent = new Intent(context, TaskWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}