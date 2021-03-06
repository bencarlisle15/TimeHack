package com.bencarlisle.timelibrary.main;

import android.util.Log;

import com.google.api.services.calendar.model.Event;

import java.time.YearMonth;
import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Parser {

    static Event parseEventResult(String result) {
        Matcher matcher = Pattern.compile("^([fF]rom )?(\\d{1,2})(:(\\d\\d))? (AM|PM|a m|p m|a\\.m\\.|p\\.m\\.) [tT]o (\\d{1,2})(:(\\d\\d))? (AM|PM|a m|p m|a\\.m\\.|p\\.m\\.) (.+)$").matcher(result);
        if (!matcher.find()) {
            return null;
        }
        int startHour = Integer.parseInt(Objects.requireNonNull(matcher.group(2)));
        String startMinuteString = matcher.group(4);
        int startMinute = 0;
        if (startMinuteString != null && startMinuteString.length() > 0) {
            startMinute = Integer.parseInt(startMinuteString);
        }
        String startAm = matcher.group(5);
        int endHour = Integer.parseInt(Objects.requireNonNull(matcher.group(6)));
        String endMinuteString = matcher.group(8);
        int endMinute = 0;
        if (endMinuteString != null && endMinuteString.length() > 0) {
            endMinute = Integer.parseInt(endMinuteString);
        }
        String endAm = matcher.group(9);
        String description = matcher.group(10);

        description = Objects.requireNonNull(description).substring(0, 1).toUpperCase() + description.substring(1);

        Calendar startTime = getTime(startHour, startMinute, startAm);
        Calendar endTime = getTime(endHour, endMinute, endAm);
        if (startTime == null || endTime == null) {
            return null;
        }
        return Helper.getEvent(startTime, endTime, description, -1);
    }

    static Task parseTaskResult(String str) {
        Matcher matcher = Pattern.compile("^[a|A]dd (.+) (due|to|do) ([A-Z][a-z]*) (\\d{1,2})[a-z][a-z] with (\\d{1,2}) hours and priority (\\d)\\.?$").matcher(str);
        if (!matcher.find()) {
            return null;
        }

        String description = matcher.group(1);
        int month = getMonth(Objects.requireNonNull(matcher.group(3)));
        int day = Integer.parseInt(Objects.requireNonNull(matcher.group(4)));
        int hoursRequired = Integer.parseInt(Objects.requireNonNull(matcher.group(5)));
        int priority = Integer.parseInt(Objects.requireNonNull(matcher.group(6)));

        description = Objects.requireNonNull(description).substring(0, 1).toUpperCase() + description.substring(1);

        Calendar dueDate = getDate(month, day);

        if (dueDate == null || hoursRequired < 0 || priority < 0) {
            return null;
        }
        Log.e("PARSER", new Task(dueDate , description, priority, hoursRequired, 0).toString() );
        return new Task(dueDate , description, priority, hoursRequired, 0);

    }

    private static int getMonth(String month) {
        if (month.equalsIgnoreCase("january")) {
            return 0;
        } else if (month.equalsIgnoreCase("february")) {
            return 1;
        } else if (month.equalsIgnoreCase("march")) {
            return 2;
        } else if (month.equalsIgnoreCase("april")) {
            return 3;
        } else if (month.equalsIgnoreCase("may")) {
            return 4;
        } else if (month.equalsIgnoreCase("june")) {
            return 5;
        } else if (month.equalsIgnoreCase("july")) {
            return 6;
        } else if (month.equalsIgnoreCase("august")) {
            return 7;
        } else if (month.equalsIgnoreCase("september")) {
            return 8;
        } else if (month.equalsIgnoreCase("october")) {
            return 9;
        } else if (month.equalsIgnoreCase("november")) {
            return 10;
        } else if (month.equalsIgnoreCase("december")) {
            return 11;
        }
        return -1;
    }

    private static Calendar getDate(int month, int day) {
        if (month < 0 ) {
            return null;
        }
        int monthLength = YearMonth.of(Calendar.getInstance().get(Calendar.YEAR), month + 1).lengthOfMonth();
        if (day <= 0 || day > monthLength) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, day);
        return calendar;
    }

    static Returnable parseReturnableResult(String result) {
        Matcher matcher = Pattern.compile("^([a-zA-Z ]+) from (\\d{1,2})(:(\\d{1,2}))? (AM|PM|a m|p m|a\\.m\\.|p\\.m\\.) [t|T]o (\\d{1,2})(:(\\d{1,2}))? (AM|PM|a m|p m|a\\.m\\.|p\\.m\\.) (.+)$").matcher(result);
        if (!matcher.find()) {
            return null;
        }
        String daysString = matcher.group(1);
        int startHour = Integer.parseInt(Objects.requireNonNull(matcher.group(2)));
        String startMinuteString = matcher.group(4);
        int startMinute = 0;
        if (startMinuteString != null && startMinuteString.length() > 0) {
            startMinute = Integer.parseInt(startMinuteString);
        }
        String startAm = matcher.group(5);
        int endHour = Integer.parseInt(Objects.requireNonNull(matcher.group(6)));
        String endMinuteString = matcher.group(8);
        int endMinute = 0;
        if (endMinuteString != null && endMinuteString.length() > 0) {
            endMinute = Integer.parseInt(endMinuteString);
        }
        String endAm = matcher.group(9);
        String description = matcher.group(10);

        description = Objects.requireNonNull(description).substring(0, 1).toUpperCase() + description.substring(1);

        boolean[] days = getDays(Objects.requireNonNull(daysString));
        Calendar startTime = getTime(startHour, startMinute, startAm);
        Calendar endTime = getTime(endHour, endMinute, endAm);
        if (startTime == null || endTime == null || days == null) {
            return null;
        }
        Event event = Helper.getEvent(startTime, endTime, description, -1);
        return new Returnable(days, event);
    }

    private static boolean[] getDays(String daysString) {
        String[] daysList = daysString.split(" ");
        boolean[] days = new boolean[7];
        if (daysList.length == 0) {
            return null;
        }
        for (String day: daysList) {
            int index = -1;
            if (day.equalsIgnoreCase("sunday")) {
                index = 0;
            } else if (day.equalsIgnoreCase("monday")) {
                index = 1;
            } else if (day.equalsIgnoreCase("tuesday")) {
                index = 2;
            } else if (day.equalsIgnoreCase("wednesday")) {
                index = 3;
            } else if (day.equalsIgnoreCase("thursday")) {
                index = 4;
            } else if (day.equalsIgnoreCase("friday")) {
                index = 5;
            } else if (day.equalsIgnoreCase("saturday")) {
                index = 6;
            }
            if (index == -1) {
                return null;
            }
            days[index] = true;
        }
        return days;
    }

    static Event parseFutureResult(String result) {
        Matcher matcher = Pattern.compile("^([A-Z][a-z]*) (\\d{1,2})[a-z][a-z] from (\\d{1,2})(:(\\d{1,2}))? (AM|PM|a m|p m|a\\.m\\.|p\\.m\\.) [t|T]o (\\d{1,2})(:(\\d{1,2}))? (AM|PM|a m|p m|a\\.m\\.|p\\.m\\.) (.+)$").matcher(result);
        if (!matcher.find()) {
            return null;
        }
        int month = getMonth(Objects.requireNonNull(matcher.group(1)));
        int day = Integer.parseInt(Objects.requireNonNull(matcher.group(2)));

        int startHour = Integer.parseInt(Objects.requireNonNull(matcher.group(3)));
        String startMinuteString = matcher.group(5);
        int startMinute = 0;
        if (startMinuteString != null && startMinuteString.length() > 0) {
            startMinute = Integer.parseInt(startMinuteString);
        }
        String startAm = matcher.group(6);
        int endHour = Integer.parseInt(Objects.requireNonNull(matcher.group(7)));
        String endMinuteString = matcher.group(9);
        int endMinute = 0;
        if (endMinuteString != null && endMinuteString.length() > 0) {
            endMinute = Integer.parseInt(endMinuteString);
        }
        String endAm = matcher.group(10);
        String description = matcher.group(11);

        description = Objects.requireNonNull(description).substring(0, 1).toUpperCase() + description.substring(1);

        Calendar date = getDate(month, day);
        Calendar startTime = getTime(startHour, startMinute, startAm);
        Calendar endTime = getTime(endHour, endMinute, endAm);
        if (startTime == null || endTime == null || date == null) {
            return null;
        }
        startTime.set(Calendar.DAY_OF_YEAR, date.get(Calendar.DAY_OF_YEAR));
        startTime.set(Calendar.YEAR, date.get(Calendar.YEAR));
        endTime.set(Calendar.DAY_OF_YEAR, date.get(Calendar.DAY_OF_YEAR));
        endTime.set(Calendar.YEAR, date.get(Calendar.YEAR));
        return Helper.getFuture(startTime, endTime, description, -1);
    }

    private static Calendar getTime(int hour, int minute, String am) {
        if (hour <= 0 || minute < 0 || hour > 12 || minute >= 60) {
            return null;
        }
        if (am.toLowerCase().charAt(0) == 'p') {
            hour += 12;
            hour %= 24;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar;
    }
}
