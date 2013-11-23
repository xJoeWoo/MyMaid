package com.joewoo.ontime.action.statuses;

import static com.joewoo.ontime.support.info.Defines.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.FriendsTimelineBean;
import com.joewoo.ontime.support.bean.StatusesBean;
import com.joewoo.ontime.support.error.ErrorCheck;
import com.joewoo.ontime.support.info.AcquireCount;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.sql.MyMaidSQLHelper;
import com.joewoo.ontime.support.util.GlobalContext;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

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

    public StatusesFriendsTimeLine(SQLiteDatabase sql,
                                   Handler handler) {
        this.mHandler = handler;
        this.sql = sql;
    }

    public StatusesFriendsTimeLine(boolean isProvided, String httpResult, Handler handler) {
        this.mHandler = handler;
        this.httpResult = httpResult;
        this.isProvidedResult = isProvided;
    }

    @Override
    public void run() {
        Log.e(TAG, "Friends TimeLine Thread START");

        if (!isProvidedResult) {
            try {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());

                if(max_id == null) {
                    hm.put(COUNT, AcquireCount.FRIENDS_TIMELINE_COUNT);
                } else {
                    hm.put(COUNT, AcquireCount.FRIENDS_TIMELINE_ADD_COUNT);
                    hm.put(MAX_ID, max_id);
                }

                httpResult = new HttpUtility().executeGetTask(URLHelper.FRIENDS_TIMELINE, hm);

                hm = null;

            } catch (Exception e) {
                mHandler.sendEmptyMessage(GOT_FRIENDS_TIMELINE_INFO_FAIL);
                e.printStackTrace();
                return;
            }
        }

        if (ErrorCheck.getError(httpResult) == null) {
            FriendsTimelineBean timeline = new Gson().fromJson(httpResult,
                    FriendsTimelineBean.class);

            ArrayList<HashMap<String, String>> text = new ArrayList<HashMap<String, String>>();

            if (max_id == null) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(BLANK, " ");
                text.add(map);
                map = null;
            }

            List<StatusesBean> statuses = timeline.getStatuses();

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


            if (max_id == null)
                mHandler.obtainMessage(GOT_FRIENDS_TIMELINE_INFO, text)
                        .sendToTarget();
            else
                mHandler.obtainMessage(GOT_FRIENDS_TIMELINE_ADD_INFO, text)
                        .sendToTarget();

            if (sql != null && !isProvidedResult && max_id == null) {
                ContentValues cv = new ContentValues();
                cv.put(MyMaidSQLHelper.FRIENDS_TIMELINE, httpResult);
                if (sql.update(MyMaidSQLHelper.tableName, cv, MyMaidSQLHelper.UID + "='"
                        + GlobalContext.getUID() + "'", null) != 0) {
                    Log.e(MyMaidSQLHelper.TAG_SQL, "Saved httpResult");
                }
            }
        } else {
            mHandler.obtainMessage(GOT_FRIENDS_TIMELINE_INFO_FAIL, ErrorCheck.getError(httpResult));
        }


    }
}
