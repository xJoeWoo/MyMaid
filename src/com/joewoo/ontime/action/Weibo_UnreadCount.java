package com.joewoo.ontime.action;

import static com.joewoo.ontime.info.Defines.*;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.joewoo.ontime.bean.UnreadCountBean;
import com.joewoo.ontime.info.WeiboConstant;
import com.joewoo.ontime.info.Weibo_URLs;

import android.os.Handler;
import android.util.Log;

public class Weibo_UnreadCount extends Thread {

    private Handler mHandler;
    private String httpResult = "{ \"error_code\":\"233\"";

    public Weibo_UnreadCount(Handler handler) {
        this.mHandler = handler;
    }

    public void run() {
        Log.e(TAG, "Unread Count Thread START");

        HttpGet httpGet = new HttpGet(Weibo_URLs.UNREAD_COUNT + "?access_token="
                + WeiboConstant.ACCESS_TOKEN + "&uid=" + WeiboConstant.UID);

        try {
            httpResult = EntityUtils.toString(new DefaultHttpClient().execute(
                    httpGet).getEntity());

            Log.e(TAG, "GOT: " + httpResult);

            UnreadCountBean b = new Gson().fromJson(httpResult,
                    UnreadCountBean.class);

            if (b.getCmtCount() != null)
                mHandler.obtainMessage(GOT_UNREAD_COUNT_INFO, b).sendToTarget();
            else
                mHandler.sendEmptyMessage(GOT_UNREAD_COUNT_INFO_FAIL);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
