package com.bencarlisle.timelibrary.main;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import java.util.Objects;

public class EventAlarmManager extends BroadcastReceiver {

    private final static int MINUTES_BEFORE_EVENT = 5;
    private final static long MILLIS_BEFORE_EVENT = MINUTES_BEFORE_EVENT * 60000;

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Alarm", "HAS GONE OFF");
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        Objects.requireNonNull(v).vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_RING, 100);
        toneG.startTone(ToneGenerator.TONE_PROP_BEEP, 100);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        toneG.startTone(ToneGenerator.TONE_PROP_BEEP, 50);
        Intent activityIntent = new Intent(context, EventWake.class);
        activityIntent.putExtra("event", intent.getByteArrayExtra("event"));
        activityIntent.putExtra("startsIn", MINUTES_BEFORE_EVENT);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(activityIntent);
    }

    public static PendingIntent createPendingIntent(Context context, Event event) {
        Intent intent = new Intent(context, EventAlarmManager.class);
        intent.putExtra("event", event.serialize());
        return PendingIntent.getBroadcast(context, event.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    public static void addAlarm(Context context, Event event) {
        long timeUntilEvent = Helper.millisUntil(event.getStartTime());
        long timeUntilAlarm = timeUntilEvent - MILLIS_BEFORE_EVENT;
        if (timeUntilEvent < 0) {
            return;
        } else if (timeUntilAlarm < 0) {
            Intent intent = new Intent(context, EventAlarmManager.class);
            intent.putExtra("event", event.serialize());
            new EventAlarmManager().onReceive(context, intent);
            return;
        }
        AlarmManager processTimer = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Objects.requireNonNull(processTimer).set(AlarmManager.RTC_WAKEUP, timeUntilAlarm, createPendingIntent(context, event));
    }

    public static void removeAlarm(Context context, Event event) {
        AlarmManager processTimer = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Objects.requireNonNull(processTimer).cancel(createPendingIntent(context, event));
    }
}
