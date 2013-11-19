package com.joewoo.ontime.action;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.bean.ErrorBean;
import com.joewoo.ontime.bean.MentionsBean;
import com.joewoo.ontime.bean.StatusesBean;
import com.joewoo.ontime.info.Weibo_Constants;
import com.joewoo.ontime.info.Weibo_URLs;
import com.joewoo.ontime.tools.MyMaidSQLHelper;
import com.joewoo.ontime.tools.Weibo_Errors;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static com.joewoo.ontime.info.Constants.BLANK;
import static com.joewoo.ontime.info.Constants.BMIDDLE_PIC;
import static com.joewoo.ontime.info.Constants.COMMENTS_COUNT;
import static com.joewoo.ontime.info.Constants.CREATED_AT;
import static com.joewoo.ontime.info.Constants.GOT_MENTIONS_INFO;
import static com.joewoo.ontime.info.Constants.GOT_MENTIONS_INFO_FAIL;
import static com.joewoo.ontime.info.Constants.HAVE_PIC;
import static com.joewoo.ontime.info.Constants.IS_REPOST;
import static com.joewoo.ontime.info.Constants.PROFILE_IMAGE_URL;
import static com.joewoo.ontime.info.Constants.REPOSTS_COUNT;
import static com.joewoo.ontime.info.Constants.RETWEETED_STATUS;
import static com.joewoo.ontime.info.Constants.RETWEETED_STATUS_BMIDDLE_PIC;
import static com.joewoo.ontime.info.Constants.RETWEETED_STATUS_COMMENTS_COUNT;
import static com.joewoo.ontime.info.Constants.RETWEETED_STATUS_CREATED_AT;
import static com.joewoo.ontime.info.Constants.RETWEETED_STATUS_REPOSTS_COUNT;
import static com.joewoo.ontime.info.Constants.RETWEETED_STATUS_SCREEN_NAME;
import static com.joewoo.ontime.info.Constants.RETWEETED_STATUS_SOURCE;
import static com.joewoo.ontime.info.Constants.RETWEETED_STATUS_THUMBNAIL_PIC;
import static com.joewoo.ontime.info.Constants.RETWEETED_STATUS_UID;
import static com.joewoo.ontime.info.Constants.SCREEN_NAME;
import static com.joewoo.ontime.info.Constants.SOURCE;
import static com.joewoo.ontime.info.Constants.TAG;
import static com.joewoo.ontime.info.Constants.TEXT;
import static com.joewoo.ontime.info.Constants.THUMBNAIL_PIC;
import static com.joewoo.ontime.info.Constants.UID;
import static com.joewoo.ontime.info.Constants.WEIBO_ID;

public class Weibo_Mentions extends Thread {

    private String count;
    private Handler mHandler;
    public boolean isProvidedResult = false;
    private String httpResult = "{ \"error_code\" : \"233\" }";
    private SQLiteDatabase sql;

    public Weibo_Mentions(int count, Handler handler) {
        this.count = String.valueOf(count);
        this.mHandler = handler;
    }

    public Weibo_Mentions(int count, SQLiteDatabase sql, Handler handler) {
        this.count = String.valueOf(count);
        this.mHandler = handler;
        this.sql = sql;
    }

    public Weibo_Mentions(String httpResult, Handler handler) {
        this.mHandler = handler;
        this.httpResult = httpResult;
        isProvidedResult = true;
    }

    @Override
    public void run() {
        Log.e(TAG, "Mentions Thread START");

        if (!isProvidedResult) {

            HttpUriRequest httpGet = new HttpGet(Weibo_URLs.MENTIONS
                    + "?access_token=" + Weibo_Constants.ACCESS_TOKEN + "&count="
                    + count);
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
                is.close();
                baos.close();

                Log.e(TAG,
                        "GOT Statues length: "
                                + String.valueOf(httpResult.length()));
                Log.e(TAG, httpResult);

            } catch (Exception e) {
                mHandler.sendEmptyMessage(GOT_MENTIONS_INFO_FAIL);
                e.printStackTrace();
                return;
            }
        }

        ErrorBean err = Weibo_Errors.getErrorBean(httpResult);

        if (err == null) {

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

            if (sql != null && !isProvidedResult) {
                ContentValues cv = new ContentValues();
                cv.put(MyMaidSQLHelper.MENTIONS, httpResult);
                if (sql.update(MyMaidSQLHelper.tableName, cv, MyMaidSQLHelper.UID + "='"
                        + Weibo_Constants.UID + "'", null) != 0) {
                    Log.e(MyMaidSQLHelper.TAG_SQL, "Saved Mentions httpResult");
                }
            }
        } else {
            mHandler.obtainMessage(GOT_MENTIONS_INFO_FAIL, err).sendToTarget();
        }


    }
}
