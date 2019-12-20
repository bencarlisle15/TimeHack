package com.bencarlisle.timehack.main;

import android.util.Log;

import com.bencarlisle.timehack.tasks.Task;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    public static Event parseEventResult(String str) {
        Event event = parseEventWithoutFrom(str);
        if (event != null) {
            return event;
        }
        return parseEventWithFrom(str);
    }

    private static Event parseEventWithoutFrom(String str) {
        String[] words = str.split(" ");
        if (words.length < 6) {
            return null;
        }
        Calendar startTime = getTime(words[0], words[1]);
        Calendar endTime = getTime(words[3], words[4]);
        if (startTime != null && words[2].equals("to") && endTime != null) {
            String description = String.join(" ", Arrays.copyOfRange(words, 5, words.length));
            return new Event(startTime, endTime, description);
        } else {
            Log.e("PARSER", "no match");
            return null;
        }
    }

    private static Event parseEventWithFrom(String str) {
        String[] words = str.split(" ");
        if (words.length < 6) {
            return null;
        }
        Calendar startTime = getTime(words[1], words[2]);
        Calendar endTime = getTime(words[4], words[5]);
        if (words[0].equals("from") && startTime != null && words[3].equals("to") && endTime != null) {
            String description = String.join(" ", Arrays.copyOfRange(words, 6, words.length));
            if (description.length() == 0) {
                Log.e("PARSER", "No event listed");
                return null;
            }
            return new Event(startTime, endTime, description);
        } else {
            Log.e("PARSER", "no match");
            return null;
        }
    }

    private static Calendar getTime(String word1, String word2) {
        Pattern patternMinute = Pattern.compile("^(\\d{1,2}):(\\d{2})$");
        Pattern patternHour = Pattern.compile("^(\\d{1,2})$");
        Matcher matcherMinute = patternMinute.matcher(word1);
        Matcher matcherHour = patternHour.matcher(word1);
        Matcher matcher = matcherHour;
        int minute = 0;
        if (matcherMinute.find()) {
            matcher = matcherMinute;
            minute = Integer.parseInt(Objects.requireNonNull(matcher.group(2)));
        } else if (!matcherHour.find()) {
            return null;
        }
        int hour = Integer.parseInt(Objects.requireNonNull(matcher.group(1)));
        if (hour <= 0 || hour > 12 || minute < 0 || minute > 60) {
            Log.e("EVENT", "ERR");
            return null;
        }
        if (hour == 12) {
            hour -= 12;
        }
        if (word2.equalsIgnoreCase("p.m.")) {
            hour += 12;
        } else if (!word2.equalsIgnoreCase("a.m.")) {
            return null;
        }
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute);
        return time;
    }

    public static Task parseTaskResult(String str) {
        Matcher matcher = Pattern.compile("add (.+) (due|to|do) (.*) (\\d+).. with (\\d+) hours and priority (\\d)").matcher(str);
        if (!matcher.find()) {
            return null;
        }
        
        String description = matcher.group(1);
        int month = getMonth(matcher.group(3));
        int day = Integer.parseInt(matcher.group(4));
        int hoursRequired = Integer.parseInt(matcher.group(5));
        int priority = Integer.parseInt(matcher.group(6));

        Calendar dueDate = getDueDate(month, day);

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

    private static Calendar getDueDate(int month, int day) {
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
        Log.e("PARSER",month + " " + day);
        return calendar;
    }
}
