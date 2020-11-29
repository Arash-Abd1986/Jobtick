package com.jobtick.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.NetworkResponse;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jobtick.interfaces.HasEditTextRegular;
import com.jobtick.utils.Helper;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PresenceChannel;

import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;
import com.jobtick.BuildConfig;
import com.jobtick.R;
import com.jobtick.utils.CameraUtils;
import com.jobtick.utils.Constant;
import com.jobtick.utils.CustomToast;
import com.jobtick.utils.SessionManager;
import com.tapadoo.alerter.Alerter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class ActivityBase extends AppCompatActivity implements HasEditTextRegular {

    private static final String TAG = ActivityBase.class.getSimpleName();

    protected ProgressDialog pDialog;
//    GoogleApiClient mGoogleApiClient;
//    Location mLastLocation;

    SessionManager sessionManager;


    // location last updated time
    private String mLastUpdateTime;

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private static final int REQUEST_CHECK_SETTINGS = 100;

    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;

    private PresenceChannel presenceChannel;
    private Pusher pusher;
    private boolean isPusherConnected = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtras();
        initProgressDialog();
        sessionManager = new SessionManager(this);
        //        mGoogleApiClient = new Builder(this)
        //                .addConnectionCallbacks(this)
        //                .addOnConnectionFailedListener(this)
        //                .addApi(LocationServices.API)
        //                .build();
        //
        //        mGoogleApiClient.connect();

        // initialize the necessary libraries
        init();

        // restore the values from saved instance state
        restoreValuesFromBundle(savedInstanceState);
        // permission();

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

        pusher = new Pusher("31c5e7256697a01d331a", options);

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                Log.e("connection", change.getCurrentState() + "");
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


    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }


    /**
     * Restoring values from saved instance state
     */
    private void restoreValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("is_requesting_updates")) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
            }

            if (savedInstanceState.containsKey("last_known_location")) {
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
            }

            if (savedInstanceState.containsKey("last_updated_on")) {
                mLastUpdateTime = savedInstanceState.getString("last_updated_on");
            }
        }

        updateLocationUI();
    }

    /**
     * Update the UI displaying the location data
     * and toggling the buttons
     */
    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            sessionManager.setLatitude(String.valueOf(mCurrentLocation.getLatitude()));
            sessionManager.setLongitude(String.valueOf(mCurrentLocation.getLongitude()));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
        outState.putParcelable("last_known_location", mCurrentLocation);
        outState.putString("last_updated_on", mLastUpdateTime);

    }

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    public void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                        //   Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(ActivityBase.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";

                                Toast.makeText(ActivityBase.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUI();
                    }
                });
    }


    public void permission() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }


    public void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //   Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
                        //  toggleButtons();
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }
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

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }

        return false;
    }

    public void showToast(String content, Context context) {
        Alerter.create(this)
            .setTitle("")
            .setText(content)
            .setBackgroundResource(R.color.colorRedError)
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

    public void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Resuming location updates depending on button state and
        // allowed permissions
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        } else {
            locationRequrestPermission();
        }

        updateLocationUI();
    }

    private void locationRequrestPermission() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }



/*    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRequestingLocationUpdates) {
            // pausing location updates
            stopLocationUpdates();
        }
    }*/

    //    /*Ending the updates for the location service*/
