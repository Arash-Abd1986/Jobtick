package com.jobtick;

import android.app.Application;

import com.jobtick.utils.MyNotificationOpenedHandler;
import com.mapbox.mapboxsdk.Mapbox;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.jobtick.utils.Constant;
import com.onesignal.OneSignal;
import com.stripe.android.PaymentConfiguration;


public class AppController extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
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


    }
}
