package com.example.cs16jkd.myapplication.Operations;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by cs16jkd on 27/03/2019.
 */

public class PermissionsHandler {
    public static final int PERMISSIONS_REQUEST = 001;
    public static final int CAMERA_REQUEST = 002;


    /**
     *Check for the permissions
     * If the permission is not granted then request it
     * @param baseActivity
     */
    public void GalleryPermissionsHandling(Context ctx,Activity baseActivity) {
        if(ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            RequestGalleryPermission(baseActivity);
        }else if(ActivityCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            RequestCameraPermission(baseActivity);
        }

    }

    /**
     * Requests the permission
     * @param baseActivity
     */
    private void RequestGalleryPermission(Activity baseActivity) {
        ActivityCompat.requestPermissions(baseActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST);

    }
    /**
     * Requests the permission
     * @param baseActivity
     */
    private void RequestCameraPermission(Activity baseActivity) {
        ActivityCompat.requestPermissions(baseActivity, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);

    }
}