//    @Override
//    public void onStop() {
//        mGoogleApiClient.disconnect();
//        super.onStop();
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        settingRequest();
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        Toast.makeText(this, "Connection Suspended!", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Toast.makeText(this, "Connection Failed!", Toast.LENGTH_SHORT).show();
//        if (connectionResult.hasResolution()) {
//            try {
//                // Start an Activity that tries to resolve the error
//                connectionResult.startResolutionForResult(this, 90000);
//            } catch (IntentSender.SendIntentException e) {
//                e.printStackTrace();
//            }
//        } else {
//            Timber.i("Location services connection failed with code " + connectionResult.getErrorCode());
//        }
//    }
//
//    /*Method to get the enable location settings dialog*/
//    public void settingRequest() {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(10000);    // 10 seconds, in milliseconds
//        mLocationRequest.setFastestInterval(1000);   // 1 second, in milliseconds
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(mLocationRequest);
//
//        PendingResult<LocationSettingsResult> result =
//                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
//                        builder.build());
//
//        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//
//            @Override
//            public void onResult(@NonNull LocationSettingsResult result) {
//                final Status status = result.getStatus();
//                final LocationSettingsStates state = result.getLocationSettingsStates();
//                switch (status.getStatusCode()) {
//                    case LocationSettingsStatusCodes.SUCCESS:
//                        // All location settings are satisfied. The client can
//                        // initialize location requests here.
//                        getLocation();
//                        break;
//                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        // Location settings are not satisfied, but this can be fixed
//                        // by showing the user a dialog.
//                        try {
//                            // Show the dialog by calling startResolutionForResult(),
//                            // and check the result in onActivityResult().
//                            status.startResolutionForResult(ActivityBase.this, 1000);
//                        } catch (IntentSender.SendIntentException e) {
//                            // Ignore the error.
//                        }
//                        break;
//                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                        // Location settings are not satisfied. However, we have no way
//                        // to fix the settings so we won't show the dialog.
//                        break;
//                }
//            }
//
//        });
//    }
//
////    @Override
////    public void onActivityResult(int requestCode, int resultCode, Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
////        // final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
////        if (requestCode == 1000) {
////            switch (resultCode) {
////                case Activity.RESULT_OK:
////                    // All required changes were successfully made
////                    getLocation();
////                    break;
////                case Activity.RESULT_CANCELED:
////                    // The user was asked to change settings, but chose not to
////                    Toast.makeText(this, "Location Service not Enabled", Toast.LENGTH_SHORT).show();
////                    break;
////                default:
////                    break;
////            }
////        }
////    }
//
//    public void getLocation() {
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//        } else {
//            /*Getting the location after aquiring location service*/
//            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                    mGoogleApiClient);
//
//            if (mLastLocation != null) {
//                //     _progressBar.setVisibility(View.INVISIBLE);
//               setBaseLatLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
//
//
//                latitude = String.valueOf(mLastLocation.getLatitude());
//                longitude = String.valueOf(mLastLocation.getLongitude());
//                Log.e("latitude", latitude);
//                Log.e("longitude", longitude);
//            } else {
//                /*if there is no last known location. Which means the device has no data for the loction currently.
//                 * So we will get the current location.
//                 * For this we'll implement Location Listener and override onLocationChanged*/
//                Log.i("Current Location", "No data for location found");
//
//                if (!mGoogleApiClient.isConnected())
//                    mGoogleApiClient.connect();
//
//                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//            }
//        }
//    }
//
//    /*When Location changes, this method get called. */
//    @Override
//    public void onLocationChanged(Location location) {
//        mLastLocation = location;
//        setBaseLatLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
//
//        latitude = String.valueOf(mLastLocation.getLatitude());
//        longitude = String.valueOf(mLastLocation.getLongitude());
//
//        Log.e("latitude", latitude);
//        Log.e("longitude", longitude);
//    }

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
                                    showToast("Mobile Phone not verified", ActivityBase.this);
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
                    showToast("Something Went Wrong", ActivityBase.this);
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
    protected void onRestart() {
        if (isPusherConnected) {
            pusher.connect();
        }
        startLocationUpdates();
        super.onRestart();
    }

    @Override
    protected void onStart() {
        startLocationUpdates();
        super.onStart();
    }

    @Override
    protected void onPause() {
        if (isPusherConnected) {
            pusher.disconnect();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (isPusherConnected) {
            pusher.disconnect();
        }
        stopLocationUpdates();
        super.onDestroy();
    }

    protected void getExtras() {
    }

    @Override
    public void editTextOnClick(@NotNull View view) {
        view.requestFocus();
        Helper.openKeyboard(this);
    }
}
