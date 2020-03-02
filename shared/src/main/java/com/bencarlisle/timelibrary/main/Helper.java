package com.bencarlisle.timelibrary.main;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class Helper {

    public static String convertTimeToString(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR);
        if (hour == 0) {
            hour = 12;
        }
        String hourString = String.valueOf(hour);
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
        return (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DATE) +"/" + calendar.get(Calendar.YEAR);
    }

    static void writeLongToBytes(byte[] message, long value, int size, int pos) {
        //todo negatives
        for (int i = size - 1; i >= 0; i--) {
            message[pos + i] = (byte) (value % 256);
            value /= 256;
        }
    }

    static void writeStringToBytes(byte[] message, String str, int pos) {
        for (int i = 0; i < str.length(); i++) {
            message[pos++] = (byte) str.charAt(i);
        }
    }

    static void writeFloatToBytes(byte[] message, float value, int pos) {
        byte[] floatArray = ByteBuffer.allocate(4).putFloat(value).array();
        writeBytesToBytes(message, floatArray, pos);
    }

    static void writeBytesToBytes(byte[] message, byte[] original, int pos) {
        for (byte b : original) {
            message[pos++] = b;
        }
    }

    static long readLongFromBytes(byte[] message, int size, int pos) {
        long val = 0;
        for (int i = pos; i < pos + size; i++) {
            val = 256 * val + ((message[i] + 256) % 256);
        }
        return val;

    }

    static String readStringFromBytes(byte[] message, int endPos, int pos) {
        //todo wrong
        StringBuilder str = new StringBuilder();
        for (int i = pos; i < endPos; i++) {
            str.append((char) message[i]);
        }
        return str.toString();
    }

    static float readFloatFromBytes(byte[] message, int pos) {
        byte[] floatArray = Arrays.copyOfRange(message, pos, pos + 4);
        return ByteBuffer.wrap(floatArray).getFloat();
    }

    public static byte[] serializeList(ArrayList<? extends Serializable> list) {
        if (list.size() == 0) {
            return new byte[]{0, 0, 0, 0};
        }
        ArrayList<byte[]> bytes = new ArrayList<>();
        int totalSize = 4;
        for (Serializable serializable: list) {
            byte[] message = serializeWithLength(serializable);
            bytes.add(message);
            totalSize += message.length;
        }
        byte[] message = new byte[totalSize];
        Helper.writeLongToBytes(message, bytes.size(), 4, 0);
        int pos = 4;
        for (byte[] byteArray: bytes) {
            Helper.writeBytesToBytes(message, byteArray, pos);
            pos += byteArray.length;
        }
        return message;
    }

    public static ArrayList<byte[]> readList(byte[] message) {
        ArrayList<byte[]> byteArrays = new ArrayList<>();
        int listSize = (int) Helper.readLongFromBytes(message, 4, 0);
        int pos = 4;
        for (int i = 0; i < listSize; i++) {
            int size = (int) Helper.readLongFromBytes(message, 4, pos);
            byte[] byteArray = Arrays.copyOfRange(message, pos + 4, pos + 4 + size);
            byteArrays.add(byteArray);
            pos += 4 + size;
        }
        return byteArrays;
    }

    public static void printArray(byte[] bytes) {
        StringBuilder str = new StringBuilder();
        for (byte b: bytes) {
            str.append(Byte.toUnsignedInt(b));
            str.append(' ');
        }
        Log.e("ARRAY", str.toString());
    }

    public static void makeToast(final Context context, String str) {
        Log.e("Toast", str);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> Toast.makeText(context, str, Toast.LENGTH_SHORT).show());
    }

    private static byte[] serializeWithLength(Serializable serializable) {
        int size = serializable.getSize();
        byte[] message = new byte[size + 4];
        Helper.writeLongToBytes(message, size, 4, 0);
        byte[] serialized = serializable.serialize();
        Helper.writeBytesToBytes(message, serialized, 4);
        return message;
    }

    static long millisUntil(Calendar time) {
        Log.e("DIFFERENCE IS", time.getTimeInMillis() - Calendar.getInstance().getTimeInMillis() + " millis");
        return time.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
    }

    public static byte[] serializeEvent(Event event) {
        int size = getSize(event);
        byte[] eventMessage = new byte[size];
        int taskId = Integer.parseInt(event.getDescription());
        Helper.writeLongToBytes(eventMessage, taskId, 4, 0);
        Helper.writeLongToBytes(eventMessage, event.getStart().getDateTime().getValue(), 8, 4);
        Helper.writeLongToBytes(eventMessage, event.getEnd().getDateTime().getValue(), 8, 12);
        Helper.writeStringToBytes(eventMessage, event.getSummary(), 20);
        return eventMessage;
    }

    public static Event readEvent(byte[] message) {
        int taskId = (int) Helper.readLongFromBytes(message, 4, 0);
        long startTime = Helper.readLongFromBytes(message, 8, 4);
        long endTime = Helper.readLongFromBytes(message, 8, 12);
        String summary = Helper.readStringFromBytes(message, message.length, 20);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        Calendar start = calendar;
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(endTime);
        Calendar end = calendar;
        return getEvent(start, end, summary, taskId);
    }

    public static Event readFuture(byte[] message) {
        int taskId = (int) Helper.readLongFromBytes(message, 4, 0);
        long startTime = Helper.readLongFromBytes(message, 8, 4);
        long endTime = Helper.readLongFromBytes(message, 8, 12);
        String summary = Helper.readStringFromBytes(message, message.length, 20);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        Calendar start = calendar;
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(endTime);
        Calendar end = calendar;
        return getFuture(start, end, summary, taskId);
    }

    static int getSize(Event event) {
        return 20 + event.getSummary().length();
    }

    public static boolean isTask(Event event) {
        return !event.getDescription().equals("-1");
    }

    private static Event getEvent(EventDateTime startTime, EventDateTime endTime, String summary, int taskId) {
        Event event= new Event().setSummary(summary);
        event.setStart(startTime);
        event.setEnd(endTime);
        event.setDescription(String.valueOf(taskId));
        return event;
    }

    public static Event getEvent(Calendar startTime, Calendar endTime, String description, int taskId) {
        return getEvent(getDateTime(startTime), getDateTime(endTime), description, taskId);
    }

    public static Calendar getCalendar(EventDateTime eventDateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(eventDateTime.getDateTime().getValue());
        return calendar;
    }


    private static EventDateTime getDateTime(Calendar calendar) {
        Calendar currentDate = Calendar.getInstance();
        calendar.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
        calendar.set(Calendar.DAY_OF_YEAR, currentDate.get(Calendar.DAY_OF_YEAR));
        Date date = calendar.getTime();
        DateTime dateTime = new DateTime(date);
        return new EventDateTime().setDateTime(dateTime).setTimeZone("America/New_York");
    }

    private static EventDateTime getDateAndTime(Calendar calendar) {
        Date date = calendar.getTime();
        DateTime dateTime = new DateTime(date);
        return new EventDateTime().setDateTime(dateTime).setTimeZone("America/New_York");
    }

    public static Calendar initCalendar(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar;
    }

    public static boolean isEqualDates(EventDateTime dateTime1, EventDateTime dateTime2) {
        long time1 = dateTime1.getDate().getValue();
        long time2 = dateTime2.getDate().getValue();
        return time1 == time2;
    }

    public static Event getFuture(Calendar startTime, Calendar endTime, String description, int taskId) {
        return getEvent(getDateAndTime(startTime), getDateAndTime(endTime), description, taskId);
    }
}
