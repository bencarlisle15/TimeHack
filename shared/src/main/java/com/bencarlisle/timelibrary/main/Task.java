package com.bencarlisle.timelibrary.main;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Task implements Comparable<Task>, Serializable {

    private static int TASK_ID = 0;
    private int id;
    private Calendar dueDate;
    private String description;
    private int priority;
    private float hoursRequired;
    private float hoursCompleted;

    public Task(Calendar dueDate, String description, int priority, float hoursRequired, float hoursCompleted) {
        this.dueDate = dueDate;
        this.description = description;
        this.priority = priority;
        this.hoursRequired = hoursRequired;
        this.hoursCompleted = hoursCompleted;
        this.id = TASK_ID++;
    }


    public Task(int id, long dueDateMillis, String description, int priority, float hoursRequired, float hoursCompleted) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dueDateMillis);
        this.dueDate = calendar;
        this.description = description;
        this.priority = priority;
        this.hoursRequired = hoursRequired;
        this.hoursCompleted = hoursCompleted;
        this.id = id;
    }

    public Task(byte[] message, boolean needsId) {
        if (needsId) {
            this.id = TASK_ID++;
        } else {
            this.id = (int) Helper.readLongFromBytes(message, 4, 0);
        }
        long dueDate = Helper.readLongFromBytes(message, 8, 4);
        this.priority = (int) Helper.readLongFromBytes(message, 4, 12);
        this.hoursRequired = Helper.readFloatFromBytes(message, 16);
        this.hoursCompleted = Helper.readFloatFromBytes(message, 20);
        this.description = Helper.readStringFromBytes(message, message.length, 24);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dueDate);
        this.dueDate = calendar;
    }

    public static void setTaskId(int taskId) {
        TASK_ID = taskId;
    }

    public int getDaysLeft() {
        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.DAY_OF_YEAR, dueDate.get(Calendar.DAY_OF_YEAR));
        midnight.set(Calendar.HOUR_OF_DAY, 12);
        midnight.set(Calendar.MINUTE, 0);
        Calendar currentMidnight = Calendar.getInstance();
        currentMidnight.set(Calendar.HOUR_OF_DAY, 0);
        currentMidnight.set(Calendar.MINUTE, 0);
        long millisDifference = midnight.getTimeInMillis() - currentMidnight.getTimeInMillis();
        return (int) TimeUnit.MILLISECONDS.toDays(millisDifference);
    }

    public int getId() {
        return id;
    }

    public Calendar getDueDate() {
        return dueDate;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public float getHoursRequired() {
        return hoursRequired;
    }

    public float getHoursCompleted() {
        return hoursCompleted;
    }

    @NonNull
    public String toString() {
        return description + " due at " + Helper.convertDateToString(dueDate) + " with priority " + priority;
    }

    public int hashCode() {
        return id;
    }

    public boolean equals(Object o) {
        return o instanceof Task && o.hashCode() == hashCode();
    }

    public int getHoursLeft() {
        return (int) Math.ceil(getHoursRequired() - getHoursCompleted());
    }

    public float getNextHours() {
        int daysLeft = getDaysLeft();
        float hoursLeft = getHoursLeft();
        if (daysLeft <= 1) {
            return hoursLeft;
        }
        for (int i = 0; i < daysLeft; i++) {
            hoursLeft = singleRound(hoursLeft);
        }
        return hoursLeft;
    }

    private float singleRound(float hoursLeft) {
        float nextHours = (float) Math.log(hoursLeft);
        float floored = (float) Math.floor(nextHours);
        float offset = nextHours - floored;
        if (nextHours < 0.125f) {
            return 0;
        } else if (offset < 0.125f) {
            return floored;
        } else if (offset < 0.375f) {
            return floored + 0.5f;
        }
        return (float) Math.ceil(nextHours);
    }

    @Override
    public int compareTo(Task t) {
        return t.getPriority() - getPriority();
    }

    @Override
    public byte[] serialize() {
        byte[] message = new byte[getSize()];
        Helper.writeLongToBytes(message, id, 4, 0);
        Helper.writeLongToBytes(message, dueDate.getTimeInMillis(), 8, 4);
        Helper.writeLongToBytes(message, priority, 4, 12);
        Helper.writeFloatToBytes(message, hoursRequired, 16);
        Helper.writeFloatToBytes(message, hoursCompleted, 20);
        Helper.writeStringToBytes(message, description, 24);
        return message;
    }

    @Override
    public int getSize() {
        return 24 + description.length();
    }
}