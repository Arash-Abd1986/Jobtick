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
        public static final String SIGN_UP = "marketing_sign_up";
        public static final String LOGIN = "marketing_login";
        public static final String POST_A_JOB = "marketing_post_a_job";
        public static final String PAYMENT_OVERVIEW = "marketing_payment_overview";
        public static final String OFFER_SUMMARY = "marketing_offer_summary";
        public static final String CANCELLATION = "marketing_cancellation";
    }


    public interface EventType{
        public static final String API_RESPOND_SUCCESS = "api_respond_success";
        public static final String BUTTON_CLICK = "button_click";
        public static final String CARD_CLICK = "card_click";
    }

    public interface EventValue{
        public static final String LOGIN_GOOGLE = "_google";
        public static final String LOGIN_FACEBOOK = "_facebook";
        public static final String LOGIN_NORMAL = "_email";

        public static final String SIGN_UP_GOOGLE = "_google";
        public static final String SIGN_UP_FACEBOOK = "_facebook";
        public static final String SIGN_UP_NORMAL = "_email";

        public static final String POST_A_JOB_SUBMIT = "_submit";

        public static final String PAYMENT_OVERVIEW_SUBMIT = "_submit";

        public static final String OFFER_SUMMARY_SUBMIT_OFFER = "_submit";

        public static final String CANCELLATION_POSTER_SUBMIT = "_poster_submit";
        public static final String CANCELLATION_WORKER_SUBMIT = "_ticker_submit";
    }
}
