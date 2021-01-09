package com.jobtick.utils;


import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeHelper {

    public static String convertToWeekDateFormat(String time){

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        SimpleDateFormat sdf
                = new SimpleDateFormat("EEEE, MMM dd", Locale.UK);

        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            return "-1";
        } catch (NullPointerException e){
            return "No date set";
        }
        return sdf.format(date);
    }

    //Format: T separator of date and time, output format: 13/03/2020
    public static String convertToJustDateFormat(String dateTime){
        if(!dateTime.contains("T")){
            return "-1";
        }

        return dateTime.substring(0, dateTime.indexOf("T"));
    }

    //Format: T separator of date and time, output format: 13/03/2020
    public static String convertToShowJustDateFormat(String dateTime){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

        Date date = null;
        try {
            date = format.parse(dateTime);
        } catch (ParseException e) {
            return "-1";
        } catch (NullPointerException e){
            return "No date set";
        }
        return sdf.format(date);
    }

    //Format: T separator of date and time, output format: 13/03/2020
    public static String convertToShowJustTimeFormat(String dateTime){
        if(!dateTime.contains("T")){
            return "-1";
        }

        return dateTime.substring(dateTime.indexOf("T") + 1, dateTime.indexOf("T") + 9);
    }


    //format 2021-01-07
    public static Long convertDateToLong(String date){

        SimpleDateFormat sdf
                = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

        try {
            Date d = sdf.parse(date);
            assert d != null;
            return d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    //format 2021-01-07
    public static Long convertDateTimeToLong(String date){

        SimpleDateFormat sdf
                = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.UK);

        try {
            Date d = sdf.parse(date);
            assert d != null;
            return d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    public static String convertSecondsToMinAndSeconds(int seconds){
        int min = seconds / 60;
        int sec = seconds % 60;
        if(sec < 10)
            return min + ":0" + sec;

        return min + ":" + sec;
    }

    public static String convertToShowTimeFormat(String time) {

        SimpleDateFormat sdf
                = new SimpleDateFormat("MMM. yyyy hh:mm aaa", Locale.UK);

        Date date = convertToLocalTime(time);
        if(date == null)
            return "-1";

        return getDayWithThFormat(time) + " " + sdf.format(date);
    }

    public static String getCurrentDateTimeInShowTimeFormat() {

        SimpleDateFormat sdf
                = new SimpleDateFormat("MMM. yyyy hh:mm aaa", Locale.UK);

        Date date = new Date();
        return getCurrentDayWithThFormat() + " " + sdf.format(date);
    }

    public static String findDifferenceWithNow(String time) {

        Date d1 = convertToLocalTime(time);
        if (d1 == null)
            return "-1";

        Date d2 = new Date();

        long difference_In_Time
                = d2.getTime() - d1.getTime();

        long difference_In_Seconds
                = (difference_In_Time
                / 1000)
                % 60;

        long difference_In_Minutes
                = (difference_In_Time
                / (1000 * 60))
                % 60;

        long difference_In_Hours
                = (difference_In_Time
                / (1000 * 60 * 60))
                % 24;

        long difference_In_Years
                = (difference_In_Time
                / (1000L * 60 * 60 * 24 * 365));

        long difference_In_Months
                = (difference_In_Time
                / (1000L * 60 * 60 * 24 * 30));

        long difference_In_Weeks
                = (difference_In_Time
                / (1000L * 60 * 60 * 24 * 7));

        long difference_In_Days
                = (difference_In_Time
                / (1000 * 60 * 60 * 24))
                % 365;

        System.out.println(
                difference_In_Years
                        + " years, " +
                        difference_In_Months
                        + " months, " +
                        difference_In_Weeks
                        + " weeks, "
                        + difference_In_Days
                        + " days, "
                        + difference_In_Hours
                        + " hours, "
                        + difference_In_Minutes
                        + " minutes, "
                        + difference_In_Seconds
                        + " seconds");

        if (difference_In_Years > 0)
            return difference_In_Years + "y";

        if (difference_In_Months > 0)
            return difference_In_Months + "m";

        if (difference_In_Weeks > 0)
            return difference_In_Weeks + "w";

        if (difference_In_Days > 0)
            return difference_In_Days + "d";

        if (difference_In_Hours > 0)
            return difference_In_Hours + "h";

        if (difference_In_Minutes > 0)
            return difference_In_Minutes + "min";

        else return "now";
    }

    private static String getDayWithThFormat(String time) {

        Date date = convertToLocalTime(time);

        if(date == null)
            return "-1";

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return day + getDayOfMonthSuffix(day);
    }

    private static String getCurrentDayWithThFormat() {

        Date date = new Date();

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return day + getDayOfMonthSuffix(day);
    }

    private static String getDayOfMonthSuffix(final int n) {
        if (n >= 11 && n <= 31) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    private static Date convertToLocalTime(String time) {
        //api pattern 2020-11-09T14:44:12+00:00
        SimpleDateFormat sdf
                = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.UK);

        Date date = getDate(time, sdf);
        if(date == null)
            return null;

        Date now = new Date();
        //convert server time to local time
        return new Date(date.getTime() + TimeZone.getDefault().getOffset(now.getTime()));
    }

    private static Date getDate(String time, SimpleDateFormat format) {

        try {
            return format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
