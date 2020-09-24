package com.jobtick.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsDelegate {

    private static final int REQUEST_CODE = 10;
    private final Activity activity;

    public PermissionsDelegate(Activity activity) {
        this.activity = activity;
    }

    public boolean hasCallPermission() {
        int permissionCheckResult = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CALL_PHONE
        );
        return permissionCheckResult == PackageManager.PERMISSION_GRANTED;
    }

    public void requestCallPermission() {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.CALL_PHONE},
                REQUEST_CODE
        );
    }

    public boolean resultGranted(int requestCode,
                          String[] permissions,
                          int[] grantResults) {

        if (requestCode != REQUEST_CODE) {
            return false;
        }

        if (grantResults.length < 1) {
            return false;
        }
        if (!(permissions[0].equals(Manifest.permission.CALL_PHONE))) {
            return false;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        requestCallPermission();
        Toast.makeText(activity, "Please enable Phone Call permission.", Toast.LENGTH_SHORT).show();
        return false;
    }
}
