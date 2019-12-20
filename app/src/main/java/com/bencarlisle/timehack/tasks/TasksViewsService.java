package com.bencarlisle.timehack.tasks;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bencarlisle.timehack.R;

public class TasksViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TasksRemoteViewsFactory(this.getApplicationContext());
    }
}

class TasksRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context context;
    private static RemoteViews taskList;
    private static SparseArray<RemoteViews> tasks = new SparseArray<>();

    TasksRemoteViewsFactory(Context context) {
        this.context = context;
    }

    static void addView(int id, RemoteViews view) {
        tasks.put(id, view);
    }

    static void removeView(int id) {
        tasks.remove(id);
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        taskList = new RemoteViews(context.getPackageName(), R.layout.task_list);
        for (int i = 0; i < tasks.size(); i++) {
            taskList.addView(R.id.task_list, tasks.valueAt(i));
        }
        Log.e("TASK", "data set changed");
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        return taskList;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

}