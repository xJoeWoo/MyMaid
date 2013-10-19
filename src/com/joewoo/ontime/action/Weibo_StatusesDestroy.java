package com.joewoo.ontime.action;

import static com.joewoo.ontime.info.Defines.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.joewoo.ontime.bean.WeiboBackBean;
import com.joewoo.ontime.info.WeiboConstant;
import com.joewoo.ontime.info.Weibo_URLs;

import android.os.Handler;
import android.util.Log;

public class Weibo_StatusesDestroy extends Thread {

    private Handler mHandler;
    private String weibo_id;

    public Weibo_StatusesDestroy(String weibo_id, Handler handler) {
        this.mHandler = handler;
        this.weibo_id = weibo_id;
    }

    public void run() {
        Log.e(TAG, "Statuses Destroy Thread START");
        String httpResult = "{ \"error_code\" : \"233\" }";

        HttpPost httpRequest = new HttpPost(Weibo_URLs.STATUSES_DESTROY);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(ACCESS_TOKEN,
                WeiboConstant.ACCESS_TOKEN));
        params.add(new BasicNameValuePair("id", weibo_id));

        try {
            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            httpResult = EntityUtils.toString(new DefaultHttpClient()
                    .execute(httpRequest).getEntity());

            Log.e(TAG, "GOT: " + httpResult);

            mHandler.obtainMessage(GOT_STATUSES_DESTROY_INFO, new Gson().fromJson(httpResult, WeiboBackBean.class)).sendToTarget();

        } catch (Exception e) {
            mHandler.sendEmptyMessage(GOT_STATUSES_DESTROY_INFO_FAIL);
            e.printStackTrace();
        }
    }
}
