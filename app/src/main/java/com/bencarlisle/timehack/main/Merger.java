package com.bencarlisle.timehack.main;

public class Merger {

    private Pollable pollable;

    public Merger(Pollable pollable) {
        this.pollable = pollable;
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
            pollable.poll();
        }
    }
}
