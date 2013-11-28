package com.joewoo.ontime.action.statuses;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.FriendsTimelineBean;
import com.joewoo.ontime.support.bean.StatusesBean;
import com.joewoo.ontime.support.error.ErrorCheck;
import com.joewoo.ontime.support.info.AcquireCount;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.sql.MyMaidSQLHelper;
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
import static com.joewoo.ontime.support.info.Defines.GOT_FRIENDS_TIMELINE_ADD_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_FRIENDS_TIMELINE_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_FRIENDS_TIMELINE_INFO_FAIL;
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

public class StatusesFriendsTimeLine extends Thread {

    private Handler mHandler;
    public boolean isProvidedResult = false;
    private String httpResult;
    private SQLiteDatabase sql;
    private String max_id = null;
    private int index = -1;

    public StatusesFriendsTimeLine(String max_id, Handler handler) {
        this.mHandler = handler;
        this.max_id = max_id;
    }

    public StatusesFriendsTimeLine(boolean isProvided, SQLiteDatabase sql, Handler handler) {
        this.mHandler = handler;
        this.isProvidedResult = isProvided;
        this.sql = sql;
    }

    @Override
    public void run() {
        Log.e(TAG, "Friends TimeLine Thread START");

        if (!isProvidedResult) {
            if (!fresh())
                return;
        } else {
            httpResult = MyMaidSQLHelper.getOneString(MyMaidSQLHelper.FRIENDS_TIMELINE, sql);

            if(httpResult == null)
                if(!fresh())
                    return;
        }

        sql = null;

        if (ErrorCheck.getError(httpResult) == null) {

            ArrayList<HashMap<String, String>> text = new ArrayList<>();

            if (max_id == null) {
                HashMap<String, String> map = new HashMap<>();
                map.put(BLANK, " ");
                text.add(map);
                map = null;
            }

            List<StatusesBean> statuses = new Gson().fromJson(httpResult,
                    FriendsTimelineBean.class).getStatuses();

            String source;
            String rt_source;

            for (StatusesBean s : statuses) {

                index++;
//                Log.e(TAG, String.valueOf(index));

                if (index == 2 && GlobalContext.getFriendsIDs() != null) {
                    boolean isAd = true;
                    long thisUserID = Long.valueOf(s.getUser().getId());
                    long[] ids = GlobalContext.getFriendsIDs();
                    for (long id : ids) {
                        if (thisUserID == id) {
                            isAd = false;
                            break;
                        }
                    }
                    ids = null;
                    if (isAd) {
                        Log.e(TAG, "HAAAAAAAA! CAUGHT AN AD!!!!");
                        continue;
                    }
                }


                HashMap<String, String> map = new HashMap<>();


                source = s.getSource();
                source = source.substring(source.indexOf(">") + 1,
                        source.length());
                source = source.substring(0, source.indexOf("<"));
                map.put(SOURCE, " · " + source);
                source = s.getCreatedAt();
                source = source.substring(source.indexOf(":") - 2,
                        source.indexOf(":") + 3);
                map.put(CREATED_AT, source);
                map.put(UID, s.getUser().getId());
                map.put(SCREEN_NAME, s.getUser().getScreenName());
                map.put(TEXT, s.getText());
                map.put(COMMENTS_COUNT, s.getCommentsCount());
                map.put(REPOSTS_COUNT, s.getRepostsCount());
                map.put(WEIBO_ID, s.getId());
                map.put(PROFILE_IMAGE_URL, s.getUser()
                        .getProfileImageUrl());

//                try {
//                    for (int i = 0; i < s.getPicURLs().size(); i++) {
//                        Log.e(TAG, String.valueOf(i) + " : " + s.getPicURLs().get(i).getThumbnailPic());
//                    }
//                } catch (Exception e) {
//                    Log.e(TAG, "Not muilt pics");
//                }


                try {

                    map.put(RETWEETED_STATUS_UID, s
                            .getRetweetedStatus().getUser().getId());
                    rt_source = s.getRetweetedStatus()
                            .getSource();
                    rt_source = rt_source.substring(rt_source.indexOf(">") + 1,
                            rt_source.length());
                    rt_source = rt_source.substring(0, rt_source.indexOf("<"));
                    map.put(RETWEETED_STATUS_SOURCE, " · " + rt_source);
                    rt_source = s.getRetweetedStatus()
                            .getCreatedAt();
                    rt_source = rt_source.substring(rt_source.indexOf(":") - 2,
                            rt_source.indexOf(":") + 3);
                    map.put(RETWEETED_STATUS_CREATED_AT, rt_source);

                    map.put(RETWEETED_STATUS_COMMENTS_COUNT, s
                            .getRetweetedStatus().getCommentsCount());
                    map.put(RETWEETED_STATUS_REPOSTS_COUNT, s
                            .getRetweetedStatus().getRepostsCount());
                    map.put(RETWEETED_STATUS_SCREEN_NAME, s
                            .getRetweetedStatus().getUser().getScreenName());
                    map.put(RETWEETED_STATUS, s
                            .getRetweetedStatus().getText());

                    if (s.getRetweetedStatus().getThumbnailPic() != null) {
                        map.put(RETWEETED_STATUS_THUMBNAIL_PIC, s
                                .getRetweetedStatus().getThumbnailPic());
                        map.put(RETWEETED_STATUS_BMIDDLE_PIC, s
                                .getRetweetedStatus().getBmiddlePic());
                    }
                    map.put(IS_REPOST, " ");

                } catch (Exception e) {

                }

                if (s.getThumbnailPic() != null) {
                    map.put(THUMBNAIL_PIC, s.getThumbnailPic());
                    map.put(BMIDDLE_PIC, s.getBmiddlePic());
                }
                text.add(map);
            }

            statuses = null;


            if (max_id == null)
                mHandler.obtainMessage(GOT_FRIENDS_TIMELINE_INFO, text)
                        .sendToTarget();
            else
                mHandler.obtainMessage(GOT_FRIENDS_TIMELINE_ADD_INFO, text)
                        .sendToTarget();


        } else {
            mHandler.obtainMessage(GOT_FRIENDS_TIMELINE_INFO_FAIL, ErrorCheck.getError(httpResult));
        }


    }

    private boolean fresh() {
        try {
            HashMap<String, String> hm = new HashMap<>();
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());

            if (max_id == null) {
                hm.put(COUNT, AcquireCount.FRIENDS_TIMELINE_COUNT);
            } else {
                hm.put(COUNT, AcquireCount.FRIENDS_TIMELINE_ADD_COUNT);
                hm.put(MAX_ID, max_id);
            }

            httpResult = new HttpUtility().executeGetTask(URLHelper.FRIENDS_TIMELINE, hm);

            hm = null;

            MyMaidSQLHelper.saveOneString(MyMaidSQLHelper.FRIENDS_TIMELINE, httpResult, sql);

            return true;

        } catch (Exception e) {
            mHandler.sendEmptyMessage(GOT_FRIENDS_TIMELINE_INFO_FAIL);
            e.printStackTrace();
            return false;
        }
    }
}
