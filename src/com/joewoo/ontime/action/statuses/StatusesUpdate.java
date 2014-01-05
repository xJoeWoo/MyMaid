package com.joewoo.ontime.action.statuses;

import android.os.Handler;
import android.util.Log;

import com.joewoo.ontime.R;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.error.ErrorCheck;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.util.GlobalContext;

import java.util.HashMap;

import static com.joewoo.ontime.support.info.Defines.ACCESS_TOKEN;
import static com.joewoo.ontime.support.info.Defines.GOT_UPDATE_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_UPDATE_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.STATUS;
import static com.joewoo.ontime.support.info.Defines.TAG;

public class StatusesUpdate extends Thread{

    private String status;
    private Handler mHandler;

    public StatusesUpdate(String status, Handler handler) {
        this.status = status;
        this.mHandler = handler;
    }

    public void run() {
        Log.e(TAG, "StatusesUpdate Weibo Thread START");
        String httpResult = null;

        try {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());
            hm.put(STATUS, status);

            httpResult = new HttpUtility().executePostTask(URLHelper.UPDATE, hm);

            hm = null;

        } catch (Exception e) {
            e.printStackTrace();
            mHandler.obtainMessage(GOT_UPDATE_INFO_FAIL, GlobalContext.getResString(R.string.notify_post_fail)).sendToTarget();
        }

//        Log.e(TAG, result);

        if(ErrorCheck.getError(httpResult) == null) {
                mHandler.sendEmptyMessage(GOT_UPDATE_INFO);
        }else {
            mHandler.obtainMessage(GOT_UPDATE_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();
        }

    }





}
