package com.jobtick.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeHelper {

    public static String convertToShowTimeFormat(String time) {

        SimpleDateFormat sdf
                = new SimpleDateFormat("MMM. yyyy hh:mm aaa", Locale.UK);

        Date date = convertToLocalTime(time);
        if(date == null)
            return "-1";

        return getDayWithThFormat(time) + " " + sdf.format(date);
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
                / (1000l * 60 * 60 * 24 * 365));

        long difference_In_Months
                = (difference_In_Time
                / (1000l * 60 * 60 * 24 * 30));

        long difference_In_Weeks
                = (difference_In_Time
                / (1000l * 60 * 60 * 24 * 7));

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

        return date.getDay() + getDayOfMonthSuffix(date.getDay());
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
