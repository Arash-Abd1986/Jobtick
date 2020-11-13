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
}