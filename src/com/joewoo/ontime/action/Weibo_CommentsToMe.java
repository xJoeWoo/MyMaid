package com.joewoo.ontime.action;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.bean.CommentsBean;
import com.joewoo.ontime.bean.CommentsToMeBean;
import com.joewoo.ontime.info.Weibo_Constants;
import com.joewoo.ontime.info.Weibo_URLs;
import com.joewoo.ontime.tools.MyMaidSQLHelper;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static com.joewoo.ontime.info.Constants.COMMENT_ID;
import static com.joewoo.ontime.info.Constants.CREATED_AT;
import static com.joewoo.ontime.info.Constants.GOT_COMMENTS_TO_ME_INFO;
import static com.joewoo.ontime.info.Constants.GOT_COMMENTS_TO_ME_INFO_FAIL;
import static com.joewoo.ontime.info.Constants.IS_COMMENT;
import static com.joewoo.ontime.info.Constants.IS_REPOST;
import static com.joewoo.ontime.info.Constants.REPLY_COMMNET_TEXT;
import static com.joewoo.ontime.info.Constants.REPLY_COMMNET_USER_SCREEN_NAME;
import static com.joewoo.ontime.info.Constants.RETWEETED_STATUS;
import static com.joewoo.ontime.info.Constants.RETWEETED_STATUS_BMIDDLE_PIC;
import static com.joewoo.ontime.info.Constants.RETWEETED_STATUS_CREATED_AT;
import static com.joewoo.ontime.info.Constants.RETWEETED_STATUS_SCREEN_NAME;
import static com.joewoo.ontime.info.Constants.RETWEETED_STATUS_SOURCE;
import static com.joewoo.ontime.info.Constants.SCREEN_NAME;
import static com.joewoo.ontime.info.Constants.SOURCE;
import static com.joewoo.ontime.info.Constants.STATUS_PROFILE_IMAGE_URL;
import static com.joewoo.ontime.info.Constants.STATUS_SOURCE;
import static com.joewoo.ontime.info.Constants.STATUS_BMIDDLE_PIC;
import static com.joewoo.ontime.info.Constants.STATUS_COMMENTS_COUNT;
import static com.joewoo.ontime.info.Constants.STATUS_CREATED_AT;
import static com.joewoo.ontime.info.Constants.STATUS_REPOSTS_COUNT;
import static com.joewoo.ontime.info.Constants.STATUS_TEXT;
import static com.joewoo.ontime.info.Constants.STATUS_USER_SCREEN_NAME;
import static com.joewoo.ontime.info.Constants.TAG;
import static com.joewoo.ontime.info.Constants.TEXT;
import static com.joewoo.ontime.info.Constants.WEIBO_ID;

public class Weibo_CommentsToMe extends Thread {

    private String count;
    private Handler mHandler;
    public boolean isProvidedResult = false;
    private String httpResult = "{ \"error_code\" : \"233\" }";
    private SQLiteDatabase sql;

    public Weibo_CommentsToMe(int count, Handler handler) {
        this.count = String.valueOf(count);
        this.mHandler = handler;
    }

    public Weibo_CommentsToMe(int count, SQLiteDatabase sql, Handler handler) {
        this.count = String.valueOf(count);
        this.mHandler = handler;
        this.sql = sql;
    }

    public Weibo_CommentsToMe(String httpResult, Handler handler) {
        this.mHandler = handler;
        this.httpResult = httpResult;
        isProvidedResult = true;
    }

