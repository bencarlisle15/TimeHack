package com.bencarlisle.timehack.main;

import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.HashMap;

public class WearMessageHandler extends WearableListenerService {

    private static HashMap<Integer, byte[]> messages = new HashMap<>();

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            Log.e("Wear", "Change");
            if (event.getType() == DataEvent.TYPE_CHANGED ) {
                DataItem item = event.getDataItem();
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                if (!dataMap.getBoolean("isResponse")) {
                    continue;
                }
                Log.e("Wear", "Received new message: " + dataMap.getInt("id") + ":" + (dataMap.getByteArray("message") == null));
                messages.put(dataMap.getInt("id"), dataMap.getByteArray("message"));
                Wearable.getDataClient(this).deleteDataItems(item.getUri());
            }
        }
    }

    public boolean hasMessage(int id) {
        return messages.containsKey(id);
    }

    public byte[] getMessage(int id) {
        if (hasMessage(id)) {
            return messages.remove(id);
        }
        return null;

    }
}
