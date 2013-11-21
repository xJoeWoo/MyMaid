package com.joewoo.ontime.action.auth;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.WeiboBackBean;
import com.joewoo.ontime.support.info.Constants;

import static com.joewoo.ontime.support.info.Defines.*;

import android.os.Handler;
import android.util.Log;

public class AccessToken extends Thread {

    private Handler mHandler;

    public AccessToken(Handler handler) {
        this.mHandler = handler;
    }

    public void run() {
        Log.e(TAG, "Request Access Token Thread Start");
        String httpResult = "NO_MESSAGES";
        HttpPost httpRequest = new HttpPost(URLHelper.TOKEN);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("client_id", APP_KEY));
        params.add(new BasicNameValuePair("client_secret", APP_SECRET));
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));
        params.add(new BasicNameValuePair("code", Constants.AUTH_CODE));
        params.add(new BasicNameValuePair("redirect_uri", URLHelper.CALLBACK));
        try {
            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                httpResult = EntityUtils.toString(httpResponse.getEntity());
                Log.e(TAG, "GOT: " + httpResult);

                WeiboBackBean j = new Gson()
                        .fromJson(httpResult, WeiboBackBean.class);

                httpClient.getConnectionManager().shutdown();

                mHandler.obtainMessage(GOT_ACCESS_TOKEN, j).sendToTarget();

            }
        } catch (Exception e) {
            Log.e(TAG, "Access Token FAILED");
            e.printStackTrace();
        }
    }
};
