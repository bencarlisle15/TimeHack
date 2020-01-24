package com.bencarlisle.timehack.returnables;

import android.util.Log;

import com.bencarlisle.timelibrary.main.DataControllable;
import com.bencarlisle.timelibrary.main.Event;
import com.bencarlisle.timelibrary.main.Merger;
import com.bencarlisle.timelibrary.main.Pollable;
import com.bencarlisle.timelibrary.main.Returnable;

import java.util.ArrayList;
import java.util.Calendar;

abstract class ReturnablesModel implements Pollable {

    final ArrayList<Returnable> returnables = new ArrayList<>();
    DataControllable dataControl;

    protected abstract void addReturnableView(Returnable returnable);
    protected abstract void deleteReturnable(int id);

    ReturnablesModel(DataControllable dataControl) {
        this.dataControl = dataControl;
    }

    void startPolling(int secondsToWait) {
        new Merger(this, secondsToWait).start();
//        createTempEvent();
    }

    public void poll() {
        synchronized (returnables) {
            ArrayList<Returnable> newReturnables = dataControl.getReturnables();
            for (Returnable returnable: newReturnables) {
                if (!returnables.contains(returnable)) {
                    Log.e("RETURNABLE FOUND", returnable.toString());
                    returnables.add(returnable);
                    addReturnableView(returnable);
                }
            }
            for (int i = 0; i < returnables.size(); i++) {
                if (!newReturnables.contains(returnables.get(i))) {
                    Log.e("REMOVING TASK ", String.valueOf(returnables.get(i).getId()));
                    deleteReturnable(returnables.get(i).getId());
                }
            }
        }
    }

    private void createTempEvent() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Calendar time = Calendar.getInstance();
            time.set(Calendar.HOUR_OF_DAY, 3);
            time.set(Calendar.MINUTE, 0);
            Calendar endTime = Calendar.getInstance();
            endTime.set(Calendar.HOUR_OF_DAY, 3);
            endTime.set(Calendar.MINUTE, 45);
            Event event = new Event(time, endTime, "HELLO FRIEND", -1);
            boolean[] days = new boolean[]{false,true,false,true,false,true,false};
            addReturnable(new Returnable(days, event));
        }).start();
    }

    private void addReturnable(Returnable returnable) {
        dataControl.addReturnable(returnable);
        returnables.add(returnable);
        addReturnableView(returnable);
    }
}
