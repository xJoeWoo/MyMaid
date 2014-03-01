package com.joewoo.ontime.action.update;

import android.os.Handler;

import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.info.Defines;
import com.joewoo.ontime.support.net.HttpUtility;

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

        try {

            String newVer = new HttpUtility().executeGetHTMLTask(URLHelper.MYMAID_VERSION);

            handler.obtainMessage(Defines.GOT_APP_VERSION_INFO, newVer).sendToTarget();

        } catch (Exception ignore) {

        }
    }
}
