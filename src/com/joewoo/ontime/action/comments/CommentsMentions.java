package com.joewoo.ontime.action.comments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.action.remind.RemindSetCount;
import com.joewoo.ontime.support.bean.CommentsBean;
import com.joewoo.ontime.support.bean.CommentsMentionsBean;
import com.joewoo.ontime.support.info.AcquireCount;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.sql.MyMaidSQLHelper;
import com.joewoo.ontime.support.util.GlobalContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.joewoo.ontime.support.info.Defines.ACCESS_TOKEN;
import static com.joewoo.ontime.support.info.Defines.BLANK;
import static com.joewoo.ontime.support.info.Defines.COMMENTS_COUNT;
import static com.joewoo.ontime.support.info.Defines.COMMENT_ID;
import static com.joewoo.ontime.support.info.Defines.COUNT;
import static com.joewoo.ontime.support.info.Defines.CREATED_AT;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_MENTIONS_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_MENTIONS_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.HAVE_PIC;
import static com.joewoo.ontime.support.info.Defines.IS_REPOST;
import static com.joewoo.ontime.support.info.Defines.MAX_ID;
import static com.joewoo.ontime.support.info.Defines.REPOSTS_COUNT;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_BMIDDLE_PIC;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_CREATED_AT;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.SOURCE;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.TEXT;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;

/**
 * Created by JoeWoo on 13-10-26.
 * OOOOOH I HAVE GRADE 3 IN SENIOR HIGH!!!
 */
public class CommentsMentions extends Thread {
    private Handler mHandler;
    public boolean isProvidedResult = false;
    private String httpResult;
    private SQLiteDatabase sql;
    private String max_id;

    public CommentsMentions(boolean isProvided, SQLiteDatabase sql, Handler handler) {
        this.mHandler = handler;
        this.sql = sql;
        this.isProvidedResult = isProvided;
    }

    public void run() {
        Log.e(TAG, "Comments StatusesMentions Thread START");

        if (!isProvidedResult) {
            if (!fresh())
                return;
        } else {
            httpResult = MyMaidSQLHelper.getOneString(MyMaidSQLHelper.COMMENTS_MENTIONS, sql);
            if(httpResult == null)
                if(!fresh())
                    return;
        }

        try {
            Log.e(TAG, httpResult);

            List<CommentsBean> comments = new Gson().fromJson(httpResult,
                    CommentsMentionsBean.class).getComments();

            ArrayList<HashMap<String, String>> text = new ArrayList<HashMap<String, String>>();

            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(BLANK, " ");
            text.add(hm);
            hm = null;

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

            comments = null;

            if (!isProvidedResult)
                new RemindSetCount(mHandler)
                        .execute(RemindSetCount.setCommentMentionsCount);

        } catch (Exception e) {
            mHandler.sendEmptyMessage(GOT_COMMENTS_MENTIONS_INFO_FAIL);
            e.printStackTrace();
        }
    }

    private boolean fresh() {
        try {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());

            if (max_id == null) {
                hm.put(COUNT, AcquireCount.COMMENTS_MENTIONS_COUNT);
            } else {
                hm.put(COUNT, AcquireCount.COMMENTS_MENTIONS_ADD_COUNT);
                hm.put(MAX_ID, max_id);
            }

            httpResult = new HttpUtility().executeGetTask(URLHelper.COMMENTS_MENTIONS, hm);

            hm = null;

            MyMaidSQLHelper.saveOneString(MyMaidSQLHelper.COMMENTS_MENTIONS, httpResult, sql);

            return true;
        } catch (Exception e) {
            mHandler.sendEmptyMessage(GOT_COMMENTS_MENTIONS_INFO_FAIL);
            e.printStackTrace();
            return false;
        }
    }

}
