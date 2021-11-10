package com.jobtick.android.utils

import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.jobtick.android.activities.DashboardActivity
import com.jobtick.android.models.PushNotificationModel
import com.onesignal.OSNotificationAction
import com.onesignal.OSNotificationOpenedResult
import com.onesignal.OneSignal.OSNotificationOpenedHandler
import org.json.JSONException
import timber.log.Timber

class MyNotificationOpenedHandler(private val application: Application) : OSNotificationOpenedHandler {
    private var pushNotificationModel: PushNotificationModel? = null
    private fun startApp() {
        val bundle = Bundle()
        val intent = Intent(application, DashboardActivity::class.java)
                .setFlags( Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        bundle.putParcelable(ConstantKey.PUSH_NOTIFICATION_MODEL, pushNotificationModel)
        intent.putExtras(bundle)
        application.startActivity(intent)
    }

    override fun notificationOpened(result: OSNotificationOpenedResult) {
        var trigger: String
        // Get custom datas from notification
        val data = result.notification.additionalData
        if (data != null) {
            if (data.has(ConstantKey.PUSH_TRIGGER) && !data.isNull(ConstantKey.PUSH_TRIGGER)) {
                pushNotificationModel = PushNotificationModel()
                try {
                    pushNotificationModel!!.setTrigger(data.getString(ConstantKey.PUSH_TRIGGER))
                    if (data.has(ConstantKey.PUSH_TASK_SLUG) && !data.isNull(ConstantKey.PUSH_TASK_SLUG)) {
                        pushNotificationModel!!.setModel_slug(data.getString(ConstantKey.PUSH_TASK_SLUG))
                    }
                    if (data.has(ConstantKey.PUSH_TASK_ID) && !data.isNull(ConstantKey.PUSH_TASK_ID)) {
                        pushNotificationModel!!.setModel_id(data.getInt(ConstantKey.PUSH_TASK_ID))
                    }
                    if (data.has(ConstantKey.PUSH_OFFER) && !data.isNull(ConstantKey.PUSH_OFFER)) {
                        val jsonObjectOffer = data.getJSONObject(ConstantKey.PUSH_OFFER)
                        if (jsonObjectOffer.has(ConstantKey.ID) && !jsonObjectOffer.isNull(ConstantKey.ID)) {
                            pushNotificationModel!!.setOffer_id(jsonObjectOffer.getInt(ConstantKey.ID))
                        }
                    }
                    if (data.has(ConstantKey.PUSH_QUESTION) && !data.isNull(ConstantKey.PUSH_QUESTION)) {
                        val jsonObjectQuestion = data.getJSONObject(ConstantKey.PUSH_QUESTION)
                        if (jsonObjectQuestion.has(ConstantKey.ID) && !jsonObjectQuestion.isNull(ConstantKey.ID)) {
                            pushNotificationModel!!.setQuestion_id(jsonObjectQuestion.getInt(ConstantKey.ID))
                        }
                    }
                    if (data.has(ConstantKey.PUSH_CONVERSATION) && !data.isNull(ConstantKey.PUSH_CONVERSATION)) {
                        val jsonObjectConversation = data.getJSONObject(ConstantKey.PUSH_CONVERSATION)
                        if (jsonObjectConversation.has(ConstantKey.ID) && !jsonObjectConversation.isNull(ConstantKey.ID)) {
                            pushNotificationModel!!.setConversation_id(jsonObjectConversation.getInt(ConstantKey.ID))
                        }
                    }
                    if (data.has(ConstantKey.PUSH_STATUS) && !data.isNull(ConstantKey.PUSH_STATUS)) {
                        pushNotificationModel!!.setStatus(data.getString(ConstantKey.PUSH_STATUS))
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                startApp()
            }
        }
        val actionType = result.action.type
        if (actionType == OSNotificationAction.ActionType.ActionTaken) Timber.tag("OneSignalExample").i("Button pressed with id: " + result.action.actionId)
    }
}