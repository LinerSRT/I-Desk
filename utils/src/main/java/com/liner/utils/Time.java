package com.liner.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Time {
    public static long getTime(){
        return System.currentTimeMillis();
    }

    public static long getTime(int year, int month, int day, int hour, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month-1, day, hour, minute);
        return calendar.getTimeInMillis();
    }

    public static String getHumanReadableTime(long time, String format){
        return new SimpleDateFormat(format, Locale.getDefault()).format(new Date(time));
    }

    public static float getPercent(long startTime, long endTime){
        return ((Float.parseFloat(String.valueOf((getTime()-startTime)))/Float.parseFloat(String.valueOf(endTime-startTime))) * 100f);
    }
}
