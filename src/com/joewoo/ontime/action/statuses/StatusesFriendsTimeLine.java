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
import com.joewoo.ontime.support.util.TimeFormat;

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
import static com.joewoo.ontime.support.info.Defines.PIC_URLS;
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

        try {

            if (!isProvidedResult) {
                if (!fresh())
                    return;
            } else {
                httpResult = MyMaidSQLHelper.getOneString(MyMaidSQLHelper.FRIENDS_TIMELINE, sql);

                if (httpResult == null)
                    if (!fresh())
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

                FriendsTimelineBean f = new Gson().fromJson(httpResult,
                        FriendsTimelineBean.class);

                List<StatusesBean> statuses = f.getStatuses();

                String[] adIDs = new String[f.getAd().size()];
                for (int i = 0; i < f.getAd().size(); i++) {
                    adIDs[i] = f.getAd().get(i).getId();
                    Log.e(TAG, "Ad: " + adIDs[i]);
                }

                String source;
//                int index = 0;

                outer:
                for (StatusesBean s : statuses) {

//                    index++;
////                Log.e(TAG, String.valueOf(index));
//
//                if (index == 3 && GlobalContext.getFriendsIDs() != null) {
//                    boolean isAd = true;
//                    long thisUserID = Long.valueOf(s.getUser().getId());
//                    long[] ids = GlobalContext.getFriendsIDs();
//                    for (long id : ids) {
//                        if (thisUserID == id) {
//                            isAd = false;
//                            break;
//                        }
//                    }
//                    ids = null;
//                    if (isAd) {
//                        Log.e(TAG, "HAAAAAAAA! CAUGHT AN AD!!!!");
//                        continue;
//                    }
//                }

                    for (String adID : adIDs)
                        if (s.getId().equals(adID)) {
                            Log.e(TAG, "HAAAAAAAA! CAUGHT AN AD!!!!");
                            continue outer;
                        }


                    HashMap<String, String> map = new HashMap<>();

                    source = s.getSource();

                    map.put(SOURCE, " · " + source.substring(source.indexOf(">") + 1,
                            source.indexOf("</a>")));

                    map.put(CREATED_AT, TimeFormat.parse(s.getCreatedAt()));

                    map.put(UID, s.getUser().getId());
                    map.put(SCREEN_NAME, s.getUser().getScreenName());
                    map.put(TEXT, s.getText());
                    map.put(COMMENTS_COUNT, s.getCommentsCount());
                    map.put(REPOSTS_COUNT, s.getRepostsCount());
                    map.put(WEIBO_ID, s.getId());
                    map.put(PROFILE_IMAGE_URL, s.getUser()
                            .getProfileImageUrl());

                    if (s.getPicURLs() != null && s.getPicURLs().size() > 1)
                        map.put(PIC_URLS, " ");


                    try {

                        map.put(RETWEETED_STATUS_UID, s
                                .getRetweetedStatus().getUser().getId());

                        source = s.getRetweetedStatus()
                                .getSource();

                        map.put(RETWEETED_STATUS_SOURCE, " · " + source.substring(source.indexOf(">") + 1,
                                source.indexOf("</a>")));

                        map.put(RETWEETED_STATUS_CREATED_AT, TimeFormat.parse(s.getRetweetedStatus().getCreatedAt()));

                        map.put(RETWEETED_STATUS_COMMENTS_COUNT, s
                                .getRetweetedStatus().getCommentsCount());
                        map.put(RETWEETED_STATUS_REPOSTS_COUNT, s
                                .getRetweetedStatus().getRepostsCount());
                        map.put(RETWEETED_STATUS_SCREEN_NAME, s
                                .getRetweetedStatus().getUser().getScreenName());
                        map.put(RETWEETED_STATUS, s
                                .getRetweetedStatus().getText());

                        if (s.getRetweetedStatus().getPicURLs() != null && s.getRetweetedStatus().getPicURLs().size() > 1)
                            map.put(PIC_URLS, " ");

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
                f = null;
                adIDs = null;
                source = null;


                if (max_id == null)
                    mHandler.obtainMessage(GOT_FRIENDS_TIMELINE_INFO, text)
                            .sendToTarget();
                else
                    mHandler.obtainMessage(GOT_FRIENDS_TIMELINE_ADD_INFO, text)
                            .sendToTarget();


            } else {
                mHandler.obtainMessage(GOT_FRIENDS_TIMELINE_INFO_FAIL, ErrorCheck.getError(httpResult));
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(GOT_FRIENDS_TIMELINE_INFO_FAIL);
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
