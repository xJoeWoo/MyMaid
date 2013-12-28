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

import java.util.HashMap;
import java.util.List;

import static com.joewoo.ontime.support.info.Defines.ACCESS_TOKEN;
import static com.joewoo.ontime.support.info.Defines.COUNT;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_TO_ME_ADD_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_TO_ME_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_TO_ME_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.MAX_ID;
import static com.joewoo.ontime.support.info.Defines.TAG;

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

            String source;

            for (CommentsBean c : comments) {

                c.setCreatedAt(TimeFormat.parse(c.getCreatedAt()));

                source = c.getSource();
                source = source.substring(source.indexOf(">") + 1,
                        source.indexOf("</a>"));
                c.setSource(source);
            }

            if (maxID == null)
                mHandler.obtainMessage(GOT_COMMENTS_TO_ME_INFO, comments).sendToTarget();
            else {
                comments.remove(0);
                mHandler.obtainMessage(GOT_COMMENTS_TO_ME_ADD_INFO, comments).sendToTarget();
            }

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
