package com.bencarlisle.timehack.day;

import java.util.Calendar;

class Resetter {

    private CalendarModel calendarModel;

    public Resetter(CalendarModel calendarModel) {
        this.calendarModel = calendarModel;
    }

    public void start() {
        new Thread(this::runAlarm).start();
    }

    private void runAlarm() {
        while (true) {
            try {
                Thread.sleep(getMillisToMidnight());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            calendarModel.clear();
        }
    }

    private static long getMillisToMidnight() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
    }
}
