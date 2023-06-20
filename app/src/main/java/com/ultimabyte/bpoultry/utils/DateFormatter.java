package com.ultimabyte.bpoultry.utils;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@SuppressWarnings({"UnusedDeclaration, WeakerAccess", "SpellCheckingInspection"})
public class DateFormatter {

    private static SimpleDateFormat sDateFormat;

    public static Date format(String date) {
        try {
            return getSimpleDate(date);
        } catch (ParseException e) {
            e.printStackTrace();
            try {
                return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault()).parse(date.replace(",", ""));
            } catch (ParseException e1) {
                //ignore
                //e1.printStackTrace();
                try {
                    return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault()).parse(date);
                } catch (Exception e2) {
                    //ignore
                    //e2.printStackTrace();
                    try {
                        return new SimpleDateFormat("E dd MMM yyyy", Locale.getDefault()).parse(date.replace(",", ""));
                    } catch (ParseException e3) {
                        e3.printStackTrace();
                    }
                }
            }
        }


        return null;
    }

    public static Date getSimpleDate(String date) throws ParseException {
        if (sDateFormat == null) {
            sDateFormat = new SimpleDateFormat("E dd MMM yyyy HH:mm:ss", Locale.getDefault());
        }
        return sDateFormat.parse(date.replace(",", ""));
    }


    private static final SimpleDateFormat dayFormatter = new SimpleDateFormat("D, y", Locale.ENGLISH);
    private static final SimpleDateFormat weekFormatter = new SimpleDateFormat("w, y", Locale.ENGLISH);
    private static final SimpleDateFormat yearFormatter = new SimpleDateFormat("y", Locale.ENGLISH);
    private static final SimpleDateFormat yesterdayFormatter = new SimpleDateFormat("yD", Locale.ENGLISH);
    private static final SimpleDateFormat hourMinuteFormatter = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
    private static final SimpleDateFormat simpleFormatter = new SimpleDateFormat("M/d/yyyy", Locale.ENGLISH);


    public static boolean isSameDay(long date) {
        return dayFormatter.format(date).equals(dayFormatter.format(System.currentTimeMillis()));
    }

    private static boolean isSameWeek(long date) {
        return weekFormatter.format(date).equals(weekFormatter.format(System.currentTimeMillis()));
    }

    private static boolean isSameYear(long date) {
        return yearFormatter.format(date).equals(yearFormatter.format(System.currentTimeMillis()));
    }

    private static boolean isYesterday(long date) {
        return Integer.parseInt(yesterdayFormatter.format(date)) + 1 == Integer.parseInt(yesterdayFormatter.format(System.currentTimeMillis()));
    }

    public static SimpleDateFormat accountFor24HourTime(Context context, SimpleDateFormat input) { //pass in 12 hour time. If needed, change to 24 hr.
        boolean isUsing24HourTime = DateFormat.is24HourFormat(context);
        if (isUsing24HourTime) {
            return new SimpleDateFormat(input.toPattern().replace('h', 'H').replaceAll(" a", ""), Locale.getDefault());
        } else return input;
    }


    public static String getTimeOfDay(Context context, long date) {
        return accountFor24HourTime(context, hourMinuteFormatter).format(date);
    }


    public static String getDateStamp(Context context, long date) {
        if (isSameDay(date)) {
            hourMinuteFormatter.setTimeZone(TimeZone.getDefault());
            return hourMinuteFormatter.format(date);
            //return accountFor24HourTime(context, hourMinuteFormatter).format(date);
        } else if (isYesterday(date)) {
            return "Yesterday";
        } else if (isSameWeek(date)) {
            return DateUtils.formatDateTime(context, date, DateUtils.FORMAT_SHOW_WEEKDAY) + ",";
        } else if (isSameYear(date)) {
            return DateUtils.formatDateTime(context, date, DateUtils.FORMAT_SHOW_WEEKDAY) + ", " + DateUtils.formatDateTime(context, date, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR | DateUtils.FORMAT_ABBREV_MONTH);
        }

        return DateUtils.formatDateTime(context, date, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH);
    }

    @SuppressWarnings("UnusedDeclaration")
    public static String getTimestamp(Context context, long date) {
        String time = " " + accountFor24HourTime(context, hourMinuteFormatter).format(date);
        if (isSameDay(date)) {
            return accountFor24HourTime(context, hourMinuteFormatter).format(date);
        } else if (isYesterday(date)) {
            return "Yesterday" + time;
        } else if (isSameWeek(date)) {
            return DateUtils.formatDateTime(context, date, DateUtils.FORMAT_SHOW_WEEKDAY) + ", " + DateUtils.formatDateTime(context, date, DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_WEEKDAY) + time;
        } else if (isSameYear(date)) {
            return DateUtils.formatDateTime(context, date, DateUtils.FORMAT_SHOW_WEEKDAY) + ", " + DateUtils.formatDateTime(context, date, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR | DateUtils.FORMAT_ABBREV_MONTH) + time;
        }

        return DateUtils.formatDateTime(context, date, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH) + time;
    }


    public static String getTimestampShort(Context context, long date) {
        if (isSameDay(date)) {
            return accountFor24HourTime(context, hourMinuteFormatter).format(date);
        } else if (isSameWeek(date)) {
            return DateUtils.formatDateTime(context, date, DateUtils.FORMAT_SHOW_WEEKDAY);
        } else if (isSameYear(date)) {
            return DateUtils.formatDateTime(context, date, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR | DateUtils.FORMAT_ABBREV_MONTH);
        }

        //return DateUtils.formatDateTime(context, date, 0) + time;
        return simpleFormatter.format(new Date(date));
    }
}
