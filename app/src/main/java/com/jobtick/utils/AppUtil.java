package com.jobtick.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.widget.EditText;
import android.widget.Toast;

import com.jobtick.R;


/**
 * Created by Mounzer on 8/16/2017.
 */

public class AppUtil {

    /**
     * to check if the form is not empty, otherwise
     * return false.
     */
    public static boolean isEmpty(EditText editText) {
        return editText.getText() == null
                || editText.getText().toString().isEmpty();

    }

    public static boolean isValidPassword(String pass) {
        if (pass.length() >= 6) {
            return true;
        }
        return false;
    }

    /**
     * @param context the application context
     * @return true or false
     * @brief methods for identifying the device is supported for calling feature or not
     */
    public static boolean isDeviceCallSupported(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            Toast.makeText(context, context.getResources().getString(R.string.no_call_feature),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    /**
     * @param context the application context
     * @param number  the specified phone number
     * @brief methods for doing a phone call with specified phone number
     */
    public static void phoneCall(Context context, String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
        context.startActivity(intent);
    }
}
