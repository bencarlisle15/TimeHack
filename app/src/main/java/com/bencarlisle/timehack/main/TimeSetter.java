package com.bencarlisle.timehack.main;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import com.bencarlisle.timelibrary.main.Helper;

import java.util.Calendar;

class TimeSetter implements View.OnFocusChangeListener, TimePickerDialog.OnTimeSetListener {

    private EditText editText;
    private Calendar calendar;
    private Context context;

    TimeSetter(EditText editText, Context context){
        this.editText = editText;
        this.editText.setOnFocusChangeListener(this);
        this.calendar = Calendar.getInstance();
        this.context = context;

    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if(hasFocus){
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            new TimePickerDialog(context, this, hour, minute, false).show();
            view.clearFocus();
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        this.editText.setText(Helper.convertTimeToString(calendar));
    }

}