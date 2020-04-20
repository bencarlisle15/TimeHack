package com.bencarlisle.timelibrary.main;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Task implements Comparable<Task>, Serializable {

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
    }


    public Task(long dueDateMillis, String description, int priority, float hoursRequired, float hoursCompleted) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dueDateMillis);
        this.dueDate = calendar;
        this.description = description;
        this.priority = priority;
        this.hoursRequired = hoursRequired;
        this.hoursCompleted = hoursCompleted;
    }

    public Task(byte[] message) {
        long dueDate = Helper.readLongFromBytes(message, 8, 0);
        this.priority = (int) Helper.readLongFromBytes(message, 4, 8);
        this.hoursRequired = Helper.readFloatFromBytes(message, 12);
        this.hoursCompleted = Helper.readFloatFromBytes(message, 16);
        this.description = Helper.readStringFromBytes(message, message.length, 20);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dueDate);
        this.dueDate = calendar;
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Task)) {
            return false;
        }
        Task task = (Task) o;
        return task.dueDate.get(Calendar.YEAR) == dueDate.get(Calendar.YEAR) && task.dueDate.get(Calendar.DAY_OF_YEAR) == dueDate.get(Calendar.DAY_OF_YEAR) && task.getDescription().equals(description);
    }

    @Override
    public int hashCode() {
        int hashCode = dueDate.get(Calendar.YEAR);
        hashCode = 31 * hashCode + dueDate.get(Calendar.DAY_OF_YEAR);
        hashCode = 31 * hashCode + description.hashCode();
        return hashCode;
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
        Helper.writeLongToBytes(message, dueDate.getTimeInMillis(), 8, 0);
        Helper.writeLongToBytes(message, priority, 4, 8);
        Helper.writeFloatToBytes(message, hoursRequired, 12);
        Helper.writeFloatToBytes(message, hoursCompleted, 16);
        Helper.writeStringToBytes(message, description, 20);
        return message;
    }

    @Override
    public int getSize() {
        return 20 + description.length();
    }
}
