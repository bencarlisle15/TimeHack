package com.bencarlisle.timehack.tasks;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bencarlisle.timehack.R;

public class TaskViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TaskRemoteViewsFactory(this.getApplicationContext());
    }
}

class TaskRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context context;
    private static SparseArray<RemoteViews> tasks = new SparseArray<>();

    TaskRemoteViewsFactory(Context context) {
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
        Log.e("TASK", "data set changed");
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        return tasks.valueAt(position);
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

//package com.bencarlisle.timehack.tasks;
//
//        import android.app.PendingIntent;
//        import android.content.Context;
//        import android.content.Intent;
//        import android.util.Log;
//        import android.util.SparseArray;
//        import android.widget.LinearLayout;
//        import android.widget.RemoteViews;
//        import android.widget.RemoteViewsService;
//
//        import com.bencarlisle.timehack.R;
//
//public class TaskViewsService extends RemoteViewsService {
//
//    @Override
//    public RemoteViewsFactory onGetViewFactory(Intent intent) {
//        return new TaskRemoteViewsFactory(this.getApplicationContext());
//    }
//}
//
//class TaskRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
//
//    private Context context;
//    private static RemoteViews linearLayout;
//    private static SparseArray<RemoteViews> tasks = new SparseArray<>();
//
//    TaskRemoteViewsFactory(Context context) {
//        this.context = context;
//    }
//
//    static void addView(int id, RemoteViews view) {
//        tasks.put(id, view);
//    }
//
//    static void removeView(int id) {
//        tasks.remove(id);
//    }
//
//    @Override
//    public void onCreate() {
//    }
//
//    @Override
//    public void onDataSetChanged() {
//        linearLayout = new RemoteViews(context.getPackageName(), R.layout.temp_linear);
//        for (int i = 0; i < tasks.size(); i++) {
//            linearLayout.addView(R.id.t, tasks.valueAt(i));
//        }
//        Log.e("TASK", "data set changed");
//    }
//
//    @Override
//    public void onDestroy() {
//
//    }
//
//    @Override
//    public int getCount() {
//        return 1;
//    }
//
//    @Override
//    public RemoteViews getViewAt(int position) {
//        return linearLayout;
////        return tasks.valueAt(position);
//    }
//
//    @Override
//    public RemoteViews getLoadingView() {
//        return null;
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return 1;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }
//
//    @Override
//    public boolean hasStableIds() {
//        return false;
//    }
//
//}