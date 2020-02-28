package com.bencarlisle.timehack.main;

import android.util.Log;

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
        handleMessage(dataMap);
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/bencarlisle");
        dataMap = putDataMapRequest.getDataMap();
        dataMap.putInt("id", id);
        dataMap.putBoolean("isResponse", true);
        Wearable.getDataClient(this).putDataItem(putDataMapRequest.asPutDataRequest());
        Log.e("App", "Sending response for " + id);
    }

    private void handleMessage(DataMap dataMap) {
        String data = new String(dataMap.getByteArray("message"));
        DataControl dataControl = new DataControl(this);
        switch (data) {
            case "addEvent":
                byte[] event = dataMap.getByteArray("data");
                Helper.printArray(data.getBytes());
                dataControl.addEvent(Helper.readEvent(event));
                break;
            case "addReturnable":
                byte[] returnable = dataMap.getByteArray("data");
                dataControl.addReturnable(new Returnable(returnable, true));
                break;
            case "addTask":
                byte[] task = dataMap.getByteArray("data");
                dataControl.addTask(new Task(task, true));
                break;
            default:
                Log.e("App", "Command: '" + data + "' not found");
                break;
        }
    }
}
