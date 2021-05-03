package com.jobtick.android;

import android.app.Application;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.jobtick.android.BuildConfig;
import com.jobtick.android.utils.MyNotificationOpenedHandler;
import com.mapbox.mapboxsdk.Mapbox;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.jobtick.android.utils.Constant;
import com.onesignal.OneSignal;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.WebSocket;


public class AppController extends Application {

    public FirebaseAnalytics mFirebaseAnalytics;
    public FirebaseCrashlytics mCrashlytics;
    private boolean isDebug;
    private static final String AF_DEV_KEY = "CQ6XaLaFHQacQ54uQK4G36";
    private Socket mSocket;

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
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)//
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
               // Log.d("debug", "User:" + userId);
                //if (registrationId != null)
                    //Log.d("debug", "registrationId:" + registrationId);

            }
        });

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Operations on FirebaseCrashlytics.
        mCrashlytics = FirebaseCrashlytics.getInstance();
        mCrashlytics.setCrashlyticsCollectionEnabled(!isDebug);
        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {
            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionData) {
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> attributionData) {

            }

            @Override
            public void onAttributionFailure(String errorMessage) {
            }
        };

        AppsFlyerLib.getInstance().init(AF_DEV_KEY, conversionListener, this);
        AppsFlyerLib.getInstance().start(this);

    }

    public Socket getSocket() {
        if (mSocket == null) {
            try {
                IO.Options opts = new IO.Options();
                opts.transports = new String[] { WebSocket.NAME };
                mSocket = IO.socket(URI.create("http://99.79.103.117:3003"), opts);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mSocket;
    }

    public boolean isDebug() {
        return isDebug;
    }
}
