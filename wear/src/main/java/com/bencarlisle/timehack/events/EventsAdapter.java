package com.bencarlisle.timehack.events;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;

import com.bencarlisle.timehack.R;

class EventsAdapter implements ListAdapter {

    private RelativeLayout calendar;
    private int numberOfViews = 0;
    private boolean deleteNext = false;

    public EventsAdapter(Context context) {
        calendar = (RelativeLayout)  View.inflate(context, R.layout.time_layout, null);
    }

    public void addView(View view) {
        if (deleteNext) {
            //view deleted before it was added
            deleteNext = false;
        } else {
            numberOfViews++;
            calendar.addView(view);
        }
    }

    public void removeView(int index) {
        if (index < numberOfViews) {
            calendar.removeViewAt(index + 1);
            numberOfViews--;
        } else {
            //view not yet added
            deleteNext = true;
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        return calendar;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}