    @Override
    public void run() {
        Log.e(TAG, "Comments To Me Thread START");

        if (!isProvidedResult) {

            HttpUriRequest httpGet = new HttpGet(Weibo_URLs.COMMENTS_TO_ME
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
                e.printStackTrace();
            }
        }

        try {
            List<CommentsBean> comments = new Gson().fromJson(httpResult,
                    CommentsToMeBean.class).getComments();

            ArrayList<HashMap<String, String>> text = new ArrayList<HashMap<String, String>>();

            String source;
            String rt_source;

            for (CommentsBean c : comments) {
                HashMap<String, String> map = new HashMap<String, String>();

                source = c.getSource();
                source = source.substring(source.indexOf(">") + 1,
                        source.length());
                source = source.substring(0, source.indexOf("<"));
                map.put(SOURCE, " · " + source);
                source = c.getCreatedAt();
                source = source.substring(source.indexOf(":") - 2,
                        source.indexOf(":") + 3);
                map.put(CREATED_AT, source);
                // map.put(UID, c.getUser().getId());
                map.put(SCREEN_NAME, c.getUser().getScreenName());
                map.put(TEXT, c.getText());
                map.put(COMMENT_ID, c.getId());

                map.put(WEIBO_ID, c.getStatus().getId());
                map.put(STATUS_TEXT, c.getStatus().getText());
                map.put(STATUS_USER_SCREEN_NAME, c.getStatus().getUser().getScreenName());
                map.put(STATUS_COMMENTS_COUNT, c.getStatus().getCommentsCount());
                map.put(STATUS_REPOSTS_COUNT, c.getStatus().getRepostsCount());

                source = c.getStatus().getCreatedAt();
                source = source.substring(source.indexOf(":") - 2,
                        source.indexOf(":") + 3);
                map.put(STATUS_CREATED_AT, source);

                source = c.getStatus().getSource();
                source = source.substring(source.indexOf(">") + 1,
                        source.length());
                source = source.substring(0, source.indexOf("<"));
                map.put(STATUS_SOURCE , " · " + source);

                map.put(STATUS_PROFILE_IMAGE_URL, c.getStatus().getUser().getProfileImageUrl());

                try {
                    rt_source = c.getStatus().getRetweetedStatus()
                            .getSource();
                    rt_source = rt_source.substring(rt_source.indexOf(">") + 1,
                            rt_source.length());
                    rt_source = rt_source.substring(0, rt_source.indexOf("<"));
                    map.put(RETWEETED_STATUS_SOURCE, " · " + rt_source);
                    rt_source = c.getStatus().getRetweetedStatus()
                            .getCreatedAt();
                    rt_source = rt_source.substring(rt_source.indexOf(":") - 2,
                            rt_source.indexOf(":") + 3);
                    map.put(RETWEETED_STATUS_CREATED_AT, rt_source);

                    map.put(RETWEETED_STATUS_SCREEN_NAME, c.getStatus()
                            .getRetweetedStatus().getUser().getScreenName());
                    map.put(RETWEETED_STATUS, c.getStatus()
                            .getRetweetedStatus().getText());

                    if (c.getStatus().getRetweetedStatus().getThumbnailPic() != null) {
                        map.put(RETWEETED_STATUS_BMIDDLE_PIC, c.getStatus()
                                .getRetweetedStatus().getBmiddlePic());
                    }
                    map.put(IS_REPOST, " ");

                } catch (Exception e) {
//                    e.printStackTrace();
                }

                if(c.getStatus().getBmiddlePic() != null) {
                    map.put(STATUS_BMIDDLE_PIC, c.getStatus().getBmiddlePic());
                }

                if (c.getReplyComment() != null) {
                    map.put(REPLY_COMMNET_USER_SCREEN_NAME, c
                            .getReplyComment().getUser().getScreenName());
                    map.put(REPLY_COMMNET_TEXT, c.getReplyComment()
                            .getText());
                } else {
                    map.put(REPLY_COMMNET_USER_SCREEN_NAME, c
                            .getStatus().getUser().getScreenName());
                    map.put(REPLY_COMMNET_TEXT, c.getStatus().getText());
                }

                map.put(IS_COMMENT, " ");

                text.add(map);
            }

            mHandler.obtainMessage(GOT_COMMENTS_TO_ME_INFO, text)
                    .sendToTarget();

            if (sql != null && !isProvidedResult) {
                ContentValues cv = new ContentValues();
                cv.put(MyMaidSQLHelper.TO_ME_COMMENTS, httpResult);
                if (sql.update(MyMaidSQLHelper.tableName, cv, MyMaidSQLHelper.UID + "='"
                        + Weibo_Constants.UID + "'", null) != 0) {
                    Log.e(MyMaidSQLHelper.TAG_SQL, "Saved Comments httpResult");
                }
            }

        } catch (Exception e) {
            mHandler.sendEmptyMessage(GOT_COMMENTS_TO_ME_INFO_FAIL);
            Log.e(TAG, "Comments To Me Thread FAILED");
            e.printStackTrace();
        }
    }
}
