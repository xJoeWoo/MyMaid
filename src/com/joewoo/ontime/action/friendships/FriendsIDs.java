package com.joewoo.ontime.action.friendships;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.FriendsIDsBean;
import com.joewoo.ontime.support.error.ErrorCheck;
import com.joewoo.ontime.support.info.AcquireCount;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.sql.MyMaidSQLHelper;
import com.joewoo.ontime.support.util.GlobalContext;

import java.util.HashMap;

import static com.joewoo.ontime.support.info.Defines.ACCESS_TOKEN;
import static com.joewoo.ontime.support.info.Defines.COUNT;
import static com.joewoo.ontime.support.info.Defines.GOT_FRIENDS_IDS_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_FRIENDS_IDS_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.TAG;

/**
 * Created by JoeWoo on 13-11-25.
 */
public class FriendsIDs extends Thread {

    private String screenName;
    private SQLiteDatabase sql = null;
    private boolean isProvided = false;
    private String httpResult;
    private Handler mHandler;

    public FriendsIDs(boolean isProvided, String screenName, SQLiteDatabase sql, Handler handler) {
        this.screenName = screenName;
        this.sql = sql;
        this.isProvided = isProvided;
        this.mHandler = handler;
    }

    public void run() {
        Log.e(TAG, "Friends IDs Thread START");

        if (!isProvided) {
            fresh();
        } else {
            Cursor c = sql.query(MyMaidSQLHelper.tableName, new String[]{
                    MyMaidSQLHelper.FRIENDS_IDS},
                    MyMaidSQLHelper.UID + "=?", new String[]{GlobalContext.getUID()}, null,
                    null, null);
            if (c.getCount() > 0 && c.moveToFirst() && c.getString(c
                    .getColumnIndex(MyMaidSQLHelper.FRIENDS_IDS)) != null) {
                httpResult = c.getString(c
                        .getColumnIndex(MyMaidSQLHelper.FRIENDS_IDS));
            } else {
                fresh();
            }
            c = null;
        }

        if (ErrorCheck.getError(httpResult) == null) {
            FriendsIDsBean f = new Gson().fromJson(httpResult, FriendsIDsBean.class);
            long[] ids = new long[f.getIDs().size()];
            for (int i = 0; i < f.getTotalNumber(); i++) {
                ids[i] = f.getIDs().get(i);
            }
            GlobalContext.setFriendsIDs(ids);
            ids = null;
            f = null;
            mHandler.sendEmptyMessage(GOT_FRIENDS_IDS_INFO);
        }


    }

    private void fresh() {
        try {
            HashMap<String, String> hm = new HashMap<>();
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());
            hm.put(SCREEN_NAME, screenName);
            hm.put(COUNT, AcquireCount.FRIENDS_IDS_COUNT);

            httpResult = new HttpUtility().executeGetTask(URLHelper.FRIENDS_IDS, hm);

            hm = null;

            if (sql != null) {
                ContentValues cv = new ContentValues();
                cv.put(MyMaidSQLHelper.FRIENDS_IDS, httpResult);
                if (sql.update(MyMaidSQLHelper.tableName, cv, MyMaidSQLHelper.UID + "='"
                        + GlobalContext.getUID() + "'", null) != 0) {
                    Log.e(MyMaidSQLHelper.TAG_SQL, "Saved Friends IDs");
                }
                cv = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Friends IDs FAILED");
            e.printStackTrace();
            mHandler.sendEmptyMessage(GOT_FRIENDS_IDS_INFO_FAIL);
        }
    }


}
