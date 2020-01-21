package com.bencarlisle.timelibrary.day;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;

import com.bencarlisle.timelibrary.R;

public class Adapter implements ListAdapter {

    private RelativeLayout calendar;
    private int numberOfViews = 0;
    private boolean deleteNext = false;

    public Adapter(Context context) {
        calendar = (RelativeLayout)  View.inflate(context, R.layout.time_layout, null);
    }

    public void addView(View view) {
        if (deleteNext) {
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
            deleteNext = true;
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        return calendar;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 1;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        return 1;
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        // TODO Auto-generated method stub
        return false;

    }
}
