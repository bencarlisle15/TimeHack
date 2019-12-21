package com.bencarlisle.timehack.main;

import androidx.annotation.NonNull;

import java.util.Calendar;

public class Event implements Comparable<Event> {

    private static int EVENT_ID = 0;
    private Calendar startTime, endTime;
    private String description;
    private int taskId;
    private int id;

    public Event(Calendar startTime, Calendar endTime, String description, int taskId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = toSentenceCase(description);
        this.taskId = taskId;
        this.id = EVENT_ID++;
    }

    public Event(int id, String description, long startTime, long endTime, int taskId) {
        this.id = id;
        this.description = description;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        this.startTime = calendar;
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(endTime);
        this.endTime = calendar;
        this.taskId = taskId;
    }

    public static void setEventId(int eventId) {
        EVENT_ID = eventId;
    }

    public int getId() {
        return this.id;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public int getTaskId() {
        return taskId;
    }

    private boolean isBetween(Calendar time) {
        return startTime.compareTo(time) < 0 && time.compareTo(endTime) > 0;
    }

    public boolean isOverlapping(Event event) {
        return isBetween(event.startTime) || isBetween(event.endTime);
    }

    @NonNull
    public String toString() {
        return description + " from " + Helper.convertTimeToString(startTime) + " to " + Helper.convertTimeToString(endTime);
    }

    private String toSentenceCase(String str) {
        return String.valueOf(str.charAt(0)).toUpperCase() + str.substring(1);
    }

    public String getDescription() {
        return description;
    }

    public int hashCode() {
        return id;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Event)) {
            return false;
        }
        return o.hashCode() == hashCode();
    }

    @Override
    public int compareTo(Event e) {
        return (int) (e.getStartTime().getTimeInMillis() - getStartTime().getTimeInMillis());
    }
}
