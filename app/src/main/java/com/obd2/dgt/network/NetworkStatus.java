package com.obd2.dgt.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import com.obd2.dgt.utils.MyUtils;

public class NetworkStatus {
    public static Boolean getNetworkConnect() {
        boolean returnData = true;
        String networkName = "";
        int downSpeed = 0;
        int upSpeed = 0;
        try {
            ConnectivityManager cm = (ConnectivityManager) MyUtils.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                //downSpeed = networkCapabilities.getLinkDownstreamBandwidthKbps();
                //upSpeed = networkCapabilities.getLinkUpstreamBandwidthKbps();
                if (networkCapabilities != null){
                    if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                        networkName = "WIFI";
                    }
                    else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                        networkName = "MOBILE";
                    }
                    else {
                        returnData = false;
                        networkName = "null";
                    }
                }
                else {
                    returnData = false;
                    networkName = "null";
                }
            }
            else {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        networkName = "WIFI";
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        networkName = "MOBILE";
                    } else {
                        returnData = false;
                        networkName = "null";
                    }
                } else {
                    returnData = false;
                    networkName = "null";
                }
            }

        }
        catch (Exception e) {
            returnData = false;
            e.printStackTrace();
        }

        return returnData;
    }
}
