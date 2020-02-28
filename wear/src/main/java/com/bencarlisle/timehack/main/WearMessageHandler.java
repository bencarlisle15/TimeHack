package com.bencarlisle.timehack.main;

import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;

public class WearMessageHandler extends WearableListenerService {

    private static ArrayList<Integer> messages = new ArrayList<>();

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED ) {
                DataItem item = event.getDataItem();
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                if (!dataMap.getBoolean("isResponse")) {
                    continue;
                }
                Log.e("Wear", "Received new message: " + dataMap.getInt("id"));
                messages.add(dataMap.getInt("id"));
                Wearable.getDataClient(this).deleteDataItems(item.getUri());
            }
        }
    }

    public boolean hasMessage(int id) {
        return messages.contains(id);
    }

    public boolean getMessage(int id) {
        if (hasMessage(id)) {
            messages.remove((Integer) id);
            return true;
        }
        return false;

    }
}
