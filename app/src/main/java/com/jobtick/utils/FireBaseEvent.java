package com.jobtick.utils;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.jobtick.AppController;

public class FireBaseEvent {

    private FirebaseAnalytics mFirebaseAnalytics;

    public FireBaseEvent(Context context) {

        this.mFirebaseAnalytics = ((AppController)context).mFirebaseAnalytics;
    }

    public void sendEvent(String eventCategory, String eventType, String eventName){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, eventType);
        bundle.putString(FirebaseAnalytics.Param.VALUE, eventName);
        mFirebaseAnalytics.logEvent(eventCategory, bundle);
    }

    public interface Event{
        public static final String SIGN_UP = "sign_up";
        public static final String LOGIN = "login";

        public static final String POST_A_JOB = "post_a_job";
    }


    public interface EventType{
        public static final String BUTTON_CLICK = "button_click";
        public static final String CARD_CLICK = "card_click";
    }

    public interface EventValue{
        public static final String LOGIN_GOOGLE = "login_google";
        public static final String LOGIN_FACEBOOK = "login_facebook";
        public static final String LOGIN_NORMAL = "login_normal";

        public static final String SIGN_UP_GOOGLE = "sign_up_google";
        public static final String SIGN_UP_FACEBOOK = "sign_up_facebook";
        public static final String SIGN_UP_NORMAL = "sign_up_normal";

        public static final String POST_A_JOB_BY_BUTTON = "post_a_job_by_button";
        public static final String POST_A_JOB_BY_CARD = "post_a_job_by_card";

    }
}
