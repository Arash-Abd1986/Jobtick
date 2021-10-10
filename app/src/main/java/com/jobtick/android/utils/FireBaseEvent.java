package com.jobtick.android.utils;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.jobtick.android.AppController;

import java.lang.ref.WeakReference;

public class FireBaseEvent {

    private final FirebaseAnalytics mFirebaseAnalytics;

    private static FireBaseEvent Instance;
    private final WeakReference<Context> contextWeakReference;

    private FireBaseEvent(Context contextWeakReference) {
        this.contextWeakReference = new WeakReference<>(contextWeakReference);
        this.mFirebaseAnalytics = ((AppController)contextWeakReference).mFirebaseAnalytics;
    }

    public void sendEvent(String eventCategory, String eventType, String eventName){
        Bundle bundle = new Bundle();
//        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, eventType);
//        bundle.putString(FirebaseAnalytics.Param.VALUE, eventName);
        mFirebaseAnalytics.logEvent(eventCategory + eventName, bundle);
    }

    public static FireBaseEvent getInstance(Context context) {
        if (Instance == null) {
            Instance = new FireBaseEvent(context);
        }
        return Instance;
    }

    public interface Event{
        String SIGN_UP = "marketing_sign_up";
        String LOGIN = "marketing_login";
        String POST_A_JOB = "marketing_post_a_job";
        String PAYMENT_OVERVIEW = "marketing_payment_overview";
        String OFFER_SUMMARY = "marketing_offer_summary";
        String CANCELLATION = "marketing_cancellation";
    }


    public interface EventType{
        String API_RESPOND_SUCCESS = "api_respond_success";
        String BUTTON_CLICK = "button_click";
        String CARD_CLICK = "card_click";
    }

    public interface EventValue{
        String LOGIN_GOOGLE = "_google";
        String LOGIN_FACEBOOK = "_facebook";
        String LOGIN_NORMAL = "_email";

        String SIGN_UP_GOOGLE = "_google";
        String SIGN_UP_FACEBOOK = "_facebook";
        String SIGN_UP_NORMAL = "_email";

        String POST_A_JOB_SUBMIT = "_submit";

        String PAYMENT_OVERVIEW_SUBMIT = "_submit";

        String OFFER_SUMMARY_SUBMIT_OFFER = "_submit";

        String CANCELLATION_POSTER_SUBMIT = "_poster_submit";
        String CANCELLATION_WORKER_SUBMIT = "_ticker_submit";
    }
}
