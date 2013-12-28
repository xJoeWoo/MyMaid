package com.joewoo.ontime.action.statuses;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.R;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.FriendsTimelineBean;
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
import static com.joewoo.ontime.support.info.Defines.GOT_FRIENDS_TIMELINE_ADD_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_FRIENDS_TIMELINE_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_FRIENDS_TIMELINE_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.MAX_ID;
import static com.joewoo.ontime.support.info.Defines.TAG;

public class StatusesFriendsTimeLine extends Thread {

    private Handler mHandler;
    public boolean isProvidedResult = false;
    private String httpResult;
    private SQLiteDatabase sql;
    private String max_id = null;

    public StatusesFriendsTimeLine(String max_id, Handler handler) {
        this.mHandler = handler;
        this.max_id = max_id;
    }

    public StatusesFriendsTimeLine(boolean isProvided, SQLiteDatabase sql, Handler handler) {
        this.mHandler = handler;
        this.isProvidedResult = isProvided;
        this.sql = sql;
    }

    @Override
    public void run() {
        Log.e(TAG, "Friends TimeLine Thread START");

        try {

            if (!isProvidedResult) {
                if (!fresh())
                    return;
            } else {
                httpResult = MyMaidSQLHelper.getOneString(MyMaidSQLHelper.FRIENDS_TIMELINE, sql);

                if (httpResult == null)
                    if (!fresh())
                        return;
            }

            sql = null;

            if (ErrorCheck.getError(httpResult) == null) {

                FriendsTimelineBean f = new Gson().fromJson(httpResult,
                        FriendsTimelineBean.class);

                List<StatusesBean> statuses = f.getStatuses();

                String[] adIDs = new String[f.getAd().size()];
                for (int i = 0; i < f.getAd().size(); i++) {
                    adIDs[i] = f.getAd().get(i).getId();
                    Log.e(TAG, "Ad: " + adIDs[i]);
                }

                int index = -1;
                int adPosition = -1;

                String source;

                for (StatusesBean s : statuses) {

                    index++;

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


                    for (String adID : adIDs)
                        if (s.getId().equals(adID)) {
                            Log.e(TAG, "HAAAAAAAA! CAUGHT AN AD!!!!");
                            adPosition = index;
                        }

                }

                if (adPosition != -1)
                    statuses.remove(adPosition);


                if (max_id == null)
                    mHandler.obtainMessage(GOT_FRIENDS_TIMELINE_INFO, statuses)
                            .sendToTarget();
                else {
                    statuses.remove(0);
                    mHandler.obtainMessage(GOT_FRIENDS_TIMELINE_ADD_INFO, statuses)
                            .sendToTarget();
                }


            } else {
                mHandler.obtainMessage(GOT_FRIENDS_TIMELINE_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.obtainMessage(GOT_FRIENDS_TIMELINE_INFO_FAIL, GlobalContext.getResString(R.string.toast_user_timeline_fail)).sendToTarget();
        }

    }

    private boolean fresh() {
        try {
            HashMap<String, String> hm = new HashMap<>();
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());

            if (max_id == null) {
                hm.put(COUNT, AcquireCount.FRIENDS_TIMELINE_COUNT);
            } else {
                hm.put(COUNT, AcquireCount.FRIENDS_TIMELINE_ADD_COUNT);
                hm.put(MAX_ID, max_id);
            }

            httpResult = new HttpUtility().executeGetTask(URLHelper.FRIENDS_TIMELINE, hm);

            hm = null;

            MyMaidSQLHelper.saveOneString(MyMaidSQLHelper.FRIENDS_TIMELINE, httpResult, sql);

            return true;

        } catch (Exception e) {
            mHandler.obtainMessage(GOT_FRIENDS_TIMELINE_INFO_FAIL, GlobalContext.getResString(R.string.toast_user_timeline_fail)).sendToTarget();
            e.printStackTrace();
            return false;
        }
    }
}
