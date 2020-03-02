package com.bencarlisle.timehack.main;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

public class ViewAdapter extends ArrayAdapter<View> {
    public ViewAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = this.getItem(position);
        if (view == null) {
            return parent;
        }
        return view;
    }

    public void removeAt(int position) {
        remove(this.getItem(position));
    }
}
