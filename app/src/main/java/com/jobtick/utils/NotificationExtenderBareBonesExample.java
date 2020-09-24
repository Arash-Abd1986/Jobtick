package com.jobtick.utils;

import android.util.Log;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;

import org.json.JSONException;

import static com.jobtick.utils.ConstantKey.IS_CHAT_SCREEN;
import static com.jobtick.utils.ConstantKey.PUSH_CONVERSATION;
import static com.jobtick.utils.ConstantKey.PUSH_TRIGGER;

public class NotificationExtenderBareBonesExample extends NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
        // Read properties from result.


        if (receivedResult.payload != null && receivedResult.payload.additionalData != null) {
            try {
                if (receivedResult.payload.additionalData.has(PUSH_TRIGGER) && receivedResult.payload.additionalData.getString(PUSH_TRIGGER).equals(PUSH_CONVERSATION)) {
                    if (IS_CHAT_SCREEN) {
                        return true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // receivedResult.payload.additionalData
        // Return true to stop the notification from displaying.
        return false;
    }
}