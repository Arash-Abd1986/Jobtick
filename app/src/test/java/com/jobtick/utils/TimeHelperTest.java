package com.jobtick.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TimeHelperTest {

    @Test
    public void findDifferenceWithNow() {

        //please before testing this, put your current time
        String currentTime = "2020-11-13T15:58:15+00:00";

        String difference = TimeHelper.findDifferenceWithNow(currentTime);

        Assert.assertEquals("now", difference);
    }

    @Test
    public void convertToShowTimeFormat() {

        String currentTime = "2020-11-09T14:44:11+00:00";

        String showFormat = TimeHelper.convertToShowTimeFormat(currentTime);

        Assert.assertEquals("9th Nov. 2020 06:14 PM", showFormat);
    }

    @Test
    public void testConvertToShowTimeFormat() {
        String time = "2020-11-24T16:05:13+00:00";

        String showFormat =TimeHelper.convertToShowTimeFormat(time);

        Assert.assertEquals("24th Nov. 2020 07:35 PM", showFormat);
    }

    @Test
    public void getCurrentTimeInShowTimeFormat() {
        String date = TimeHelper.getCurrentDateTimeInShowTimeFormat();

        //put current date
        Assert.assertEquals("30th Nov. 2020 12:30 AM", date);
    }
}