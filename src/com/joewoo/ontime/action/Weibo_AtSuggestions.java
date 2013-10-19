package com.joewoo.ontime.action;

import static com.joewoo.ontime.info.Defines.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.joewoo.ontime.R;
import com.joewoo.ontime.bean.AtSuggestionBean;
import com.joewoo.ontime.info.WeiboConstant;
import com.joewoo.ontime.info.Weibo_URLs;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;

public class Weibo_AtSuggestions extends Thread {

    private String user;
    private Handler mHandler;
    private Context context;

    public Weibo_AtSuggestions(String user, Context context, Handler handler) {
        this.user = user;
        this.context = context;
        this.mHandler = handler;
    }

    public void run() {
        Log.e(TAG, "At User Suggestions Thread START");
        String httpResult = "{ \"error_code\" : \"233\" }";

        HttpGet httpGet = new HttpGet(Weibo_URLs.AT_SUGGESTIONS + "?access_token="
                + WeiboConstant.ACCESS_TOKEN + "&q=" + user + "&type=0");

        try {

            httpResult = EntityUtils.toString(new DefaultHttpClient().execute(
                    httpGet).getEntity());

            Log.e(TAG, "GOT: " + httpResult);

            Type listType = new TypeToken<List<AtSuggestionBean>>() {
            }.getType();

            List<AtSuggestionBean> events = new Gson().fromJson(httpResult,
                    listType);

            ArrayAdapter<AtSuggestionBean> files = new ArrayAdapter<AtSuggestionBean>(
                    context, R.layout.at_lv, R.id.lv_tv1, events);

            mHandler.obtainMessage(GOT_AT_SUGGESTIONS_INFO, files)
                    .sendToTarget();


        } catch (Exception e) {
            Log.e(TAG, "At User Suggesstions FAILED");
            e.printStackTrace();
        }

    }

}
