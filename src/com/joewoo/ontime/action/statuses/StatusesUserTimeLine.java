package com.joewoo.ontime.action.statuses;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.R;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.StatusesBean;
import com.joewoo.ontime.support.bean.UserTimelineBean;
import com.joewoo.ontime.support.error.ErrorCheck;
import com.joewoo.ontime.support.info.AcquireCount;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.util.GlobalContext;
import com.joewoo.ontime.support.util.TimeFormat;

import java.util.HashMap;
import java.util.List;

import static com.joewoo.ontime.support.info.Defines.ACCESS_TOKEN;
import static com.joewoo.ontime.support.info.Defines.COUNT;
import static com.joewoo.ontime.support.info.Defines.GOT_USER_TIMELINE_ADD_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_USER_TIMELINE_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_USER_TIMELINE_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.MAX_ID;
import static com.joewoo.ontime.support.info.Defines.SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.TAG;

public class StatusesUserTimeLine extends Thread {

    private Handler mHandler;
    private String screenName;
    private String maxID;

    public StatusesUserTimeLine(String screenName, Handler handler) {
        this.screenName = screenName;
        this.mHandler = handler;
    }

    public StatusesUserTimeLine(String screenName, String maxID, Handler handler) {
        this.screenName = screenName;
        this.mHandler = handler;
        this.maxID = maxID;
    }


    public void run() {
        Log.e(TAG, "User Time Line Thread START");
        String httpResult;

        try {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());
            hm.put(SCREEN_NAME, screenName);

            if(maxID == null) {
                hm.put(COUNT, AcquireCount.USER_TIMELINE_COUNT);
            } else {
                hm.put(COUNT, AcquireCount.USER_TIMELINE_ADD_COUNT);
                hm.put(MAX_ID, maxID);
            }

            httpResult = new HttpUtility().executeGetTask(URLHelper.USER_TIMELINE, hm);

            hm = null;
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.obtainMessage(GOT_USER_TIMELINE_INFO_FAIL, GlobalContext.getResString(R.string.toast_user_timeline_fail)).sendToTarget();
            return;
        }

        if (ErrorCheck.getError(httpResult) == null) {

//            ArrayList<HashMap<String, String>> text = new ArrayList<HashMap<String, String>>();

//            if (maxID == null) {
//                HashMap<String, String> map = new HashMap<String, String>();
//                map.put(BLANK, " ");
//                text.add(map);
//            }

            List<StatusesBean> statuses = new Gson().fromJson(httpResult, UserTimelineBean.class).getStatuses();

            String source;

            for (StatusesBean s : statuses) {

                s.setCreatedAt(TimeFormat.parse(s.getCreatedAt()));

                source = s.getSource();
                source = source.substring(source.indexOf(">") + 1,
                        source.indexOf("</a>"));
                s.setSource(source);

                if(s.getRetweetedStatus() != null) {

                    s.getRetweetedStatus().setCreatedAt(TimeFormat.parse(s.getRetweetedStatus().getCreatedAt()));

                    source = s.getRetweetedStatus().getSource();

                    if(source != null) {
                        source = source.substring(source.indexOf(">") + 1,
                                source.indexOf("</a>"));
                        s.getRetweetedStatus().setSource(source);
                    }
                }

//                HashMap<String, String> map = new HashMap<>();
//                source = s.getSource();
//
//                map.put(SOURCE, " · " + source.substring(source.indexOf(">") + 1,
//                        source.indexOf("</a>")));
//                map.put(CREATED_AT, TimeFormat.parse(s.getCreatedAt()));
//                map.put(UID, s.getUser().getId());
//                map.put(SCREEN_NAME, s.getUser().getScreenName());
//                map.put(TEXT, s.getText());
//                map.put(COMMENTS_COUNT, s.getCommentsCount());
//                map.put(REPOSTS_COUNT, s.getRepostsCount());
//                map.put(WEIBO_ID, s.getId());
//                map.put(PROFILE_IMAGE_URL, s.getUser()
//                        .getProfileImageUrl());
//
//                if(s.getPicURLs() != null && s.getPicURLs().size() > 1)
//                    map.put(PIC_URLS, " ");
//
//                try {
//
//                    map.put(RETWEETED_STATUS_UID, s
//                            .getRetweetedStatus().getUser().getId());
//                    source = s.getRetweetedStatus()
//                            .getSource();
//                    map.put(RETWEETED_STATUS_SOURCE, " · " + source.substring(source.indexOf(">") + 1,
//                            source.indexOf("</a>")));
//                    map.put(RETWEETED_STATUS_CREATED_AT, TimeFormat.parse(s.getRetweetedStatus().getCreatedAt()));
//                    map.put(RETWEETED_STATUS_COMMENTS_COUNT, s
//                            .getRetweetedStatus().getCommentsCount());
//                    map.put(RETWEETED_STATUS_REPOSTS_COUNT, s
//                            .getRetweetedStatus().getRepostsCount());
//                    map.put(RETWEETED_STATUS_SCREEN_NAME, s
//                            .getRetweetedStatus().getUser().getScreenName());
//                    map.put(RETWEETED_STATUS, s
//                            .getRetweetedStatus().getText());
//
//                    if(s.getRetweetedStatus().getPicURLs() != null && s.getRetweetedStatus().getPicURLs().size() > 1)
//                        map.put(PIC_URLS, " ");
//
//                    if (s.getRetweetedStatus().getThumbnailPic() != null) {
//                        map.put(RETWEETED_STATUS_THUMBNAIL_PIC, s
//                                .getRetweetedStatus().getThumbnailPic());
//                        map.put(RETWEETED_STATUS_BMIDDLE_PIC, s
//                                .getRetweetedStatus().getBmiddlePic());
//                    }
//                    map.put(IS_REPOST, " ");
//
//                } catch (Exception e) {
////                    e.printStackTrace();
//                }
//
//                if (s.getThumbnailPic() != null) {
//                    map.put(THUMBNAIL_PIC, s.getThumbnailPic());
//                    map.put(BMIDDLE_PIC, s.getBmiddlePic());
//                }
//                text.add(map);
            }

            if(maxID == null)
                mHandler.obtainMessage(GOT_USER_TIMELINE_INFO, statuses).sendToTarget();
            else {
                statuses.remove(0);
                mHandler.obtainMessage(GOT_USER_TIMELINE_ADD_INFO, statuses).sendToTarget();

            }

        } else {
            mHandler.obtainMessage(GOT_USER_TIMELINE_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();
        }
    }
}
