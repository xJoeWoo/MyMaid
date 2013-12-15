package com.joewoo.ontime.action.comments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.R;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.action.remind.RemindSetCount;
import com.joewoo.ontime.support.bean.CommentsBean;
import com.joewoo.ontime.support.bean.CommentsToMeBean;
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
import static com.joewoo.ontime.support.info.Defines.COMMENT_ID;
import static com.joewoo.ontime.support.info.Defines.COUNT;
import static com.joewoo.ontime.support.info.Defines.CREATED_AT;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_TO_ME_ADD_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_TO_ME_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_TO_ME_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.IS_COMMENT;
import static com.joewoo.ontime.support.info.Defines.IS_REPOST;
import static com.joewoo.ontime.support.info.Defines.MAX_ID;
import static com.joewoo.ontime.support.info.Defines.PIC_URLS;
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
    private String maxID = null;

    public CommentsToMe(boolean isProvided, SQLiteDatabase sql, Handler handler) {
        this.mHandler = handler;
        this.sql = sql;
        this.isProvidedResult = isProvided;
    }

    public CommentsToMe(String maxID, Handler handler) {
        this.maxID = maxID;
        this.mHandler = handler;
    }

    @Override
    public void run() {
        Log.e(TAG, "Comments To Me Thread START");

        if (!isProvidedResult) {
            if (!fresh())
                return;
        } else {
            httpResult = MyMaidSQLHelper.getOneString(MyMaidSQLHelper.COMMENTS_TO_ME, sql);
            if (httpResult == null)
                if (!fresh())
                    return;
        }

        sql = null;

        if (ErrorCheck.getError(httpResult) == null) {
            List<CommentsBean> comments = new Gson().fromJson(httpResult,
                    CommentsToMeBean.class).getComments();

            ArrayList<HashMap<String, String>> text = new ArrayList<HashMap<String, String>>();

//            HashMap<String, String> hm = new HashMap<String, String>();
//            hm.put(BLANK, " ");
//            text.add(hm);
//            hm = null;

            String source;

            for (CommentsBean c : comments) {
                HashMap<String, String> map = new HashMap<String, String>();

                source = c.getSource();
                map.put(SOURCE, " · " + source.substring(source.indexOf(">") + 1,
                        source.indexOf("</a>")));
                map.put(CREATED_AT, TimeFormat.parse(c.getCreatedAt()));
                // map.put(UID, c.getUser().getId());
                map.put(SCREEN_NAME, c.getUser().getScreenName());
                map.put(TEXT, c.getText());
                map.put(COMMENT_ID, c.getId());

                map.put(WEIBO_ID, c.getStatus().getId());
                map.put(STATUS_TEXT, c.getStatus().getText());
                map.put(STATUS_USER_SCREEN_NAME, c.getStatus().getUser().getScreenName());
                map.put(STATUS_COMMENTS_COUNT, c.getStatus().getCommentsCount());
                map.put(STATUS_REPOSTS_COUNT, c.getStatus().getRepostsCount());
                map.put(STATUS_CREATED_AT, TimeFormat.parse(c.getStatus().getCreatedAt()));

                if (c.getStatus().getPicURLs() != null && c.getStatus().getPicURLs().size() > 1)
                    map.put(PIC_URLS, " ");

                source = c.getStatus().getSource();
                map.put(STATUS_SOURCE, " · " + source.substring(source.indexOf(">") + 1,
                        source.indexOf("</a>")));

                map.put(STATUS_PROFILE_IMAGE_URL, c.getStatus().getUser().getProfileImageUrl());

                try {
                    source = c.getStatus().getRetweetedStatus()
                            .getSource();

                    map.put(RETWEETED_STATUS_SOURCE, " · " + source.substring(source.indexOf(">") + 1,
                            source.indexOf("</a>")));
                    map.put(RETWEETED_STATUS_CREATED_AT, TimeFormat.parse(c.getStatus().getRetweetedStatus().getCreatedAt()));

                    map.put(RETWEETED_STATUS_SCREEN_NAME, c.getStatus()
                            .getRetweetedStatus().getUser().getScreenName());
                    map.put(RETWEETED_STATUS, c.getStatus()
                            .getRetweetedStatus().getText());

                    if (c.getStatus().getRetweetedStatus().getPicURLs() != null && c.getStatus().getRetweetedStatus().getPicURLs().size() > 1)
                        map.put(PIC_URLS, " ");

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

                text.add(map);
            }

            if (maxID == null)
                mHandler.obtainMessage(GOT_COMMENTS_TO_ME_INFO, text).sendToTarget();
            else
                mHandler.obtainMessage(GOT_COMMENTS_TO_ME_ADD_INFO, text).sendToTarget();

            if (!isProvidedResult && maxID == null)
                new RemindSetCount(RemindSetCount.CommentsCount).start();

        } else {
            mHandler.obtainMessage(GOT_COMMENTS_TO_ME_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();
        }
    }

    private boolean fresh() {
        try {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());

            if (maxID == null)
                hm.put(COUNT, AcquireCount.COMMENTS_TO_ME_COUNT);
            else {
                hm.put(COUNT, AcquireCount.COMMENTS_TO_ME_ADD_COUNT);
                hm.put(MAX_ID, maxID);
            }

            httpResult = new HttpUtility().executeGetTask(URLHelper.COMMENTS_TO_ME, hm);

            hm = null;

            MyMaidSQLHelper.saveOneString(MyMaidSQLHelper.COMMENTS_TO_ME, httpResult, sql);

            return true;
        } catch (Exception e) {
            mHandler.obtainMessage(GOT_COMMENTS_TO_ME_INFO_FAIL, GlobalContext.getAppContext().getString(R.string.toast_comments_fail)).sendToTarget();
            e.printStackTrace();
            return false;
        }
    }
}
