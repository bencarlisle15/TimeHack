package com.bencarlisle.timehack.events;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bencarlisle.timehack.R;

public class TimeViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TimeRemoteViewsFactory(this.getApplicationContext());
    }
}

class TimeRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context context;
    private RemoteViews calendar;
    private static SparseArray<RemoteViews> events = new SparseArray<>();

    TimeRemoteViewsFactory(Context context) {
        this.context = context;
    }

    static void addView(int id, RemoteViews view) {
        events.put(id, view);
    }

    static void removeView(int id) {
        events.remove(id);
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        calendar = new RemoteViews(context.getPackageName(), R.layout.time_layout);
        for (int i = 0; i < events.size(); i++) {
            calendar.addView(R.id.full_calendar, events.valueAt(i));
        }
        Log.e("EVENT", "DATA SET CHANGED");
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
        return calendar;
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