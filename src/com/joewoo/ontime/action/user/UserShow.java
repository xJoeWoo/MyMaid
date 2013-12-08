package com.joewoo.ontime.action.user;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.R;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.WeiboBackBean;
import com.joewoo.ontime.support.error.ErrorCheck;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.util.GlobalContext;

import java.util.HashMap;

import static com.joewoo.ontime.support.info.Defines.ACCESS_TOKEN;
import static com.joewoo.ontime.support.info.Defines.GOT_SHOW_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_SHOW_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.UID;

public class UserShow extends Thread {

    private Handler mHandler;
    private String screenName = null;

    public UserShow(Handler handler) {
        this.mHandler = handler;
    }

    public UserShow(String screenName, Handler handler) {
        this.screenName = screenName;
        this.mHandler = handler;
    }

    public void run() {
        Log.e(TAG, "Show User Info Thread START");
        String httpResult;

        try {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());
            if(screenName != null)
                hm.put(SCREEN_NAME, screenName);
            else
                hm.put(UID, GlobalContext.getUID());

            httpResult = new HttpUtility().executeGetTask(URLHelper.USER_SHOW, hm);

            hm = null;

        } catch (Exception e) {
            mHandler.obtainMessage(GOT_SHOW_INFO_FAIL, GlobalContext.getAppContext().getString(R.string.toast_user_timeline_fail)).sendToTarget();
            e.printStackTrace();
            return;
        }

        if (ErrorCheck.getError(httpResult) == null) {
            mHandler.obtainMessage(GOT_SHOW_INFO, new Gson().fromJson(httpResult, WeiboBackBean.class))
                    .sendToTarget();
        } else {
            mHandler.obtainMessage(GOT_SHOW_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();
        }


    }
}
