package com.bencarlisle.timehack.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Returnable {

    private static int RETURNABLE_ID;
    private int id;
    private boolean[] days;
    private Event event;

    public Returnable(boolean[] days, Event event) {
        this.days = days;
        this.event = event;
        this.id = RETURNABLE_ID++;
    }


    public Returnable(int id, String bitfield, Event event) {
        boolean[] days = new boolean[bitfield.length()];
        for (int i = 0; i < bitfield.length(); i++) {
            days[i] = (bitfield.charAt(i) == '1');
        }
        this.days = days;
        this.event = event;
        this.id = id;
    }

    public static void setReturnableId(int returnableId) {
        RETURNABLE_ID = returnableId;
    }

    public boolean[] getDays() {
        return days;
    }

    public boolean isHappeningOnDay(int day) {
        return days[day];
    }

    public String getBitfield() {
        StringBuilder ans = new StringBuilder();
        for (boolean day: days) {
            ans.append(day ? "1" : "0");
        }
        return ans.toString();
    }

    public Event getEvent() {
        return event;
    }

    public int getId() {
        return id;
    }

    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        return (o instanceof Returnable) && o.hashCode() == hashCode();
    }

    @NonNull
    @Override
    public String toString() {
        return event + " on " + getDaysString();
    }

    public String getDaysString() {
        ArrayList<String> daysList = new ArrayList<>();
        for (int i = 0; i < days.length; i++) {
            if (days[i]) {
                daysList.add(convertDayToString(i));
            }
        }
        if (daysList.size() == 0) {
            return "no days";
        }
        return String.join("", daysList);
    }

    private String convertDayToString(int i) {
        switch (i) {
            case 0:
                return "S";
            case 1:
                return "M";
            case 2:
                return "T";
            case 3:
                return "W";
            case 4:
                return "Th";
            case 5:
                return "F";
            case 6:
                return "Sa";
            default:
                return "X";
        }
    }
}
