package com.bencarlisle.timelibrary.main;

import androidx.annotation.NonNull;

import java.util.Calendar;

public class Event implements Comparable<Event>, Serializable {

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

    public Event(byte[] message, boolean needsId) {
        if (needsId) {
            this.id = EVENT_ID++;
        } else {
            this.id = (int) Helper.readLongFromBytes(message, 4, 0);
        }
        long startTime = Helper.readLongFromBytes(message, 8, 4);
        long endTime = Helper.readLongFromBytes(message, 8, 12);
        this.taskId = (int) Helper.readLongFromBytes(message, 4, 20);
        this.description = Helper.readStringFromBytes(message, message.length, 24);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        this.startTime = calendar;
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(endTime);
        this.endTime = calendar;
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

    @Override
    public byte[] serialize() {
        byte[] message = new byte[getSize()];
        Helper.writeLongToBytes(message, id, 4, 0);
        Helper.writeLongToBytes(message, startTime.getTimeInMillis(), 8, 4);
        Helper.writeLongToBytes(message, endTime.getTimeInMillis(), 8, 12);
        Helper.writeLongToBytes(message, taskId, 4, 20);
        Helper.writeStringToBytes(message, description, 24);
        return message;
    }

    @Override
    public int getSize() {
        return 24 + description.length();
    }
}
