package com.obd2.dgt.utils;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.BLUETOOTH_CONNECT;
import static android.Manifest.permission.BLUETOOTH_SCAN;
import static android.Manifest.permission.FOREGROUND_SERVICE;
import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECEIVE_BOOT_COMPLETED;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;



public class PermissionSupport {
    private final Context context;
    private final Activity activity;
    private final String[] permissions = {
            BLUETOOTH,
            BLUETOOTH_ADMIN,
            BLUETOOTH_SCAN,
            BLUETOOTH_CONNECT,
            ACCESS_COARSE_LOCATION,
            ACCESS_FINE_LOCATION,
            POST_NOTIFICATIONS,
            FOREGROUND_SERVICE,
            RECEIVE_BOOT_COMPLETED,
    };
    private final String[] permissions2 = {
            BLUETOOTH,
            BLUETOOTH_ADMIN,
            ACCESS_COARSE_LOCATION,
            ACCESS_FINE_LOCATION,
            POST_NOTIFICATIONS,
            FOREGROUND_SERVICE,
            RECEIVE_BOOT_COMPLETED,
    };
    private final String[] permissions3 = {
            BLUETOOTH,
            BLUETOOTH_ADMIN,
            ACCESS_COARSE_LOCATION,
            ACCESS_FINE_LOCATION,
            POST_NOTIFICATIONS,
            RECEIVE_BOOT_COMPLETED,
    };
    private List permissionList;

    private final int MULTIPLE_PERMISSIONS = 1023;

    public PermissionSupport(Activity _activity, Context _context){
        this.activity = _activity;
        this.context = _context;
    }

    public boolean checkPermission() {
        int result;
        permissionList = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            for (String pm : permissions) {
                result = ContextCompat.checkSelfPermission(context, pm);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(pm);
                }
            }
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            for (String pm : permissions3) {
                result = ContextCompat.checkSelfPermission(context, pm);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(pm);
                }
            }
        }
         else {
            for (String pm : permissions2) {
                result = ContextCompat.checkSelfPermission(context, pm);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(pm);
                }
            }
        }
        if(!permissionList.isEmpty()){
            return false;
        }
        return true;
    }

    public void requestPermission(){
        ActivityCompat.requestPermissions(activity, (String[]) permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
    }

    public boolean permissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == MULTIPLE_PERMISSIONS && (grantResults.length >0)) {
            for(int i=0; i< grantResults.length; i++){
                if(grantResults[i] == -1){
                    return false;
                }
            }
        }
        return true;
    }

}
