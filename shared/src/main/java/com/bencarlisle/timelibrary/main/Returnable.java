package com.bencarlisle.timelibrary.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.api.services.calendar.model.Event;

import java.util.ArrayList;
import java.util.Arrays;

public class Returnable implements Serializable {

    private boolean[] days;
    private Event event;

    public Returnable(boolean[] days, Event event) {
        this.days = days;
        this.event = event;
    }


    public Returnable(String bitfield, Event event) {
        boolean[] days = new boolean[bitfield.length()];
        for (int i = 0; i < bitfield.length(); i++) {
            days[i] = (bitfield.charAt(i) == '1');
        }
        this.days = days;
        this.event = event;
    }

    public Returnable(byte[] message) {
        String bitfield = Helper.readStringFromBytes(message, 11, 0);
        boolean[] days = new boolean[bitfield.length()];
        for (int i = 0; i < bitfield.length(); i++) {
            days[i] = (bitfield.charAt(i) == '1');
        }
        this.days = days;
        byte[] eventArray = Arrays.copyOfRange(message, 7, message.length);
        this.event = Helper.readEvent(eventArray);
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Returnable)) {
            return false;
        }
        Returnable returnable = (Returnable) o;
        return Arrays.equals(days, returnable.days) && event.equals(returnable.event);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(days);
        result = 31 * result + event.hashCode();
        return result;
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

    @Override
    public byte[] serialize() {
        byte[] message = new byte[getSize()];
        Helper.writeStringToBytes(message, getBitfield(), 0);
        byte[] eventArray = Helper.serializeEvent(event);
        Helper.writeBytesToBytes(message, eventArray, 7);
        return message;
    }

    @Override
    public int getSize() {
        return 7 + Helper.getSize(event);
    }
}
