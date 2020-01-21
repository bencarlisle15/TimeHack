package com.bencarlisle.timehack.main;

import android.util.Log;

import com.bencarlisle.timelibrary.main.DataControl;
import com.bencarlisle.timelibrary.main.Event;
import com.bencarlisle.timelibrary.main.Helper;
import com.bencarlisle.timelibrary.main.Returnable;
import com.bencarlisle.timelibrary.main.Task;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageHandler extends WearableListenerService {

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                handleDataChange(event.getDataItem());
            }
        }
    }

    private void handleDataChange(DataItem dataItem) {
        DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
        if (dataMap.getBoolean("isResponse")) {
            return;
        }
        int id = dataMap.getInt("id");
        Log.e("App", "Received message: " + new String(dataMap.getByteArray("message")) + ":" + id);
        byte[] response = handleMessage(dataMap);
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/bencarlisle");
        dataMap = putDataMapRequest.getDataMap();
        dataMap.putInt("id", id);
        dataMap.putBoolean("isResponse", true);
        dataMap.putByteArray("message", response);
        Wearable.getDataClient(this).putDataItem(putDataMapRequest.asPutDataRequest());
        Log.e("App", "Send message " + id);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private byte[] handleMessage(DataMap dataMap) {
        String data = new String(dataMap.getByteArray("message"));
        byte[] response = null;
        DataControl dataControl = new DataControl(this);
        if (data.equals("getEvents")) {
            ArrayList<Event> events = dataControl.getEvents();
            response = Helper.serializeList(events);
        } else if (data.equals("getReturnables")) {
            ArrayList<Returnable> returnables = dataControl.getReturnables();
            response = Helper.serializeList(returnables);
        } else if (data.equals("getTasks")) {
            ArrayList<Task> tasks = dataControl.getTasks();
            response = Helper.serializeList(tasks);
        } else if (data.equals("getNextEventId")) {
            int eventId = dataControl.getNextEventId();
            response = String.valueOf(eventId).getBytes();
        } else if (data.equals("getNextReturnableId")) {
            int returnableId = dataControl.getNextReturnableId();
            response = String.valueOf(returnableId).getBytes();
        } else if (data.equals("getNextTaskId")) {
            int taskId = dataControl.getNextTaskId();
            response = String.valueOf(taskId).getBytes();
        } else if (data.matches("^removeEvent(\\d+)$")) {
            Matcher matcher = Pattern.compile("^removeEvent(\\d+)$").matcher(data);
            matcher.matches();
            int id = Integer.parseInt(Objects.requireNonNull(matcher.group(1)));
            dataControl.removeEvent(id);
        } else if (data.matches("^removeReturnable(\\d+)$")) {
            Matcher matcher = Pattern.compile("removeReturnable(\\d+)$").matcher(data);
            matcher.matches();
            int id = Integer.parseInt(Objects.requireNonNull(matcher.group(1)));
            dataControl.removeReturnable(id);
        } else if (data.matches("^removeTask(\\d+)$")) {
            Matcher matcher = Pattern.compile("^removeTask(\\d+)$").matcher(data);
            matcher.matches();
            int id = Integer.parseInt(Objects.requireNonNull(matcher.group(1)));
            dataControl.removeTask(id);
        } else if (data.equals("addEvent")) {
            byte[] event = dataMap.getByteArray("data");
            Helper.printArray(data.getBytes());
            dataControl.addEvent(new Event(event, true));
        } else if (data.equals("addReturnable")) {
            byte[] returnable = dataMap.getByteArray("data");
            dataControl.addReturnable(new Returnable(returnable, true));
        } else if (data.equals("addTask")) {
            byte[] task = dataMap.getByteArray("data");
            dataControl.addTask(new Task(task, true));
        } else {
            Log.e("App", "Command: '" + data + "' not found");
        }
        return response;
    }
}
