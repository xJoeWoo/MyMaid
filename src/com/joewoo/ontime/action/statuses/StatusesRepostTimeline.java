package com.joewoo.ontime.action.statuses;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.R;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.RepostTimelineBean;
import com.joewoo.ontime.support.bean.StatusesBean;
import com.joewoo.ontime.support.error.ErrorCheck;
import com.joewoo.ontime.support.info.AcquireCount;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.util.GlobalContext;

import java.util.HashMap;
import java.util.List;

import static com.joewoo.ontime.support.info.Defines.ACCESS_TOKEN;
import static com.joewoo.ontime.support.info.Defines.COUNT;
import static com.joewoo.ontime.support.info.Defines.GOT_REPOST_TIMELINE_ADD_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_REPOST_TIMELINE_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_REPOST_TIMELINE_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.MAX_ID;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;

/**
 * Created by JoeWoo on 13-10-19.
 */
public class StatusesRepostTimeline extends Thread {

    private Handler mHandler;
    private String weiboID;
    private String maxID;

    public StatusesRepostTimeline(String weiboID, Handler handler) {
        this.weiboID = weiboID;
        this.mHandler = handler;
    }

    public StatusesRepostTimeline(String weiboID, String maxID, Handler handler) {
        this.weiboID = weiboID;
        this.mHandler = handler;
        this.maxID = maxID;
    }

    public void run() {
        String httpResult = null;
        Log.e(TAG, "Reposts Timeline Thread Start");
        try {

            HashMap<String, String> hm = new HashMap<>();
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());
            hm.put(WEIBO_ID, weiboID);

            if(maxID == null) {
                hm.put(COUNT, AcquireCount.REPOSTS_TIMELINE_COUNT);
            } else {
                hm.put(COUNT, AcquireCount.REPOSTS_TIMELINE_ADD_COUNT);
                hm.put(MAX_ID, maxID);
            }

            httpResult = new HttpUtility().executeGetTask(URLHelper.REPOST_TIMELINE, hm);

            hm = null;

        } catch (Exception e) {
            mHandler.obtainMessage(GOT_REPOST_TIMELINE_INFO_FAIL, GlobalContext.getResString(R.string.toast_repost_timeline_fail)).sendToTarget();
            e.printStackTrace();
            return;
        }

        if (ErrorCheck.getError(httpResult) == null) {

            List<StatusesBean> statuses = new Gson().fromJson(httpResult,
                    RepostTimelineBean.class).getReposts();


            if(maxID == null)
                mHandler.obtainMessage(GOT_REPOST_TIMELINE_INFO, statuses).sendToTarget();
            else
            {
                statuses.remove(0);
                mHandler.obtainMessage(GOT_REPOST_TIMELINE_ADD_INFO, statuses).sendToTarget();
            }

        } else {
            mHandler.obtainMessage(GOT_REPOST_TIMELINE_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();
        }
    }
}
