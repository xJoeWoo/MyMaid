package com.joewoo.ontime.action.statuses;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.R;
import com.joewoo.ontime.action.MyMaidActionHelper;
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

import java.util.HashMap;
import java.util.List;

import static com.joewoo.ontime.support.info.Defines.ACCESS_TOKEN;
import static com.joewoo.ontime.support.info.Defines.COUNT;
import static com.joewoo.ontime.support.info.Defines.GOT_MENTIONS_ADD_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_MENTIONS_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_MENTIONS_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.MAX_ID;
import static com.joewoo.ontime.support.info.Defines.TAG;

public class StatusesMentions extends Thread {

    private Handler mHandler;
    public boolean isProvidedResult = false;
    private String httpResult;
    private String maxID = null;

    public StatusesMentions(boolean isProvided, Handler handler) {
        this.mHandler = handler;
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
            httpResult = MyMaidSQLHelper.getOneString(MyMaidSQLHelper.MENTIONS);
            if (httpResult == null)
                if (!fresh())
                    return;
        }

        if (ErrorCheck.getError(httpResult) == null) {

            List<StatusesBean> statuses = new Gson().fromJson(httpResult,
                    MentionsBean.class).getStatuses();

            String source;

            for (StatusesBean s : statuses) {

                s.setCreatedAt(TimeFormat.parse(s.getCreatedAt()));

                source = s.getSource();
                source = source.substring(source.indexOf(">") + 1,
                        source.indexOf("</a>"));
                s.setSource(source);

                if (s.getRetweetedStatus() != null && s.getRetweetedStatus().getUser() != null) {

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
                MyMaidActionHelper.remindSetCount(RemindSetCount.MENTIONS_COUNT);

        } else {
            mHandler.obtainMessage(GOT_MENTIONS_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();
        }
    }

    private boolean fresh() {
        try {
            HashMap<String, String> hm = new HashMap<>();
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());

            if (maxID == null) {
                hm.put(COUNT, AcquireCount.MENTIONS_COUNT);
            } else {
                hm.put(COUNT, AcquireCount.MENTIONS_ADD_COUNT);
                hm.put(MAX_ID, maxID);
            }

            httpResult = new HttpUtility().executeGetTask(URLHelper.MENTIONS, hm);

            hm = null;

            MyMaidSQLHelper.saveOneString(MyMaidSQLHelper.MENTIONS, httpResult);

            return true;

        } catch (Exception e) {
            mHandler.obtainMessage(GOT_MENTIONS_INFO_FAIL, GlobalContext.getResString(R.string.toast_mentions_fail)).sendToTarget();
            e.printStackTrace();
            return false;
        }
    }
}
