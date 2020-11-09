package com.jobtick.utils;

public class StringUtils {

    public static String getPriceTxt(String txt) {
        return "$" + txt.replace("-", "").replaceAll("\\.0*$", "");
    }
}
