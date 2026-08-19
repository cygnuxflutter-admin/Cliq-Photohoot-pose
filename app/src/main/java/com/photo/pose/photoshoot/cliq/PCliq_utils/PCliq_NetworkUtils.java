package com.photo.pose.photoshoot.cliq.PCliq_utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class PCliq_NetworkUtils {
    public static boolean isNetworkAvailable(Context activity) {
        ConnectivityManager connectivity = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            for (NetworkInfo networkInfo : info) {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

}
