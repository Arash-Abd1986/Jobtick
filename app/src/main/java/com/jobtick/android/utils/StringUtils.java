package com.jobtick.android.utils;

public class StringUtils {

    public static String getPriceTxt(String txt) {
        return "$" + txt.replace("-", "").replaceAll("\\.0*$", "");
    }

    public static boolean checkCreditCardExpiryFormat(String date){
        return date.matches("[0-1][0-9]/20[2-9][0-9]");
    }
    public static boolean checkCreditCardExpiryFormatSimple(String date){
        return date.matches("[0-1][0-9]/[2-9][0-9]");
    }
}
