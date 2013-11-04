package com.joewoo.ontime.action;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.bean.CommentsBean;
import com.joewoo.ontime.bean.CommentsMentionsBean;
import com.joewoo.ontime.info.WeiboConstant;
import com.joewoo.ontime.info.Weibo_URLs;
import com.joewoo.ontime.tools.MySQLHelper;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static com.joewoo.ontime.info.Defines.BMIDDLE_PIC;
import static com.joewoo.ontime.info.Defines.COMMENTS_COUNT;
import static com.joewoo.ontime.info.Defines.COMMENT_ID;
import static com.joewoo.ontime.info.Defines.CREATED_AT;
import static com.joewoo.ontime.info.Defines.GOT_COMMENTS_MENTIONS_INFO;
import static com.joewoo.ontime.info.Defines.GOT_COMMENTS_MENTIONS_INFO_FAIL;
import static com.joewoo.ontime.info.Defines.HAVE_PIC;
import static com.joewoo.ontime.info.Defines.IS_REPOST;
import static com.joewoo.ontime.info.Defines.PROFILE_IMAGE_URL;
import static com.joewoo.ontime.info.Defines.REPOSTS_COUNT;
import static com.joewoo.ontime.info.Defines.RETWEETED_STATUS;
import static com.joewoo.ontime.info.Defines.RETWEETED_STATUS_BMIDDLE_PIC;
import static com.joewoo.ontime.info.Defines.RETWEETED_STATUS_COMMENTS_COUNT;
import static com.joewoo.ontime.info.Defines.RETWEETED_STATUS_CREATED_AT;
import static com.joewoo.ontime.info.Defines.RETWEETED_STATUS_REPOSTS_COUNT;
import static com.joewoo.ontime.info.Defines.RETWEETED_STATUS_SCREEN_NAME;
import static com.joewoo.ontime.info.Defines.RETWEETED_STATUS_UID;
import static com.joewoo.ontime.info.Defines.SCREEN_NAME;
import static com.joewoo.ontime.info.Defines.SOURCE;
import static com.joewoo.ontime.info.Defines.STATUS_TEXT;
import static com.joewoo.ontime.info.Defines.STATUS_USER_SCREEN_NAME;
import static com.joewoo.ontime.info.Defines.TAG;
import static com.joewoo.ontime.info.Defines.TAG_SQL;
import static com.joewoo.ontime.info.Defines.TEXT;
import static com.joewoo.ontime.info.Defines.WEIBO_ID;

/**
 * Created by JoeWoo on 13-10-26.
 * OOOOOH I HAVE GRADE 3 IN SENIOR HIGH!!!
 */
public class Weibo_CommentsMentions extends Thread {

    private String count;
    private Handler mHandler;
    public boolean isProvidedResult = false;
    private String httpResult = "{ \"error_code\" : \"233\" }";
    private MySQLHelper sqlHelper;
    private String max_id;

    public Weibo_CommentsMentions(int count, Handler handler) {
        this.count = String.valueOf(count);
        this.mHandler = handler;
    }

    public Weibo_CommentsMentions(int count, MySQLHelper sqlHelper, Handler handler) {
        this.count = String.valueOf(count);
        this.mHandler = handler;
        this.sqlHelper = sqlHelper;
    }

    public Weibo_CommentsMentions(String httpResult, Handler handler) {
        this.mHandler = handler;
        this.httpResult = httpResult;
        isProvidedResult = true;
    }

    public void run(){
        Log.e(TAG, "Comments Mentions Thread START");

        if (!isProvidedResult) {

            HttpUriRequest httpGet;
            if (max_id == null) {

                httpGet = new HttpGet(Weibo_URLs.COMMENTS_MENTIONS + "?access_token="
                        + WeiboConstant.ACCESS_TOKEN + "&count="
                        + count);
            } else {
                httpGet = new HttpGet(Weibo_URLs.COMMENTS_MENTIONS + "?access_token="
                        + WeiboConstant.ACCESS_TOKEN + "&max_id=" + max_id + "&count="
                        + count);
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
                Log.e(TAG, httpResult);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

            try {
                Log.e(TAG, httpResult);

                List<CommentsBean> comments = new Gson().fromJson(httpResult,
                        CommentsMentionsBean.class).getComments();

                ArrayList<HashMap<String, String>> text = new ArrayList<HashMap<String, String>>();

                String source;
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

                    map.put(COMMENTS_COUNT, c.getStatus().getCommentsCount());
                    map.put(REPOSTS_COUNT, c.getStatus().getRepostsCount());
                    map.put(RETWEETED_STATUS_SCREEN_NAME, c.getStatus().getUser().getScreenName());
                    map.put(RETWEETED_STATUS, c.getStatus().getText());
                    map.put(RETWEETED_STATUS_CREATED_AT, c.getStatus().getCreatedAt());

                    map.put(IS_REPOST, " ");

                        if (c.getStatus().getThumbnailPic() != null) {
                            map.put(HAVE_PIC, " ");
                            map.put(RETWEETED_STATUS_BMIDDLE_PIC, c.getStatus().getBmiddlePic());
                        }

                    text.add(map);
                }

                mHandler.obtainMessage(GOT_COMMENTS_MENTIONS_INFO, text)
                        .sendToTarget();

                if (sqlHelper != null && !isProvidedResult) {
                    SQLiteDatabase sql = sqlHelper.getWritableDatabase();

                    ContentValues cv = new ContentValues();
                    cv.put(sqlHelper.COMMENTS_MENTIONS, httpResult);
                    if (sql.update(sqlHelper.tableName, cv, sqlHelper.UID + "='"
                            + WeiboConstant.UID + "'", null) != 0) {
                        Log.e(TAG_SQL, "Saved Comments Mentions httpResult");
                    }
                }

            } catch (Exception e) {
                mHandler.sendEmptyMessage(GOT_COMMENTS_MENTIONS_INFO_FAIL);
                e.printStackTrace();
            }
        }


}
