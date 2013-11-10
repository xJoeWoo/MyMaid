package com.joewoo.ontime.action;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.bean.MentionsBean;
import com.joewoo.ontime.bean.StatusesBean;
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

import static com.joewoo.ontime.info.Constants.BMIDDLE_PIC;
import static com.joewoo.ontime.info.Constants.COMMENTS_COUNT;
import static com.joewoo.ontime.info.Constants.CREATED_AT;
import static com.joewoo.ontime.info.Constants.GOT_MENTIONS_INFO;
import static com.joewoo.ontime.info.Constants.GOT_MENTIONS_INFO_FAIL;
import static com.joewoo.ontime.info.Constants.HAVE_PIC;
import static com.joewoo.ontime.info.Constants.IS_REPOST;
import static com.joewoo.ontime.info.Constants.REPOSTS_COUNT;
import static com.joewoo.ontime.info.Constants.RETWEETED_STATUS;
import static com.joewoo.ontime.info.Constants.RETWEETED_STATUS_BMIDDLE_PIC;
import static com.joewoo.ontime.info.Constants.RETWEETED_STATUS_COMMENTS_COUNT;
import static com.joewoo.ontime.info.Constants.RETWEETED_STATUS_REPOSTS_COUNT;
import static com.joewoo.ontime.info.Constants.RETWEETED_STATUS_SCREEN_NAME;
import static com.joewoo.ontime.info.Constants.RETWEETED_STATUS_UID;
import static com.joewoo.ontime.info.Constants.SCREEN_NAME;
import static com.joewoo.ontime.info.Constants.SOURCE;
import static com.joewoo.ontime.info.Constants.TAG;
import static com.joewoo.ontime.info.Constants.TEXT;
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
                e.printStackTrace();
            }
        }

        try {

            MentionsBean mentions = new Gson().fromJson(httpResult,
                    MentionsBean.class);

            List<StatusesBean> statuses = mentions.getStatuses();
            ArrayList<HashMap<String, String>> text = new ArrayList<HashMap<String, String>>();

            String source;

            for (StatusesBean b : statuses) {
                HashMap<String, String> map = new HashMap<String, String>();
                source = b.getSource();
                source = source.substring(source.indexOf(">") + 1,
                        source.length());
                source = source.substring(0, source.indexOf("<"));
                map.put(SOURCE, " · " + source);
                source = b.getCreatedAt();
                source = source.substring(source.indexOf(":") - 2,
                        source.indexOf(":") + 3);
                map.put(CREATED_AT, source);
                map.put(SCREEN_NAME, b.getUser().getScreenName());
                map.put(TEXT, b.getText());
                map.put(WEIBO_ID, b.getId());
                map.put(COMMENTS_COUNT, b.getCommentsCount());
                map.put(REPOSTS_COUNT, b.getRepostsCount());
                map.put(UID, b.getUser().getId());

                try {
                    map.put(RETWEETED_STATUS_UID, b
                            .getRetweetedStatus().getUser().getId());
                    map.put(RETWEETED_STATUS_SCREEN_NAME, b
                            .getRetweetedStatus().getUser().getScreenName());
                    map.put(RETWEETED_STATUS, b
                            .getRetweetedStatus().getText());
                    map.put(RETWEETED_STATUS_COMMENTS_COUNT, b
                            .getRetweetedStatus().getCommentsCount());
                    map.put(RETWEETED_STATUS_REPOSTS_COUNT, b
                            .getRetweetedStatus().getRepostsCount());

                    if (b.getRetweetedStatus().getThumbnailPic() != null) {
                        map.put(HAVE_PIC, " ");
                        map.put(RETWEETED_STATUS_BMIDDLE_PIC, b
                                .getRetweetedStatus().getBmiddlePic());
                    }
                    map.put(IS_REPOST, " ");

                } catch (Exception e) {
//                    e.printStackTrace();
                }

                if (b.getThumbnailPic() != null) {
                    map.put(HAVE_PIC, " ");
                    map.put(BMIDDLE_PIC, b.getBmiddlePic());
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

        } catch (Exception e) {
            mHandler.sendEmptyMessage(GOT_MENTIONS_INFO_FAIL);
            Log.e(TAG, "Mentions Thread FAILED");
            e.printStackTrace();
        }
    }

}
