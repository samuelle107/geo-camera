package com.samuelle.geocamera.presenter;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivityPresenter {
    Activity context;
    private final String[] REQUIRED_PERMISSIONS = new String[] {
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.INTERNET",
            "android.permission.CAMERA"
    };
    private final int REQUEST_CODE_PERMISSIONS = 101;

    public MainActivityPresenter(Activity context) {
        this.context = context;
    }

    // Check if all permissions have been allowed
    public boolean hasAllowedAllPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    // Request the un-allowed permissions
    public void requestPermissions() {
        ActivityCompat.requestPermissions(context, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
    }
}
