package com.joewoo.ontime.action.statuses;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.R;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.action.remind.RemindSetCount;
import com.joewoo.ontime.support.bean.MentionsBean;
import com.joewoo.ontime.support.bean.StatusesBean;
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
import static com.joewoo.ontime.support.info.Defines.BMIDDLE_PIC;
import static com.joewoo.ontime.support.info.Defines.COMMENTS_COUNT;
import static com.joewoo.ontime.support.info.Defines.COUNT;
import static com.joewoo.ontime.support.info.Defines.CREATED_AT;
import static com.joewoo.ontime.support.info.Defines.GOT_MENTIONS_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_MENTIONS_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.IS_REPOST;
import static com.joewoo.ontime.support.info.Defines.MAX_ID;
import static com.joewoo.ontime.support.info.Defines.PIC_URLS;
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
import static com.joewoo.ontime.support.info.Defines.GOT_MENTIONS_ADD_INFO;

public class StatusesMentions extends Thread {

    private Handler mHandler;
    public boolean isProvidedResult = false;
    private String httpResult;
    private SQLiteDatabase sql;
    private String maxID = null;

    public StatusesMentions(boolean isProvided, SQLiteDatabase sql, Handler handler) {
        this.mHandler = handler;
        this.sql = sql;
        this.isProvidedResult = isProvided;
    }

    public StatusesMentions(String maxID, Handler handler) {
        this.maxID = maxID;
        this.mHandler = handler;
    }

    @Override
    public void run() {
        Log.e(TAG, "StatusesMentions Thread START");


        if (!isProvidedResult) {
            if (!fresh())
                return;
        } else {
            httpResult = MyMaidSQLHelper.getOneString(MyMaidSQLHelper.MENTIONS, sql);
            if (httpResult == null)
                if (!fresh())
                    return;
        }

        sql = null;

        if (ErrorCheck.getError(httpResult) == null) {

            List<StatusesBean> statuses = new Gson().fromJson(httpResult,
                    MentionsBean.class).getStatuses();

//            ArrayList<HashMap<String, String>> text = new ArrayList<HashMap<String, String>>();

//            HashMap<String, String> hm = new HashMap<String, String>();
//            hm.put(BLANK, " ");
//            text.add(hm);
//            hm = null;

            String source;

            for (StatusesBean s : statuses) {

                s.setCreatedAt(TimeFormat.parse(s.getCreatedAt()));

                source = s.getSource();
                source = source.substring(source.indexOf(">") + 1,
                        source.indexOf("</a>"));
                s.setSource(source);

                if (s.getRetweetedStatus() != null) {

                    s.getRetweetedStatus().setCreatedAt(TimeFormat.parse(s.getRetweetedStatus().getCreatedAt()));

                    source = s.getRetweetedStatus().getSource();
                    source = source.substring(source.indexOf(">") + 1,
                            source.indexOf("</a>"));
                    s.getRetweetedStatus().setSource(source);
                }

            }

            if (maxID == null)
                mHandler.obtainMessage(GOT_MENTIONS_INFO, statuses).sendToTarget();
            else {
                statuses.remove(0);
                mHandler.obtainMessage(GOT_MENTIONS_ADD_INFO, statuses).sendToTarget();

            }

            if (!isProvidedResult && maxID == null)
                new RemindSetCount(RemindSetCount.MentionsCount).start();

        } else {
            mHandler.obtainMessage(GOT_MENTIONS_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();
        }
    }

    private boolean fresh() {
        try {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());

            if (maxID == null) {
                hm.put(COUNT, AcquireCount.MENTIONS_COUNT);
            } else {
                hm.put(COUNT, AcquireCount.MENTIONS_ADD_COUNT);
                hm.put(MAX_ID, maxID);
            }

            httpResult = new HttpUtility().executeGetTask(URLHelper.MENTIONS, hm);

            hm = null;

            MyMaidSQLHelper.saveOneString(MyMaidSQLHelper.MENTIONS, httpResult, sql);

            return true;

        } catch (Exception e) {
            mHandler.obtainMessage(GOT_MENTIONS_INFO_FAIL, GlobalContext.getResString(R.string.toast_mentions_fail)).sendToTarget();
            e.printStackTrace();
            return false;
        }
    }
}
