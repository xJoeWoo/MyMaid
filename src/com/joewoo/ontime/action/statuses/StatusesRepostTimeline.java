package com.joewoo.ontime.action.statuses;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.RepostTimelineBean;
import com.joewoo.ontime.support.bean.StatusesBean;
import com.joewoo.ontime.support.info.AcquireCount;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.util.GlobalContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.joewoo.ontime.support.info.Defines.ACCESS_TOKEN;
import static com.joewoo.ontime.support.info.Defines.COUNT;
import static com.joewoo.ontime.support.info.Defines.GOT_REPOST_TIMELINE_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_REPOST_TIMELINE_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.MAX_ID;
import static com.joewoo.ontime.support.info.Defines.REPOST_WEIBO_ID;
import static com.joewoo.ontime.support.info.Defines.SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.TEXT;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;

/**
 * Created by JoeWoo on 13-10-19.
 */
public class StatusesRepostTimeline extends Thread {

    private Handler mHandler;
    private String weibo_id;
    private String max_id;

    public StatusesRepostTimeline(String weibo_id, Handler handler) {
        this.weibo_id = weibo_id;
        this.mHandler = handler;
    }

    public StatusesRepostTimeline(String weibo_id, String max_id, Handler handler) {
        this.weibo_id = weibo_id;
        this.mHandler = handler;
        this.max_id = max_id;
    }

    public void run() {
        String httpResult = null;
        Log.e(TAG, "Reposts Timeline Thread Start");
        try {

            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());
            hm.put(WEIBO_ID, weibo_id);

            if(max_id == null) {
                hm.put(COUNT, AcquireCount.REPOSTS_TIMELINE_COUNT);
            } else {
                hm.put(COUNT, AcquireCount.REPOSTS_TIMELINE_ADD_COUNT);
                hm.put(MAX_ID, max_id);
            }

            httpResult = new HttpUtility().executeGetTask(URLHelper.REPOST_TIMELINE, hm);

            hm = null;

        } catch (Exception e) {
            mHandler.sendEmptyMessage(GOT_REPOST_TIMELINE_INFO_FAIL);
            e.printStackTrace();
            return;
        }

        try {
            List<StatusesBean> reposts = new Gson().fromJson(httpResult,
                    RepostTimelineBean.class).getReposts();

            ArrayList<HashMap<String, String>> text = new ArrayList<HashMap<String, String>>();


            for (StatusesBean c : reposts) {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put(SCREEN_NAME, c.getUser().getScreenName());
                map.put(TEXT, c.getText());
                map.put(REPOST_WEIBO_ID, c.getId());

                text.add(map);
            }

            mHandler.obtainMessage(GOT_REPOST_TIMELINE_INFO, text)
                    .sendToTarget();

        } catch (Exception e) {
            mHandler.sendEmptyMessage(GOT_REPOST_TIMELINE_INFO_FAIL);
            e.printStackTrace();
        }
    }
}
