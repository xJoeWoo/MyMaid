package com.joewoo.ontime.action.remind;

import static com.joewoo.ontime.support.info.Defines.*;

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
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.WeiboBackBean;
import com.joewoo.ontime.support.info.Constants;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class RemindSetCount extends AsyncTask<String, Integer, String> {

    private Handler mHandler;

    public final static String setCommentsCount = "cmt";
    public final static String setMentionsCount = "mention_status";
    public final static String setCommentMentionsCount = "mention_cmt";
    public final static String setFollowersCount = "follower";

    public RemindSetCount(Handler handler) {
        this.mHandler = handler;
    }

    @Override
    protected String doInBackground(String... params) {

        Log.e(TAG, "Set Remind Count AsycnTask start");
        Log.e(TAG, "type: " + params[0]);
        String httpResult = "{ \"error_code\" : \"233\" }";

        HttpPost httpRequest = new HttpPost(URLHelper.SET_REMIND_COUNT);
        List<NameValuePair> p = new ArrayList<NameValuePair>();
        p.add(new BasicNameValuePair(ACCESS_TOKEN, Constants.ACCESS_TOKEN));
        p.add(new BasicNameValuePair("type", params[0]));

        try {
            httpRequest.setEntity(new UrlEncodedFormEntity(p, HTTP.UTF_8));
            httpResult = EntityUtils.toString(new DefaultHttpClient().execute(
                    httpRequest).getEntity());

            Log.e(TAG, "GOT: " + httpResult);


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
        WeiboBackBean b = new Gson().fromJson(result, WeiboBackBean.class);

        if (b.getSetRemindCountResult() == null) {
            mHandler.sendEmptyMessage(GOT_SET_REMIND_COUNT_INFO_FAIL);
        }
    }

}
