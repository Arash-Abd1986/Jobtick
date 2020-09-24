package com.jobtick.utils;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.jobtick.activities.DashboardActivity;
import com.jobtick.models.PushNotificationModel;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import static com.jobtick.utils.ConstantKey.ID;
import static com.jobtick.utils.ConstantKey.PUSH_CONVERSATION;
import static com.jobtick.utils.ConstantKey.PUSH_CONVERSATION_ID;
import static com.jobtick.utils.ConstantKey.PUSH_OFFER;
import static com.jobtick.utils.ConstantKey.PUSH_OFFER_ID;
import static com.jobtick.utils.ConstantKey.PUSH_QUESTION;
import static com.jobtick.utils.ConstantKey.PUSH_QUESTION_ID;
import static com.jobtick.utils.ConstantKey.PUSH_STATUS;
import static com.jobtick.utils.ConstantKey.PUSH_TASK;
import static com.jobtick.utils.ConstantKey.PUSH_TASK_ID;
import static com.jobtick.utils.ConstantKey.PUSH_TASK_SLUG;
import static com.jobtick.utils.ConstantKey.PUSH_TRIGGER;

public class MyNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {

    private Application application;
    private PushNotificationModel pushNotificationModel;

    public MyNotificationOpenedHandler(Application application) {
        this.application = application;
    }

    @Override
    public void notificationOpened(OSNotificationOpenResult result) {

        String trigger;
        // Get custom datas from notification
        JSONObject data = result.notification.payload.additionalData;
        if (data != null) {
            if (data.has(PUSH_TRIGGER) && !data.isNull(PUSH_TRIGGER)) {
                pushNotificationModel = new PushNotificationModel();
                try {
                    pushNotificationModel.setTrigger(data.getString(PUSH_TRIGGER));

                    if (data.has(PUSH_TASK_SLUG) && !data.isNull(PUSH_TASK_SLUG)) {
                        pushNotificationModel.setModel_slug(data.getString(PUSH_TASK_SLUG));
                    }

                    if (data.has(PUSH_TASK_ID) && !data.isNull(PUSH_TASK_ID)) {
                        pushNotificationModel.setModel_id(data.getInt(PUSH_TASK_ID));
                    }

                    if (data.has(PUSH_OFFER) && !data.isNull(PUSH_OFFER)) {
                        JSONObject jsonObjectOffer = data.getJSONObject(PUSH_OFFER);
                        if (jsonObjectOffer.has(ID) && !jsonObjectOffer.isNull(ID)) {
                            pushNotificationModel.setOffer_id(jsonObjectOffer.getInt(ID));
                        }
                    }
                    if (data.has(PUSH_QUESTION) && !data.isNull(PUSH_QUESTION)) {
                        JSONObject jsonObjectQuestion = data.getJSONObject(PUSH_QUESTION);
                        if (jsonObjectQuestion.has(ID) && !jsonObjectQuestion.isNull(ID)) {

                            pushNotificationModel.setQuestion_id(jsonObjectQuestion.getInt(ID));
                        }
                    }
                    if (data.has(PUSH_CONVERSATION) && !data.isNull(PUSH_CONVERSATION)) {
                        JSONObject jsonObjectConversation = data.getJSONObject(PUSH_CONVERSATION);
                        if (jsonObjectConversation.has(ID) && !jsonObjectConversation.isNull(ID)) {

                            pushNotificationModel.setConversation_id(jsonObjectConversation.getInt(ID));
                        }
                    }
                    if (data.has(PUSH_STATUS) && !data.isNull(PUSH_STATUS)) {
                        pushNotificationModel.setStatus(data.getString(PUSH_STATUS));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startApp();
            }

//            String myCustomData = data.optString("key", null);
        }

        // React to button pressed
        OSNotificationAction.ActionType actionType = result.action.type;
        if (actionType == OSNotificationAction.ActionType.ActionTaken)
            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

        // Launch new activity using Application object
    }

    private void startApp() {

        Bundle bundle = new Bundle();
        Intent intent = new Intent(application, DashboardActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        bundle.putParcelable(ConstantKey.PUSH_NOTIFICATION_MODEL, pushNotificationModel);
        intent.putExtras(bundle);
        application.startActivity(intent);
    }
}
