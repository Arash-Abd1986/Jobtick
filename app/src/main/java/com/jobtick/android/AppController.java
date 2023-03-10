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

        /*new Instabug.Builder(this, "14cdc056876dd1e8bf9a8579522f9b85")
                .setInvocationEvents(InstabugInvocationEvent.SHAKE, InstabugInvocationEvent.SCREENSHOT)
                .build();*/

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);
        //FLAG_IMMUTABLE
        Mapbox.getInstance(getApplicationContext(), Constant.MAPBOX_API_KEY);

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setNotificationOpenedHandler(new MyNotificationOpenedHandler(this));
        OneSignal.setAppId("dd77e2db-8358-4c20-95b9-e4943e401d4b");
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
                mSocket = IO.socket(URI.create(Constant.BASE_URL_SERVER), opts);
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
