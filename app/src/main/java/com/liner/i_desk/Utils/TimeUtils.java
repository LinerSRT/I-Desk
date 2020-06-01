package com.liner.i_desk.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class TimeUtils {
    public static String SERVER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZZZZ";
    public static String LOCAL_DATE_FORMAT = "dd.MM.YYYY HH:mm";


    public static Calendar getTime(String time) {
        try {
            Calendar calendar = Calendar.getInstance();
            if (time.length() > 25) {
                calendar.setTime(Objects.requireNonNull(new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault()).parse(time)));
            } else {
                calendar.setTime(Objects.requireNonNull(new SimpleDateFormat(LOCAL_DATE_FORMAT, Locale.getDefault()).parse(time)));
            }
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getTime(Calendar calendar, Type type) {
        switch (type) {
            case LOCAL:
                return new SimpleDateFormat(LOCAL_DATE_FORMAT, Locale.getDefault()).format(Objects.requireNonNull(calendar.getTime()));

            case SERVER:
            default:
                return new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault()).format(Objects.requireNonNull(calendar.getTime()));
        }
    }


    public static String getCurrentTime(Type type) {
        switch (type) {
            case SERVER:
                return new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault()).format(new Date());
            case LOCAL:
            default:
                return new SimpleDateFormat(LOCAL_DATE_FORMAT, Locale.getDefault()).format(new Date());
        }
    }

    public static String convertDate(String value) {
        try {
            if (value.length() > 25) {
                return new SimpleDateFormat(LOCAL_DATE_FORMAT, Locale.getDefault()).format(Objects.requireNonNull(new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault()).parse(value)));
            } else {
                return new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault()).format(Objects.requireNonNull(new SimpleDateFormat(LOCAL_DATE_FORMAT, Locale.getDefault()).parse(value)));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static float getDatesPercentage(String startTime, String endTime, Type type) {
        Date start = getTime(startTime).getTime();
        Date end = getTime(endTime).getTime();
        long totalTime = end.getTime() - start.getTime();
        long elapsedTime = System.currentTimeMillis() - start.getTime();
        return TextUtils.getPercent(elapsedTime, totalTime);
    }


    public enum Type {
        SERVER,
        LOCAL
    }
}
