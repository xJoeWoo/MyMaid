package com.joewoo.ontime.support.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.joewoo.ontime.support.util.GlobalContext;

/**
 * Created by JoeWoo on 13-11-21.
 */
public final class NetworkStatus {

    public static final int NETWORK_AVAILABLE = 1;
    public static final int NETWORK_NOT_AVAILABLE = 0;

    public static boolean isNetworkAvailable() {
        NetworkInfo netInfo = ((ConnectivityManager) GlobalContext.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return netInfo != null && netInfo.isAvailable();
    }

    public static int getNetworkStatus() {
        return isNetworkAvailable() ? NETWORK_AVAILABLE : NETWORK_NOT_AVAILABLE;
    }

}
