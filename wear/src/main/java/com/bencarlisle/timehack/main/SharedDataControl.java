package com.bencarlisle.timehack.main;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.bencarlisle.timelibrary.main.DataControllable;
import com.bencarlisle.timelibrary.main.Helper;
import com.bencarlisle.timelibrary.main.Returnable;
import com.bencarlisle.timelibrary.main.Task;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.api.services.calendar.model.Event;

import java.util.Calendar;
import java.util.Random;

public class SharedDataControl implements DataControllable {

    private Context context;
    private WearMessageHandler messageHandler;
    private static volatile int ID = new Random().nextInt();

    SharedDataControl(Context context) {
        this.context = context;
        messageHandler = new WearMessageHandler();
    }

    public void addEvent(Event event) {
        sendByteMessage("addEvent", Helper.serializeEvent(event));
    }

    public void addReturnable(Returnable returnable) {
        sendByteMessage("addReturnable", returnable.serialize());
    }

    public void addTask(Task task) {
        sendByteMessage("addTask", task.serialize());
    }

    private void sendByteMessage(String message, byte[] data) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/bencarlisle");
        DataMap dataMap = putDataMapRequest.getDataMap();
        int id = getNextId();
        dataMap.putInt("id", id);
        dataMap.putBoolean("isResponse", false);
        dataMap.putByteArray("message", message.getBytes());
        dataMap.putByteArray("data", data);
        Wearable.getDataClient(context).putDataItem(putDataMapRequest.asPutDataRequest());
        if (!receivedMessage(id)) {
            showNoResponse(context);
        } else {
            showResponse(context);
        }
    }

    private boolean receivedMessage(int id) {
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

    private void showNoResponse(final Context context) {
        makeToast(context, "No response detected");
    }

    private  void showResponse(final Context context) {
        makeToast(context, "Successfully sent");
    }

    private void makeToast(final Context context, String str) {
        Log.e("Wear", str);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> Toast.makeText(context, str, Toast.LENGTH_SHORT).show());
    }

    private synchronized static int getNextId() {
        return ID++;
    }
}
