package com.bencarlisle.timehack.main;

import java.util.Calendar;

public class Helper {

    public static String convertTimeToString(Calendar calendar) {
        String hourString = String.valueOf(calendar.get(Calendar.HOUR));
        int minute = calendar.get(Calendar.MINUTE);
        String minuteString;
        if (minute < 10) {
            minuteString = "0" + minute;
        } else {
            minuteString = String.valueOf(minute);
        }
        String amString = (calendar.get(Calendar.AM_PM) == Calendar.AM) ? " AM" : "PM";
        return hourString + ":" + minuteString + " " + amString;
    }

    public static String convertDateToString(Calendar calendar) {
        return (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DATE);
    }
}
