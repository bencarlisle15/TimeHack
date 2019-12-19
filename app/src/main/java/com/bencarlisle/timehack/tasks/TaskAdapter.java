package com.bencarlisle.timehack.tasks;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

class TaskAdapter extends ArrayAdapter<View> {
    public TaskAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return this.getItem(position);
    }

    public void removeId(int position) {
        remove(this.getItem(position));
    }
}
