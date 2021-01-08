package com.jobtick.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TimeHelperTest {



//    @Test
//    public void findDifferenceWithNow() {
//
//        //please before testing this, put your current time
//        String currentTime = "2020-11-13T15:58:15+00:00";
//
//        String difference = TimeHelper.findDifferenceWithNow(currentTime);
//
//        Assert.assertEquals("now", difference);
//    }
//
//    @Test
//    public void convertToShowTimeFormat() {
//
//        String currentTime = "2020-11-09T14:44:11+00:00";
//
//        String showFormat = TimeHelper.convertToShowTimeFormat(currentTime);
//
//        Assert.assertEquals("9th Nov. 2020 06:14 PM", showFormat);
//    }
//
//    @Test
//    public void testConvertToShowTimeFormat() {
//        String time = "2020-11-24T16:05:13+00:00";
//
//        String showFormat =TimeHelper.convertToShowTimeFormat(time);
//
//        Assert.assertEquals("24th Nov. 2020 07:35 PM", showFormat);
//    }
//
//    @Test
//    public void getCurrentTimeInShowTimeFormat() {
//        String date = TimeHelper.getCurrentDateTimeInShowTimeFormat();
//
//        //put current date
//        Assert.assertEquals("30th Nov. 2020 12:30 AM", date);
//    }
//
    @Test
    public void convertDateToLong() {
        String today = "2021-01-07";
        String tomorrow = "2021-01-08";
        long longTimeToday = TimeHelper.convertDateToLong(today);
        long longTimeTomorrow = TimeHelper.convertDateToLong(tomorrow);

        long delta = longTimeTomorrow - longTimeToday;

        Assert.assertEquals(86400000L, delta);
    }

    @Test
    public void convertToWeekDateFormat_usual() {
        String time = "2020-01-01";
        String formetedTime = TimeHelper.convertToWeekDateFormat(time);

        Assert.assertEquals("Wednesday, Jan 01", formetedTime);
    }

    @Test
    public void convertToWeekDateFormat_when_null() {
        String time = null;
        String formetedTime = TimeHelper.convertToWeekDateFormat(time);

        Assert.assertEquals("No date set", formetedTime);
    }

    @Test
    public void convertToWeekDateFormat_when_empty() {
        String time = "";
        String formetedTime = TimeHelper.convertToWeekDateFormat(time);

        Assert.assertEquals("-1", formetedTime);
    }
}