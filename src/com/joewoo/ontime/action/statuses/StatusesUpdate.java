package com.joewoo.ontime.action.statuses;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.WeiboBackBean;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.util.GlobalContext;

import java.util.HashMap;

import static com.joewoo.ontime.support.info.Defines.ACCESS_TOKEN;
import static com.joewoo.ontime.support.info.Defines.GOT_UPDATE_INFO;
import static com.joewoo.ontime.support.info.Defines.STATUS;
import static com.joewoo.ontime.support.info.Defines.TAG;

public class StatusesUpdate extends AsyncTask<String, Integer, String> {

    private String status;
    private Handler mHandler;

    public StatusesUpdate(String status, Handler handler) {
        this.status = status;
        this.mHandler = handler;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... params) {
        Log.e(TAG, "StatusesUpdate Weibo Thread START");
        String httpResult = null;

        try {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());
            hm.put(STATUS, status);

            httpResult = new HttpUtility().executePostTask(URLHelper.UPDATE, hm);

            hm = null;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return httpResult;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {

    }

    @Override
    protected void onPostExecute(String result) {
        Log.e(TAG, result);
        Gson gson = new Gson();
        WeiboBackBean j = gson.fromJson(result, WeiboBackBean.class);

        if (mHandler != null)
            mHandler.obtainMessage(GOT_UPDATE_INFO, j).sendToTarget();
    }

    @Override
    protected void onCancelled() {
    }

}
