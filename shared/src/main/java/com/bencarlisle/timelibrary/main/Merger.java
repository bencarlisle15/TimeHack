package com.bencarlisle.timelibrary.main;

public class Merger extends Thread {

    private Pollable pollable;
    private int millisToWait;

    public Merger(Pollable pollable, int secondsToWait) {
        this.pollable = pollable;
        this.millisToWait= secondsToWait * 1000;
    }

    public void run() {
        pollable.poll();
        while (true) {
            try {
                Thread.sleep(millisToWait);
            } catch (InterruptedException e) {
                return;
            }
            pollable.poll();
        }
    }
}
