package com.bencarlisle.timehack.main;

import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

public class WearMessageHandler extends WearableListenerService {

    private static SparseArray<byte[]> messages = new SparseArray<>();

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
                messages.put(dataMap.getInt("id"), dataMap.getByteArray("message"));
                Wearable.getDataClient(this).deleteDataItems(item.getUri());
            }
        }
    }

    public boolean hasMessage(int id) {
        return messages.indexOfKey(id) != -1;
    }

    public byte[] getMessage(int id) {
        if (hasMessage(id)) {
            byte[] message = messages.get(id);
            messages.remove(id);
            return message;
        }
        return null;

    }
}
