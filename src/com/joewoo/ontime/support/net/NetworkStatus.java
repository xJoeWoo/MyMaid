package com.joewoo.ontime.support.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.util.GlobalContext;

/**
 * Created by JoeWoo on 13-11-21.
 */
public final class NetworkStatus {

    public static boolean check(boolean showToast) {
        NetworkInfo netInfo = ((ConnectivityManager) GlobalContext.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if(netInfo != null && netInfo.isAvailable())
            return true;
        else {
            if(showToast)
                Toast.makeText(GlobalContext.getAppContext(), R.string.toast_no_network, Toast.LENGTH_SHORT).show();
            return false;
        }

    }

}
