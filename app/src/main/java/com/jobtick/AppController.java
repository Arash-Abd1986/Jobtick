package com.jobtick;

import android.app.Application;

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
    private boolean isDebug;


    @Override
    public void onCreate() {
        super.onCreate();
        isDebug = BuildConfig.DEBUG;

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
        mCrashlytics.setCrashlyticsCollectionEnabled(!isDebug);

    }


    public boolean isDebug() {
        return isDebug;
    }
}
