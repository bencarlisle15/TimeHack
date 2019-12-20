package com.bencarlisle.timehack.main;

import android.util.Log;

import java.time.YearMonth;
import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Parser {

    static Event parseEventResult(String result) {
        Matcher matcher = Pattern.compile("^(from)? (\\d{1,2})(:(\\d{1,2}))? (AM|PM) to (\\d{1,2})(:(\\d{1,2}))? (AM|PM) (.*)$").matcher(result);
        if (!matcher.find()) {
            return null;
        }
        int startHour = Integer.parseInt(Objects.requireNonNull(matcher.group(2)));
        int startMinute = Integer.parseInt(Objects.requireNonNull(matcher.group(4)));
        String startAm = matcher.group(5);
        int endHour = Integer.parseInt(Objects.requireNonNull(matcher.group(6)));
        int endMinute = Integer.parseInt(Objects.requireNonNull(matcher.group(8)));
        String endAm = matcher.group(9);
        String description = matcher.group(10);

        Calendar startTime = getTime(startHour, startMinute, startAm);
        Calendar endTime = getTime(endHour, endMinute, endAm);
        if (startTime == null || endTime == null) {
            return null;
        }
        return new Event(startTime, endTime, description);
    }

    static Task parseTaskResult(String str) {
        Matcher matcher = Pattern.compile("^add (.+) (due|to|do) ([A-Z][a-z]*) (\\d{1,2})[a-z][a-z] with (\\d{1,2}) hours and priority (\\d)$").matcher(str);
        if (!matcher.find()) {
            return null;
        }

        String description = matcher.group(1);
        int month = getMonth(Objects.requireNonNull(matcher.group(3)));
        int day = Integer.parseInt(Objects.requireNonNull(matcher.group(4)));
        int hoursRequired = Integer.parseInt(Objects.requireNonNull(matcher.group(5)));
        int priority = Integer.parseInt(Objects.requireNonNull(matcher.group(6)));

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
        Matcher matcher = Pattern.compile("^([a-zA-Z ]*) from (\\d{1,2})(:(\\d{1,2}))? (AM|PM) to (\\d{1,2})(:(\\d{1,2}))? (AM|PM) (.*)$").matcher(result);
        if (!matcher.find()) {
            return null;
        }
        String daysString = matcher.group(1);
        int startHour = Integer.parseInt(Objects.requireNonNull(matcher.group(2)));
        int startMinute = Integer.parseInt(Objects.requireNonNull(matcher.group(4)));
        String startAm = matcher.group(5);
        int endHour = Integer.parseInt(Objects.requireNonNull(matcher.group(6)));
        int endMinute = Integer.parseInt(Objects.requireNonNull(matcher.group(8)));
        String endAm = matcher.group(9);
        String description = matcher.group(10);

        boolean[] days = getDays(Objects.requireNonNull(daysString));
        Calendar startTime = getTime(startHour, startMinute, startAm);
        Calendar endTime = getTime(endHour, endMinute, endAm);
        if (startTime == null || endTime == null || days == null) {
            return null;
        }
        Event event = new Event(startTime, endTime, description);
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

    private static Calendar getTime(int hour, int minute, String am) {
        if (hour <= 0 || minute < 0 || hour > 12 || minute >= 60) {
            return null;
        }
        if (am.equalsIgnoreCase("pm")) {
            hour += 12;
            hour %= 24;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar;
    }
}
