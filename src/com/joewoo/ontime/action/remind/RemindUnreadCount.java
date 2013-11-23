package com.joewoo.ontime.action.remind;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.UnreadCountBean;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.util.GlobalContext;

import java.util.HashMap;

import static com.joewoo.ontime.support.info.Defines.ACCESS_TOKEN;
import static com.joewoo.ontime.support.info.Defines.GOT_UNREAD_COUNT_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_UNREAD_COUNT_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.UID;

public class RemindUnreadCount extends Thread {

    private Handler mHandler;
    private String httpResult;

    public RemindUnreadCount(Handler handler) {
        this.mHandler = handler;
    }

    public void run() {
        Log.e(TAG, "Unread Count Thread START");
        try {

            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());
            hm.put(UID, GlobalContext.getUID());

            httpResult = new HttpUtility().executeGetTask(URLHelper.UNREAD_COUNT, hm);

            hm = null;

            UnreadCountBean b = new Gson().fromJson(httpResult,
                    UnreadCountBean.class);

            if (b.getCmtCount() != null)
                mHandler.obtainMessage(GOT_UNREAD_COUNT_INFO, b).sendToTarget();
            else
                mHandler.sendEmptyMessage(GOT_UNREAD_COUNT_INFO_FAIL);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
