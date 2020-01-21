package com.bencarlisle.timelibrary.main;

import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
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

    public static void writeLongToBytes(byte[] message, long value, int size, int pos) {
        //todo negatives
        for (int i = size - 1; i >= 0; i--) {
            message[pos + i] = (byte) (value % 256);
            value /= 256;
        }
    }

    public static void writeStringToBytes(byte[] message, String str, int pos) {
        for (int i = 0; i < str.length(); i++) {
            message[pos++] = (byte) str.charAt(i);
        }
    }

    public static void writeFloatToBytes(byte[] message, float value, int pos) {
        byte[] floatArray = ByteBuffer.allocate(4).putFloat(value).array();
        writeBytesToBytes(message, floatArray, pos);
    }

    public static void writeBytesToBytes(byte[] message, byte[] original, int pos) {
        for (byte b : original) {
            message[pos++] = b;
        }
    }

    public static long readLongFromBytes(byte[] message, int size, int pos) {
        long val = 0;
        for (int i = pos; i < pos + size; i++) {
            val = 256 * val + ((message[i] + 256) % 256);
        }
        Log.e("Wear", "Reading long " + val);
        return val;

    }

    public static String readStringFromBytes(byte[] message, int endPos, int pos) {
        //todo wrong
        StringBuilder str = new StringBuilder();
        for (int i = pos; i < endPos; i++) {
            str.append((char) message[i]);
        }
        return str.toString();
    }

    public static float readFloatFromBytes(byte[] message, int pos) {
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

    private static byte[] serializeWithLength(Serializable serializable) {
        int size = serializable.getSize();
        byte[] message = new byte[size + 4];
        Helper.writeLongToBytes(message, size, 4, 0);
        byte[] serialized = serializable.serialize();
        Helper.writeBytesToBytes(message, serialized, 4);
        return message;
    }
}
