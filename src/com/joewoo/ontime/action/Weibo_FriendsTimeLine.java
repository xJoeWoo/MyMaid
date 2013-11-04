package com.joewoo.ontime.action;

import static com.joewoo.ontime.info.Defines.*;

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
import com.joewoo.ontime.bean.FriendsTimelineBean;
import com.joewoo.ontime.bean.StatusesBean;
import com.joewoo.ontime.info.WeiboConstant;
import com.joewoo.ontime.info.Weibo_URLs;
import com.joewoo.ontime.tools.MySQLHelper;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

public class Weibo_FriendsTimeLine extends Thread {

    private String count;
    private Handler mHandler;
    public boolean isProvidedResult = false;
    private String httpResult = "{ \"error_code\" : \"233\" }";
    private MySQLHelper sqlHelper;
    private String max_id = null;

    public Weibo_FriendsTimeLine(int count, Handler handler) {
        this.count = String.valueOf(count);
        this.mHandler = handler;
    }

    public Weibo_FriendsTimeLine(String max_id, int count, Handler handler) {
        this.count = String.valueOf(count);
        this.mHandler = handler;
        this.max_id = max_id;
    }

    public Weibo_FriendsTimeLine(int count, MySQLHelper sqlHelper,
                                 Handler handler) {
        this.count = String.valueOf(count);
        this.mHandler = handler;
        this.sqlHelper = sqlHelper;
    }

    public Weibo_FriendsTimeLine(String httpResult, Handler handler) {
        this.mHandler = handler;
        this.httpResult = httpResult;
        isProvidedResult = true;
    }

    @Override
    public void run() {
        Log.e(TAG, "Friends TimeLine Thread START");

        if (!isProvidedResult) {
            HttpUriRequest httpGet;

            if (max_id == null) {
                httpGet = new HttpGet(Weibo_URLs.FRIENDS_TIMELINE + "?access_token="
                        + WeiboConstant.ACCESS_TOKEN + "&count=" + count);
            } else {
                httpGet = new HttpGet(Weibo_URLs.FRIENDS_TIMELINE + "?access_token="
                        + WeiboConstant.ACCESS_TOKEN + "&count=" + count
                        + "&max_id=" + max_id);
            }

            httpGet.addHeader("Accept-Encoding", "gzip");

            try {

                InputStream is = new DefaultHttpClient().execute(httpGet)
                        .getEntity().getContent();

                is = new GZIPInputStream(is);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                int i = -1;
                while ((i = is.read()) != -1) {
                    baos.write(i);
                }

                httpResult = baos.toString();

                Log.e(TAG,
                        "GOT Statues length: "
                                + String.valueOf(httpResult.length()));
                // Log.e(TAG, httpResult);

            } catch (Exception e) {
                Log.e(TAG, "Friends Timeline Thread Network Problem");
                e.printStackTrace();
            }
        }

        try {

            FriendsTimelineBean timeline = new Gson().fromJson(httpResult,
                    FriendsTimelineBean.class);

            ArrayList<HashMap<String, String>> text = new ArrayList<HashMap<String, String>>();

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
//                    e.printStackTrace();
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

            if (sqlHelper != null && !isProvidedResult && max_id == null) {
                SQLiteDatabase sql = sqlHelper.getWritableDatabase();

                ContentValues cv = new ContentValues();
                cv.put(sqlHelper.FRIENDS_TIMELINE, httpResult);
                if (sql.update(sqlHelper.tableName, cv, sqlHelper.UID + "='"
                        + WeiboConstant.UID + "'", null) != 0) {
                    Log.e(TAG_SQL, "Saved httpResult");
                }
            }

        } catch (Exception e) {
            mHandler.sendEmptyMessage(GOT_FRIENDS_TIMELINE_INFO_FAIL);
            Log.e(TAG, "Friends Timeline Thread FAILED");
            e.printStackTrace();
        }

    }
}
