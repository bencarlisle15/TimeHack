package com.bencarlisle.timehack;

import androidx.annotation.NonNull;
import com.framgia.library.calendardayview.data.IEvent;
import com.framgia.library.calendardayview.data.IPopup;

import java.util.Calendar;

public class Event implements IEvent, IPopup {

    private Calendar startTime, endTime;
    private String description;

    Event(Calendar startTime, Calendar endTime, String description) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = toSentenceCase(description);
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    @Override
    public String getTitle() {
        return description;
    }

    public String getDescription() {
        return "";
    }

    @Override
    public String getQuote() {
        return "";
    }

    @Override
    public String getImageStart() {
        return "";
    }

    @Override
    public String getImageEnd() {
        return "";
    }

    @Override
    public Boolean isAutohide() {
        return false;
    }

    private boolean isBetween(Calendar time) {
        return startTime.compareTo(time) < 0 && time.compareTo(endTime) > 0;
    }

    boolean isOverlapping(Event event) {
        return isBetween(event.startTime) || isBetween(event.endTime);
    }

    @NonNull
    public String toString() {
        return description + " from " + convertToString(startTime) + " to " + convertToString(endTime);
    }

    private String toSentenceCase(String str) {
        return String.valueOf(str.charAt(0)).toUpperCase() + str.substring(1);
    }

    private String convertToString(Calendar time) {
        return time.get(Calendar.HOUR) + ":" + time.get(Calendar.MINUTE) + " " + (time.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
    }

    @Override
    public String getName() {
        return description;
    }

    @Override
    public int getColor() {
        return 0;
    }
}
