package com.joewoo.ontime.action.statuses;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.StatusesBean;
import com.joewoo.ontime.support.bean.UserTimelineBean;
import com.joewoo.ontime.support.error.ErrorCheck;
import com.joewoo.ontime.support.info.AcquireCount;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.util.GlobalContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.joewoo.ontime.support.info.Defines.ACCESS_TOKEN;
import static com.joewoo.ontime.support.info.Defines.BLANK;
import static com.joewoo.ontime.support.info.Defines.BMIDDLE_PIC;
import static com.joewoo.ontime.support.info.Defines.COMMENTS_COUNT;
import static com.joewoo.ontime.support.info.Defines.COUNT;
import static com.joewoo.ontime.support.info.Defines.CREATED_AT;
import static com.joewoo.ontime.support.info.Defines.GOT_USER_TIMELINE_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_USER_TIMELINE_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.IS_REPOST;
import static com.joewoo.ontime.support.info.Defines.MAX_ID;
import static com.joewoo.ontime.support.info.Defines.PROFILE_IMAGE_URL;
import static com.joewoo.ontime.support.info.Defines.REPOSTS_COUNT;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_BMIDDLE_PIC;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_COMMENTS_COUNT;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_CREATED_AT;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_REPOSTS_COUNT;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_SOURCE;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_THUMBNAIL_PIC;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_UID;
import static com.joewoo.ontime.support.info.Defines.SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.SOURCE;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.TEXT;
import static com.joewoo.ontime.support.info.Defines.THUMBNAIL_PIC;
import static com.joewoo.ontime.support.info.Defines.UID;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;

public class StatusesUserTimeLine extends Thread {

    private Handler mHandler;
    private String screenName;
    private String max_id;

    public StatusesUserTimeLine(String screenName, Handler handler) {
        this.screenName = screenName;
        this.mHandler = handler;
    }

    public StatusesUserTimeLine(String screenName, String max_id, Handler handler) {
        this.screenName = screenName;
        this.mHandler = handler;
        this.max_id = max_id;
    }


    public void run() {
        Log.e(TAG, "User Time Line Thread START");
        String httpResult;

        try {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());
            hm.put(SCREEN_NAME, screenName);

            if(max_id == null) {
                hm.put(COUNT, AcquireCount.USER_TIMELINE_COUNT);
            } else {
                hm.put(COUNT, AcquireCount.USER_TIMELINE_ADD_COUNT);
                hm.put(MAX_ID, max_id);
            }

            httpResult = new HttpUtility().executeGetTask(URLHelper.USER_TIMELINE, hm);

            hm = null;
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(GOT_USER_TIMELINE_INFO_FAIL);
            return;
        }

        if (ErrorCheck.getError(httpResult) == null) {

            UserTimelineBean timeline = new Gson().fromJson(httpResult,
                    UserTimelineBean.class);

            ArrayList<HashMap<String, String>> text = new ArrayList<HashMap<String, String>>();

            if (max_id == null) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(BLANK, " ");
                text.add(map);
            }

            List<StatusesBean> statuses = timeline.getStatuses();

            String source;
            String rt_source;

            for (int i = 0; i < statuses.size(); i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                source = statuses.get(i).getSource();
                source = source.substring(source.indexOf(">") + 1,
                        source.length());
                source = source.substring(0, source.indexOf("<"));
                map.put(SOURCE, " · " + source);
                source = statuses.get(i).getCreatedAt();
                source = source.substring(source.indexOf(":") - 2,
                        source.indexOf(":") + 3);
                map.put(CREATED_AT, source);
                map.put(UID, statuses.get(i).getUser().getId());
                map.put(SCREEN_NAME, statuses.get(i).getUser().getScreenName());
                map.put(TEXT, statuses.get(i).getText());
                map.put(COMMENTS_COUNT, statuses.get(i).getCommentsCount());
                map.put(REPOSTS_COUNT, statuses.get(i).getRepostsCount());
                map.put(WEIBO_ID, statuses.get(i).getId());
                map.put(PROFILE_IMAGE_URL, statuses.get(i).getUser()
                        .getProfileImageUrl());

                try {

                    map.put(RETWEETED_STATUS_UID, statuses.get(i)
                            .getRetweetedStatus().getUser().getId());
                    rt_source = statuses.get(i).getRetweetedStatus()
                            .getSource();
                    rt_source = rt_source.substring(rt_source.indexOf(">") + 1,
                            rt_source.length());
                    rt_source = rt_source.substring(0, rt_source.indexOf("<"));
                    map.put(RETWEETED_STATUS_SOURCE, " · " + rt_source);
                    rt_source = statuses.get(i).getRetweetedStatus()
                            .getCreatedAt();
                    rt_source = rt_source.substring(rt_source.indexOf(":") - 2,
                            rt_source.indexOf(":") + 3);
                    map.put(RETWEETED_STATUS_CREATED_AT, rt_source);

                    map.put(RETWEETED_STATUS_COMMENTS_COUNT, statuses.get(i)
                            .getRetweetedStatus().getCommentsCount());
                    map.put(RETWEETED_STATUS_REPOSTS_COUNT, statuses.get(i)
                            .getRetweetedStatus().getRepostsCount());
                    map.put(RETWEETED_STATUS_SCREEN_NAME, statuses.get(i)
                            .getRetweetedStatus().getUser().getScreenName());
                    map.put(RETWEETED_STATUS, statuses.get(i)
                            .getRetweetedStatus().getText());

                    if (statuses.get(i).getRetweetedStatus().getThumbnailPic() != null) {
                        map.put(RETWEETED_STATUS_THUMBNAIL_PIC, statuses.get(i)
                                .getRetweetedStatus().getThumbnailPic());
                        map.put(RETWEETED_STATUS_BMIDDLE_PIC, statuses.get(i)
                                .getRetweetedStatus().getBmiddlePic());
                    }
                    map.put(IS_REPOST, " ");

                } catch (Exception e) {
//                    e.printStackTrace();
                }

                if (statuses.get(i).getThumbnailPic() != null) {
                    map.put(THUMBNAIL_PIC, statuses.get(i).getThumbnailPic());
                    map.put(BMIDDLE_PIC, statuses.get(i).getBmiddlePic());
                }
                text.add(map);
            }

            mHandler.obtainMessage(GOT_USER_TIMELINE_INFO, text).sendToTarget();
        } else {
            mHandler.obtainMessage(GOT_USER_TIMELINE_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();
        }
    }
}