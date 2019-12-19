package com.bencarlisle.timehack.tasks;

import java.util.Calendar;

public class Task {

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

    public static void setTaskId(int taskId) {
        TASK_ID = taskId;
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

    public String toString() {
        return description + " due at " + convertToString(dueDate) + " with priority " + priority;
    }

    public int hashCode() {
        return id;
    }

    public boolean equals(Object o) {
        return o instanceof Task && o.hashCode() == hashCode();
    }

    private String convertToString(Calendar time) {
        return time.get(Calendar.HOUR) + ":" + time.get(Calendar.MINUTE) + " " + (time.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
    }

    public int getHoursLeft() {
        return (int) Math.ceil(getHoursRequired() - getHoursCompleted());
    }
}
