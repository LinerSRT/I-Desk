package com.liner.i_desk.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
    public static String SERVER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZZZZ";
    private static String LOCAL_DATE_FORMAT = "dd.MM.YYYY HH:mm";

    public static String getCurrentTime(Type type) {
        switch (type) {
            case SERVER:
                return new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault()).format(new Date());
            case LOCAL:
            default:
                return new SimpleDateFormat(LOCAL_DATE_FORMAT, Locale.getDefault()).format(new Date());
        }
    }

    public static String convertDate(String value, boolean toLocal) {
        try {
            if (toLocal) {
                return new SimpleDateFormat(LOCAL_DATE_FORMAT, Locale.getDefault()).format(Objects.requireNonNull(new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault()).parse(value)));
            } else {
                return new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault()).format(Objects.requireNonNull(new SimpleDateFormat(LOCAL_DATE_FORMAT, Locale.getDefault()).parse(value)));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String getTime(Type type, int value, TimeUnit timeUnit) {
        Date date;
        if (value < 0) {
            date = new Date(System.currentTimeMillis() - timeUnit.toMillis(Math.abs(value)));
        } else {
            date = new Date(System.currentTimeMillis() + timeUnit.toMillis(Math.abs(value)));
        }
        switch (type) {
            case SERVER:
                return new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault()).format(date);
            case LOCAL:
            default:
                return new SimpleDateFormat(LOCAL_DATE_FORMAT, Locale.getDefault()).format(date);
        }
    }

    public static Date getDate(String time) {
        try {
            return new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault()).parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static float getDeadlinePercent(String startTime, String endTime) {
        Date start = getDate(startTime);
        Date end = getDate(endTime);
        long totalTime = end.getTime() - start.getTime();
        long elapsedTime = System.currentTimeMillis() - start.getTime();
        return  TextUtils.getPercent(elapsedTime, totalTime);
    }

    public enum Type {
        SERVER,
        LOCAL
    }
}
