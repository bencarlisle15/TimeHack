package com.bencarlisle.timehack.main;

import android.util.Log;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    public static Event parseResult(String str) {
        Event event = parseWithoutFrom(str);
        if (event != null) {
            return event;
        }
        return parseWithFrom(str);
    }

    private static Event parseWithoutFrom(String str) {
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

    private static Event parseWithFrom(String str) {
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
}
