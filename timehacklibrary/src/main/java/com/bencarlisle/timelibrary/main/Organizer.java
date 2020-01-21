package com.bencarlisle.timelibrary.main;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class Organizer extends IntentService {

    private static boolean isStarted = false;
    private final static int RECOMMENDED_START = 10;
    private final static int RECOMMENDED_END = 23;
    private final static float TIME_BUFFER = 10;
    private final static float TIME_BUFFER_HOURS = TIME_BUFFER / 60.0f;

    public Organizer() {
        super("Organizer");
        if (!isStarted) {
            new Thread(this::runAlarm).start();
            isStarted = true;
        }
    }

    public void start() {
        new Thread(this::runAlarm).start();
    }

    private void runAlarm() {
        DataControl dataControl = new DataControl(this);
        boolean isAlreadyRun = dataControl.isAlreadyRun();
        dataControl.close();
        if (!isAlreadyRun) {
            runTaskAdder();
        }
        while (true) {
            try {
                Thread.sleep(getMillisToMidnight());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runTaskAdder();
        }
    }

    private void runTaskAdder() {
//        Log.e("RESETTER", "RUNNING");
        DataControl dataControl = new DataControl(this);
        for (Event event: dataControl.getTaskEvents()) {
            dataControl.addTaskHours(event.getTaskId(), getHoursBetween(event.getStartTime(), event.getEndTime()));
        }
        dataControl.cleanTasks();
        dataControl.clearEvents();
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        ArrayList<Returnable> returnables = dataControl.getReturnablesOnDay(day);
        TreeSet<Event> events = new TreeSet<>();

        for (Returnable returnable: returnables) {
            dataControl.addEvent(returnable.getEvent());
            events.add(returnable.getEvent());
        }

        TreeSet<Task> tasks = new TreeSet<>(dataControl.getTasks());

        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, RECOMMENDED_START);
        start.set(Calendar.MINUTE, 0);

        Calendar end = Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY, RECOMMENDED_END);
        end.set(Calendar.MINUTE, 0);

//        TreeSet<Task> unAddedTasks = new TreeSet<>();

        while (!tasks.isEmpty()) {
            Task task = tasks.first();
            Event event = findSpot(events, task, start, end);
            tasks.remove(task);
            if (event != null) {
                dataControl.addEvent(event);
                events.add(event);
//            } else {
//                unAddedTasks.add(task);
            }
        }
        dataControl.runOrganizer();
        dataControl.close();
    }

    private Event findSpot(TreeSet<Event> events, Task task, Calendar start, Calendar end) {
        float nextHours = task.getNextHours();
        if (events.size() == 0 || getHoursBetween(start, events.first().getStartTime()) > TIME_BUFFER_HOURS + nextHours) {
            return createEvent(start, nextHours, task);
        }
        Event lastEvent = events.first();
        Iterator<Event> eventIterator = events.iterator();
        eventIterator.next();

        while (eventIterator.hasNext()) {
            Event event = eventIterator.next();
            if (getHoursBetween(lastEvent.getEndTime(), event.getStartTime()) > TIME_BUFFER_HOURS + nextHours) {
                return createEvent(lastEvent.getEndTime(), nextHours, task);
            }
            lastEvent = event;
        }

        if (getHoursBetween(lastEvent.getEndTime(), end) > TIME_BUFFER_HOURS + nextHours) {
            return createEvent(lastEvent.getEndTime(), nextHours, task);
        }
        if (task.getDaysLeft() == 1 || nextHours > 2 || task.getPriority() > 3) {
            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, start.get(Calendar.HOUR_OF_DAY));
            startTime.set(Calendar.MINUTE, start.get(Calendar.MINUTE));
            startTime.add(Calendar.HOUR, (int) (-nextHours));
            startTime.add(Calendar.MINUTE, (int) Math.floor(-60 * (nextHours % 1)));
            start.add(Calendar.MINUTE, (int) (-TIME_BUFFER / 2));
            return createEvent(startTime, nextHours, task);
        }
        return null;
    }

    private Event createEvent(Calendar time, float nextHours, Task task) {
        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
        start.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
        start.add(Calendar.MINUTE, (int) (TIME_BUFFER / 2));
        Calendar end = Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY, start.get(Calendar.HOUR_OF_DAY));
        end.set(Calendar.MINUTE, start.get(Calendar.MINUTE));
        end.add(Calendar.HOUR_OF_DAY, (int) nextHours);
        end.add(Calendar.MINUTE, (int) Math.ceil(60 * (nextHours % 1)));
        return new Event(start, end, task.getDescription(), task.getId());
    }

    private float getHoursBetween(Calendar start, Calendar end) {
        long millisDifference = end.getTimeInMillis() - start.getTimeInMillis();
        return (float) TimeUnit.MILLISECONDS.toHours(millisDifference);
    }

    private long getMillisToMidnight() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
//        return 10000;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        onTaskRemoved(intent);
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e("RESETTER", "HANDLING INTENT");
        if (!isStarted) {
            new Thread(this::runAlarm).start();
            isStarted = true;
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }
}
