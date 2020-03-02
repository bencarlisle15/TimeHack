package com.bencarlisle.timehack.main;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.bencarlisle.timelibrary.main.Helper;

import java.util.Calendar;

class DateSetter implements View.OnFocusChangeListener, DatePickerDialog.OnDateSetListener {

    private EditText editText;
    private Calendar calendar;
    private Context context;

    DateSetter(EditText editText, Context context){
        this.editText = editText;
        this.editText.setOnFocusChangeListener(this);
        this.calendar = Calendar.getInstance();
        this.context = context;

    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if(hasFocus){
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            new DatePickerDialog(context, this, year, month, day).show();
            view.clearFocus();
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        this.editText.setText(Helper.convertDateToString(calendar));
    }

}