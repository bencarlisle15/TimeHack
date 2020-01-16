package com.bencarlisle.timehack.main;

import android.content.Context;
import android.util.Log;

import com.bencarlisle.timelibrary.main.DataControl;
import com.bencarlisle.timelibrary.main.DatabaseProvider;
import com.bencarlisle.timelibrary.main.Event;
import com.bencarlisle.timelibrary.main.Helper;
import com.bencarlisle.timelibrary.main.Returnable;
import com.bencarlisle.timelibrary.main.Task;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class SharedDataControl {

    private Context context;
    private WearMessageHandler messageHandler;

    public SharedDataControl(Context context) {
        this.context = context;
        messageHandler = new WearMessageHandler();
    }

    public void clearEvents() { }

    public ArrayList<Event> getEvents() {
        Log.e("Wear", "Getting events");
        byte[] result = sendAndReceive("getEvents");
        ArrayList<byte[]> bytes = Helper.readList(result);
        ArrayList<Event> events = new ArrayList<>();
        for (byte[] byteArray: bytes) {
            events.add(new Event(byteArray));
        }
        return events;
    }

    public ArrayList<Returnable> getReturnables() {
        byte[] result = sendAndReceive("getReturnables");
        ArrayList<byte[]> bytes = Helper.readList(result);
        ArrayList<Returnable> returnables = new ArrayList<>();
        for (byte[] byteArray: bytes) {
            returnables.add(new Returnable(byteArray));
        }
        return returnables;
    }

    public ArrayList<Task> getTasks() {
        byte[] result = sendAndReceive("getTasks");
        ArrayList<byte[]> bytes = Helper.readList(result);
        ArrayList<Task> tasks = new ArrayList<>();
        for (byte[] byteArray: bytes) {
            tasks.add(new Task(byteArray));
        }
        return tasks;
    }

    private int getNextEventId() {
        byte[] result = sendAndReceive(DataControl.getSQL("getNextEventId", null));
        return (int) Helper.readLongFromBytes(result, 4, 0);
    }

    private int getNextReturnableId() {
        byte[] result = sendAndReceive(DataControl.getSQL("getNextReturnableId", null));
        return (int) Helper.readLongFromBytes(result, 4, 0);
    }

    private int getNextTaskId() {
        byte[] result = sendAndReceive(DataControl.getSQL("getNextTaskId", null));
        return (int) Helper.readLongFromBytes(result, 4, 0);
    }

    public void removeEvent(int id) {
        sendSingleMessage("removeEvent" + id);
    }

    public void removeReturnable(int id) {
        sendSingleMessage("removeReturnable" + id);
    }

    public void removeTask(int id) {
        sendSingleMessage("removeTask" + id);
    }

    public void addEvent(Event event) {
        Log.e("DATA EVENT", "ADDING event" + event);
        sendSingleMessage("addEvent" + new String(Helper.serializeWithLength(event)));
    }

    public void addReturnable(Returnable returnable) {
        Log.e("DATA RETURNABLE", "ADDING returnable" + returnable);
        sendSingleMessage("addReturnable" + new String(Helper.serializeWithLength(returnable)));
    }

    public void addTask(Task task) {
        Log.e("DATA TASK", "ADDING TASK " + task);
        sendSingleMessage("addTask" + new String(Helper.serializeWithLength(task)));
    }

    private void sendSingleMessage(String message) {
        sendMessage(message, 0);
    }

    private byte[] sendAndReceive(String message) {
        int id = new Random().nextInt();
        Log.e("Wear", "Sending and receiving message: " + message + ":" + id);
        sendMessage(message, id);
        long startTime = Calendar.getInstance().getTimeInMillis();
        while (!messageHandler.hasMessage(id) && Calendar.getInstance().getTimeInMillis() - startTime < 5000) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.e("Wear", "Getting message " + id);
        byte[] response = messageHandler.getMessage(id);
        if (response == null) {
            Log.e("Wear", "No response detected");
        }
        return response;
    }

    private void sendMessage(String message, int id) {
        Log.e("Wear", "Sending message: " + message + ":" + id);
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/bencarlisle");
        DataMap dataMap = putDataMapRequest.getDataMap();
        dataMap.putInt("id", id);
        dataMap.putBoolean("isResponse", false);
        dataMap.putByteArray("message", message.getBytes());
        Wearable.getDataClient(context).putDataItem(putDataMapRequest.asPutDataRequest());
    }
}
