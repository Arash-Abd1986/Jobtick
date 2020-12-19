package com.jobtick;

import android.app.Application;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.jobtick.utils.MyNotificationOpenedHandler;
import com.mapbox.mapboxsdk.Mapbox;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.jobtick.utils.Constant;
import com.onesignal.OneSignal;
import com.stripe.android.PaymentConfiguration;

import timber.log.Timber;


public class AppController extends Application {

    public FirebaseAnalytics mFirebaseAnalytics;
    public FirebaseCrashlytics mCrashlytics;


    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_TYooMQauvdEDq54NiTphI7jx"
        );

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);
        Mapbox.getInstance(getApplicationContext(), Constant.MAPBOX_API_KEY);
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new MyNotificationOpenedHandler(this))
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Operations on FirebaseCrashlytics.
        mCrashlytics = FirebaseCrashlytics.getInstance();
        mCrashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG);

    }
}
