package com.joewoo.ontime.action.auth;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.WeiboBackBean;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.util.GlobalContext;

import java.util.HashMap;

import static com.joewoo.ontime.support.info.Defines.GOT_ACCESS_TOKEN;
import static com.joewoo.ontime.support.info.Defines.TAG;

public class AccessToken extends Thread {

    private Handler mHandler;

    public AccessToken(Handler handler) {
        this.mHandler = handler;
    }

    public void run() {
        Log.e(TAG, "Request Access Token Thread Start");
        String httpResult;

        try {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("client_id", GlobalContext.APP_KEY);
            hm.put("client_secret", GlobalContext.APP_SECRET);
            hm.put("grant_type", "authorization_code");
            hm.put("code", GlobalContext.getAuthCode());
            hm.put("redirect_uri", URLHelper.CALLBACK);

            httpResult = new HttpUtility().executePostTask(URLHelper.TOKEN, hm);

            hm = null;

            WeiboBackBean j = new Gson()
                    .fromJson(httpResult, WeiboBackBean.class);

            mHandler.obtainMessage(GOT_ACCESS_TOKEN, j).sendToTarget();


        } catch (Exception e) {
            Log.e(TAG, "Access Token FAILED");
            e.printStackTrace();
        }
    }
};
