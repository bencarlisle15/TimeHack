package com.bencarlisle.timehack;

class Merger {

    private CalendarModel calendarModel;

    public Merger(CalendarModel calendarModel) {
        this.calendarModel = calendarModel;
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
            calendarModel.poll();
        }
    }
}
