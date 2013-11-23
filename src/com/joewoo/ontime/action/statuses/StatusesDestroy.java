package com.joewoo.ontime.action.statuses;

import android.os.Handler;
import android.util.Log;

import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.error.ErrorCheck;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.util.GlobalContext;

import java.util.HashMap;

import static com.joewoo.ontime.support.info.Defines.ACCESS_TOKEN;
import static com.joewoo.ontime.support.info.Defines.GOT_STATUSES_DESTROY_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_STATUSES_DESTROY_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;

public class StatusesDestroy extends Thread {

    private Handler mHandler;
    private String weibo_id;

    public StatusesDestroy(String weibo_id, Handler handler) {
        this.mHandler = handler;
        this.weibo_id = weibo_id;
    }

    public void run() {
        Log.e(TAG, "Statuses Destroy Thread START");
        String httpResult;

        try {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(WEIBO_ID, weibo_id);
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());

            httpResult = new HttpUtility().executePostTask(URLHelper.STATUSES_DESTROY, hm);

            hm = null;

        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(GOT_STATUSES_DESTROY_INFO_FAIL);
            return;
        }

        if(ErrorCheck.getError(httpResult) == null)
            mHandler.sendEmptyMessage(GOT_STATUSES_DESTROY_INFO);
        else
            mHandler.obtainMessage(GOT_STATUSES_DESTROY_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();
    }
}
