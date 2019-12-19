package com.bencarlisle.timehack.tasks;

import android.util.Log;

class Merger {

    private TaskModel taskModel;

    public Merger(TaskModel taskModel) {
        this.taskModel = taskModel;
    }

    public void start() {
        new Thread(this::runAlarm).start();
    }

    private void runAlarm() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            taskModel.poll();
        }
    }
}
