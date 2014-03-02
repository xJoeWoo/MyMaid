package com.joewoo.ontime.action.update;

import android.os.Handler;
import android.util.Log;

import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.info.Defines;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.setting.MyMaidSettingsHelper;

/**
 * Created by Joe on 14-3-1.
 */
public class CheckUpdate extends Thread {

    private Handler handler;

    public CheckUpdate(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        Log.e(Defines.TAG, "Check Update Thread START!");

        try {

            String newVer = new HttpUtility().executeGetHTMLTask(URLHelper.MYMAID_VERSION);

            MyMaidSettingsHelper.save(MyMaidSettingsHelper.CHECK_UPDATE_TIME, System.currentTimeMillis());

            MyMaidSettingsHelper.save(MyMaidSettingsHelper.NEW_VERSION, newVer);

            handler.obtainMessage(Defines.GOT_APP_VERSION_INFO, newVer).sendToTarget();

        } catch (Exception ignore) {

        }
    }
}
