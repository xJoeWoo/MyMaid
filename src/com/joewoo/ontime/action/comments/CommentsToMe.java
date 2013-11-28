package com.joewoo.ontime.action.comments;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.action.remind.RemindSetCount;
import com.joewoo.ontime.support.bean.CommentsBean;
import com.joewoo.ontime.support.bean.CommentsToMeBean;
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
import static com.joewoo.ontime.support.info.Defines.COMMENT_ID;
import static com.joewoo.ontime.support.info.Defines.COUNT;
import static com.joewoo.ontime.support.info.Defines.CREATED_AT;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_TO_ME_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_TO_ME_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.IS_COMMENT;
import static com.joewoo.ontime.support.info.Defines.IS_REPOST;
import static com.joewoo.ontime.support.info.Defines.REPLY_COMMNET_TEXT;
import static com.joewoo.ontime.support.info.Defines.REPLY_COMMNET_USER_SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_BMIDDLE_PIC;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_CREATED_AT;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_SOURCE;
import static com.joewoo.ontime.support.info.Defines.SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.SOURCE;
import static com.joewoo.ontime.support.info.Defines.STATUS_BMIDDLE_PIC;
import static com.joewoo.ontime.support.info.Defines.STATUS_COMMENTS_COUNT;
import static com.joewoo.ontime.support.info.Defines.STATUS_CREATED_AT;
import static com.joewoo.ontime.support.info.Defines.STATUS_PROFILE_IMAGE_URL;
import static com.joewoo.ontime.support.info.Defines.STATUS_REPOSTS_COUNT;
import static com.joewoo.ontime.support.info.Defines.STATUS_SOURCE;
import static com.joewoo.ontime.support.info.Defines.STATUS_TEXT;
import static com.joewoo.ontime.support.info.Defines.STATUS_USER_SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.TEXT;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;

public class CommentsToMe extends Thread {

    private Handler mHandler;
    public boolean isProvidedResult = false;
    private String httpResult;
    private SQLiteDatabase sql;
//
//    public CommentsToMe(SQLiteDatabase sql, Handler handler) {
//        this.mHandler = handler;
//        this.sql = sql;
//    }

    public CommentsToMe(boolean isProvided, SQLiteDatabase sql, Handler handler) {
        this.mHandler = handler;
        this.sql = sql;
        this.isProvidedResult = isProvided;
    }

    @Override
    public void run() {
        Log.e(TAG, "Comments To Me Thread START");

        if (!isProvidedResult) {
            if (!fresh())
                return;
        } else {
            httpResult = MyMaidSQLHelper.getOneString(MyMaidSQLHelper.COMMENTS_TO_ME, sql);
            if(httpResult == null)
                if(!fresh())
                    return;
        }

        sql = null;

        if (ErrorCheck.getError(httpResult) == null) {
            List<CommentsBean> comments = new Gson().fromJson(httpResult,
                    CommentsToMeBean.class).getComments();

            ArrayList<HashMap<String, String>> text = new ArrayList<HashMap<String, String>>();

            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(BLANK, " ");
            text.add(hm);
            hm = null;

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
                map.put(STATUS_SOURCE, " · " + source);

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

                if (c.getStatus().getBmiddlePic() != null) {
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

            if(!isProvidedResult)
                new RemindSetCount(mHandler)
                        .execute(RemindSetCount.setCommentsCount);

        } else {
            mHandler.obtainMessage(GOT_COMMENTS_TO_ME_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();
        }
    }

    private boolean fresh() {
        try {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());
            hm.put(COUNT, AcquireCount.COMMENTS_TO_ME_COUNT);

            httpResult = new HttpUtility().executeGetTask(URLHelper.COMMENTS_TO_ME, hm);

            hm = null;

            MyMaidSQLHelper.saveOneString(MyMaidSQLHelper.COMMENTS_TO_ME, httpResult, sql);

            return true;
        } catch (Exception e) {
            mHandler.sendEmptyMessage(GOT_COMMENTS_TO_ME_INFO_FAIL);
            e.printStackTrace();
            return false;
        }
    }
}
