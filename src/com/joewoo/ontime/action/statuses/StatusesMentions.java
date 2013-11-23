package com.joewoo.ontime.action.statuses;

import android.content.ContentValues;
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

    public StatusesMentions(SQLiteDatabase sql, Handler handler) {
        this.mHandler = handler;
        this.sql = sql;
    }

    public StatusesMentions(boolean isProvided, String httpResult, Handler handler) {
        this.mHandler = handler;
        this.httpResult = httpResult;
        isProvidedResult = isProvided;
    }

    @Override
    public void run() {
        Log.e(TAG, "StatusesMentions Thread START");

        if (!isProvidedResult) {
//
//            HttpUriRequest httpGet = new HttpGet(URLHelper.MENTIONS
//                    + "?access_token=" + GlobalContext.getAccessToken() + "&count="
//                    + count);
//            httpGet.addHeader("Accept-Encoding", "gzip");
//
//            try {
//
//                InputStream is = new DefaultHttpClient().execute(httpGet)
//                        .getEntity().getContent();
//
//                is = new GZIPInputStream(is);
//
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//                int i = -1;
//                while ((i = is.read()) != -1) {
//                    baos.write(i);
//                }
//
//                httpResult = baos.toString();
//                is.close();
//                baos.close();
//
//                Log.e(TAG,
//                        "GOT Statues length: "
//                                + String.valueOf(httpResult.length()));
//                Log.e(TAG, httpResult);

            try {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());
                hm.put(COUNT, AcquireCount.MENTIONS_COUNT);

                httpResult = new HttpUtility().executeGetTask(URLHelper.MENTIONS, hm);

                hm = null;
            } catch (Exception e) {
                mHandler.sendEmptyMessage(GOT_MENTIONS_INFO_FAIL);
                e.printStackTrace();
                return;
            }
        }

        if (ErrorCheck.getError(httpResult) == null) {

            MentionsBean mentions = new Gson().fromJson(httpResult,
                    MentionsBean.class);

            List<StatusesBean> statuses = mentions.getStatuses();

            ArrayList<HashMap<String, String>> text = new ArrayList<HashMap<String, String>>();


            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(BLANK, " ");
            text.add(hm);
            hm = null;

            String source;
            String rt_source;

            for (StatusesBean s : statuses) {
                HashMap<String, String> map = new HashMap<String, String>();
                source = s.getSource();
                source = source.substring(source.indexOf(">") + 1,
                        source.length());
                source = source.substring(0, source.indexOf("<"));
                map.put(SOURCE, " · " + source);
                source = s.getCreatedAt();
                source = source.substring(source.indexOf(":") - 2,
                        source.indexOf(":") + 3);
                map.put(CREATED_AT, source);
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

            if (sql != null && !isProvidedResult) {
                ContentValues cv = new ContentValues();
                cv.put(MyMaidSQLHelper.MENTIONS, httpResult);
                if (sql.update(MyMaidSQLHelper.tableName, cv, MyMaidSQLHelper.UID + "='"
                        + GlobalContext.getUID() + "'", null) != 0) {
                    Log.e(MyMaidSQLHelper.TAG_SQL, "Saved StatusesMentions httpResult");
                }
            }
        } else {
            mHandler.obtainMessage(GOT_MENTIONS_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();
        }


    }
}
