package com.bencarlisle.timehack.main;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.bencarlisle.timelibrary.main.DataControllable;
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

public class SharedDataControl implements DataControllable {

    private Context context;
    private WearMessageHandler messageHandler;
    private final static int MAX_RETRIES = 5;
    private static volatile int ID = new Random().nextInt();

    public SharedDataControl(Context context) {
        this.context = context;
        messageHandler = new WearMessageHandler();
    }

    public ArrayList<Event> getEvents() {
        byte[] result = sendAndReceive("getEvents");
        if (result == null) {
            return null;
        }
        ArrayList<Event> events = new ArrayList<>();
        ArrayList<byte[]> bytes = Helper.readList(result);
        for (byte[] byteArray: bytes) {
            events.add(new Event(byteArray, false));
        }
        return events;
    }

    public ArrayList<Returnable> getReturnables() {
        byte[] result = sendAndReceive("getReturnables");
        if (result == null) {
            return null;
        }
        ArrayList<Returnable> returnables = new ArrayList<>();
        ArrayList<byte[]> bytes = Helper.readList(result);
        for (byte[] byteArray: bytes) {
            returnables.add(new Returnable(byteArray, false));
        }
        return returnables;
    }

    public ArrayList<Task> getTasks() {
        byte[] result = sendAndReceive("getTasks");
        if (result == null) {
            return null;
        }
        ArrayList<Task> tasks = new ArrayList<>();
        ArrayList<byte[]> bytes = Helper.readList(result);
        for (byte[] byteArray: bytes) {
            tasks.add(new Task(byteArray, false));
        }
        return tasks;
    }

    public void removeEvent(int id) {
        sendAndReceive("removeEvent" + id);
    }

    public void removeReturnable(int id) {
        sendAndReceive("removeReturnable" + id);
    }

    public void removeTask(int id) {
        sendAndReceive("removeTask" + id);
    }

    public void addEvent(Event event) {
        sendByteMessage("addEvent", event.serialize());
    }

    public void addReturnable(Returnable returnable) {
        sendByteMessage("addReturnable", returnable.serialize());
    }

    public void addTask(Task task) {
        sendByteMessage("addTask", task.serialize());
    }

    private void sendByteMessage(String message, byte[] data) {
        for (int i = 0; i < MAX_RETRIES; i++) {
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/bencarlisle");
            DataMap dataMap = putDataMapRequest.getDataMap();
            int id = getNextId();
            Log.e("Wear", "Sending byte message: " + message + " id: " + id + " retry: " + i);
            dataMap.putInt("id", id);
            dataMap.putBoolean("isResponse", false);
            dataMap.putByteArray("message", message.getBytes());
            dataMap.putByteArray("data", data);
            Wearable.getDataClient(context).putDataItem(putDataMapRequest.asPutDataRequest());
            if (receiveMessage(id) != null) {
                return;
            }
        }
        showNoResponse(context);
    }

    private byte[] sendAndReceive(String message) {
        for (int i = 0; i < MAX_RETRIES; i++) {
            int id = getNextId();
            Log.e("Wear", "Sending message: " + message + " id: " + id + " retry: " + i);
            sendMessage(message, id);
            byte[] response = receiveMessage(id);
            if (response != null) {
                return response;
            }
        }
        showNoResponse(context);
        return null;
    }

    private byte[] receiveMessage(int id) {
        long startTime = Calendar.getInstance().getTimeInMillis();
        while (!messageHandler.hasMessage(id) && Calendar.getInstance().getTimeInMillis() - startTime < 1000) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return messageHandler.getMessage(id);
    }

    private void sendMessage(String message, int id) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/bencarlisle");
        DataMap dataMap = putDataMapRequest.getDataMap();
        dataMap.putInt("id", id);
        dataMap.putBoolean("isResponse", false);
        dataMap.putByteArray("message", message.getBytes());
        Wearable.getDataClient(context).putDataItem(putDataMapRequest.asPutDataRequest());
    }

    private void showNoResponse(final Context context) {
        Log.e("Wear", "No response detected");
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> Toast.makeText(context, "No response detected", Toast.LENGTH_SHORT).show());
    }

    private synchronized static int getNextId() {
        return ID++;
    }
}
