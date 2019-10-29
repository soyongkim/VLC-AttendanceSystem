package com.example.soyongkim.vlc_receiver.controller.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissonAllow {

    public static void setupPermissions(Activity activity) {
        int permission = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest(activity);
        }
    }


    public static void makeRequest(Activity activity) {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(
                activity,
                permissions,
                0
        );
    }

}
