package com.jobtick.android.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;

import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;
import com.jobtick.android.R;

import com.jobtick.android.utils.CameraUtils;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.SessionManager;
import com.segment.analytics.android.integrations.appsflyer.AppsflyerIntegration;
import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import timber.log.Timber;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;

public class ActivityBase extends AppCompatActivity {
    static final String SEGMENT_WRITE_KEY = "CQ6XaLaFHQacQ54uQK4G36";
    protected ProgressDialog pDialog;
    SessionManager sessionManager;

    private Pusher pusher;
    private boolean isPusherConnected = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtras();
        initProgressDialog();

        Analytics.Builder builder = new Analytics.Builder(this , SEGMENT_WRITE_KEY)
                .use(AppsflyerIntegration.FACTORY);
        sessionManager = new SessionManager(this);

        if (sessionManager.getLogin()) {
            callPusherPresence();
            isPusherConnected = true;
        } else {
            isPusherConnected = false;
        }

    }

    private void callPusherPresence() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("X-REQUESTED-WITH", "xmlhttprequest");
        headers.put("Accept", "application/json");
        HttpAuthorizer authorizer = new HttpAuthorizer(Constant.BASE_URL + "broadcasting/auth");

        authorizer.setHeaders(headers);
        PusherOptions options = new PusherOptions()
                .setEncrypted(true)
                .setCluster("us2")
                .setAuthorizer(authorizer);

        pusher = new Pusher(getString(R.string.pusher_api_key), options);

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                Timber.tag("connection").e(change.getCurrentState() + "");
                System.out.println("State changed to " + change.getCurrentState() +
                        " from " + change.getPreviousState());
                if (change.getCurrentState() == ConnectionState.CONNECTED) {
                    // subscribeToPresenceChannel();
                }
            }

            @Override
            public void onError(String message, String code, Exception e) {
                System.out.println("There was a problem connecting!");
            }
        }, ConnectionState.ALL);
    }

    /**
     * Alert dialog to navigate to app settings
     * to enable necessary permissions
     */
    public void showPermissionsAlert() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Permissions required!")
                .setMessage("Camera needs few permissions to work properly. Grant them in settings.")
                .setPositiveButton("GOTO SETTINGS", (dialog, which) -> CameraUtils.openSettings(ActivityBase.this))
                .setNegativeButton("CANCEL", null)
                .show();
    }


    public boolean isConnected() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void showToast(String content, Context context) {
        Alerter.create(this)
            .setTitle("")
            .setText(content)
            .setBackgroundResource(R.color.colorRedError)
                .show();
    }

    public void showSuccessToast(String content, Context context) {
        Alerter.create(this)
            .setTitle("")
            .setText(content)
            .setBackgroundResource(R.color.colorOk)
                .show();
    }

    public void initProgressDialog() {
            pDialog = new ProgressDialog(this);
            pDialog.setTitle(getString(R.string.processing));
            pDialog.setMessage(getString(R.string.please_wait));
            pDialog.setCancelable(false);
    }


    public void showProgressDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    public void hideProgressDialog() {

        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void unauthorizedUser() {
        sessionManager.setUserAccount(null);
        sessionManager.setLogin(false);
        sessionManager.setTokenType(null);
        sessionManager.setAccessToken(null);
        Intent main = new Intent(ActivityBase.this, AuthActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(main);
    }


    public void errorHandle1(NetworkResponse networkResponse) {
        if (networkResponse != null) {
            switch (networkResponse.statusCode) {
                case 400:
                case 401:
                    unauthorizedUser();
                    hideProgressDialog();
                    break;
                case 402:
                case 403:
                    if (networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        try {
                            JSONObject jsonObject = new JSONObject(jsonError);
                            JSONObject jsonObject_error = jsonObject.getJSONObject("error");
                            switch (jsonObject_error.getInt("error_code")) {
                                case 401:
                                    unauthorizedUser();
                                    break;
                                case 1002:
                                    showToast("Email not verified", ActivityBase.this);
                                    break;
                                case 1003:
                                    showToast("Mobile number not verified", ActivityBase.this);
                                    break;
                                case 1004:
                                    showToast("You are suspended,Please contact to admin", ActivityBase.this);
                                    unauthorizedUser();
                                    break;
                                case 1005:
                                    showToast("You are temp suspended,Please contact to admin", ActivityBase.this);
                                    unauthorizedUser();
                                    break;
                                case 1030:
                                    showToast("Profile incomplete", ActivityBase.this);
                                    break;
                                case 1031:
                                    showToast("Payment info incomplete", ActivityBase.this);
                                    break;
                                case 1040:
                                    showToast("Insufficient Balance", ActivityBase.this);
                                    break;
                                case 1050:
                                    showToast("No payment method...", ActivityBase.this);
                                    break;
                                case 1051:
                                    showToast("Invalid card...", ActivityBase.this);
                                    break;
                                case 1052:
                                    showToast("Two many payment request...", ActivityBase.this);
                                    break;
                                case 400:
                                    showToast(jsonObject_error.getString("message"), ActivityBase.this);
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 500:
                case 501:
                case 502:
                case 503:
                case 504:
                case 505:
                    Intent intent = new Intent(this, ServerUnderMaintenance.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    break;
                default:
                    showToast("Something Went Wrong", ActivityBase.this);
                    break;
            }
        } else {
            showToast("Network Issue", ActivityBase.this);
        }
    }


    @Override
    protected void onPause() {
        if (isPusherConnected) {
            pusher.disconnect();
        }
        super.onPause();
    }


    protected void getExtras() {
    }
}
