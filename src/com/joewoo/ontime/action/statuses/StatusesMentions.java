package com.joewoo.ontime.action.statuses;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.action.remind.RemindSetCount;
import com.joewoo.ontime.support.bean.MentionsBean;
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
import static com.joewoo.ontime.support.info.Defines.GOT_MENTIONS_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_MENTIONS_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.IS_REPOST;
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

public class StatusesMentions extends Thread {

    private Handler mHandler;
    public boolean isProvidedResult = false;
    private String httpResult;
    private SQLiteDatabase sql;

    public StatusesMentions(boolean isProvided, SQLiteDatabase sql, Handler handler) {
        this.mHandler = handler;
        this.sql = sql;
        this.isProvidedResult = isProvided;
    }

    @Override
    public void run() {
        Log.e(TAG, "StatusesMentions Thread START");


        if (!isProvidedResult) {
            if (!fresh())
                return;
        } else {
            httpResult = MyMaidSQLHelper.getOneString(MyMaidSQLHelper.MENTIONS, sql);
            if(httpResult == null)
                if(!fresh())
                    return;
        }

        sql = null;

        if (ErrorCheck.getError(httpResult) == null) {

            List<StatusesBean> statuses = new Gson().fromJson(httpResult,
                    MentionsBean.class).getStatuses();

            ArrayList<HashMap<String, String>> text = new ArrayList<HashMap<String, String>>();

            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(BLANK, " ");
            text.add(hm);
            hm = null;

            String source;

            for (StatusesBean s : statuses) {
                HashMap<String, String> map = new HashMap<String, String>();
                source = s.getSource();
                map.put(SOURCE, " · " + source.substring(source.indexOf(">") + 1,
                        source.indexOf("</a>")));
                map.put(CREATED_AT, TimeFormat.parse(s.getCreatedAt()));
                map.put(SCREEN_NAME, s.getUser().getScreenName());
                map.put(TEXT, s.getText());
                map.put(WEIBO_ID, s.getId());
                map.put(COMMENTS_COUNT, s.getCommentsCount());
                map.put(REPOSTS_COUNT, s.getRepostsCount());
                map.put(UID, s.getUser().getId());
                map.put(PROFILE_IMAGE_URL, s.getUser()
                        .getProfileImageUrl());


                try {
                    map.put(RETWEETED_STATUS_UID, s
                            .getRetweetedStatus().getUser().getId());
                    source = s.getRetweetedStatus()
                            .getSource();
                    map.put(RETWEETED_STATUS_SOURCE, " · " + source.substring(source.indexOf(">") + 1,
                            source.indexOf("</a>")));
                    map.put(RETWEETED_STATUS_CREATED_AT, TimeFormat.parse(s.getRetweetedStatus().getCreatedAt()));
                    map.put(RETWEETED_STATUS_UID, s
                            .getRetweetedStatus().getUser().getId());
                    map.put(RETWEETED_STATUS_SCREEN_NAME, s
                            .getRetweetedStatus().getUser().getScreenName());
                    map.put(RETWEETED_STATUS, s
                            .getRetweetedStatus().getText());
                    map.put(RETWEETED_STATUS_COMMENTS_COUNT, s
                            .getRetweetedStatus().getCommentsCount());
                    map.put(RETWEETED_STATUS_REPOSTS_COUNT, s
                            .getRetweetedStatus().getRepostsCount());

                    if (s.getRetweetedStatus().getThumbnailPic() != null) {
                        map.put(RETWEETED_STATUS_THUMBNAIL_PIC, s
                                .getRetweetedStatus().getThumbnailPic());
                        map.put(RETWEETED_STATUS_BMIDDLE_PIC, s
                                .getRetweetedStatus().getBmiddlePic());
                    }
                    map.put(IS_REPOST, " ");

                } catch (Exception e) {
//                    e.printStackTrace();
                }

                if (s.getThumbnailPic() != null) {
                    map.put(THUMBNAIL_PIC, s.getThumbnailPic());
                    map.put(BMIDDLE_PIC, s.getBmiddlePic());
                }

                text.add(map);
            }

            mHandler.obtainMessage(GOT_MENTIONS_INFO, text).sendToTarget();

            if(!isProvidedResult)
                new RemindSetCount(mHandler)
                        .execute(RemindSetCount.setMentionsCount);

        } else {
            mHandler.obtainMessage(GOT_MENTIONS_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();
        }
    }

    private boolean fresh() {
        try {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());
            hm.put(COUNT, AcquireCount.MENTIONS_COUNT);

            httpResult = new HttpUtility().executeGetTask(URLHelper.MENTIONS, hm);

            hm = null;

            MyMaidSQLHelper.saveOneString(MyMaidSQLHelper.MENTIONS, httpResult, sql);

            return true;

        } catch (Exception e) {
            mHandler.sendEmptyMessage(GOT_MENTIONS_INFO_FAIL);
            e.printStackTrace();
            return false;
        }
    }
}
