package com.joewoo.ontime.action;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.joewoo.ontime.bean.WeiboBackBean;
import com.joewoo.ontime.info.WeiboConstant;
import com.joewoo.ontime.info.Weibo_URLs;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

import static com.joewoo.ontime.info.Defines.ACCESS_TOKEN;
import static com.joewoo.ontime.info.Defines.GOT_UPDATE_INFO;
import static com.joewoo.ontime.info.Defines.STATUS;
import static com.joewoo.ontime.info.Defines.TAG;

public class Weibo_Update extends AsyncTask<String, Integer, String> {

    private String status;
    private Handler mHandler;
//	private ProgressBar pb;

    public Weibo_Update(String status, ProgressBar pb, Handler handler) {
        this.status = status;
        this.mHandler = handler;
//		this.pb = pb;
    }

    public Weibo_Update(String status, Handler handler) {
        this.status = status;
        this.mHandler = handler;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... params) {
        Log.e(TAG, "Update Weibo Thread START");
        String httpResult = "{ \"error_code\":\"233\",\"error\":\"新浪抽风了，没有信息返回\"}";

        HttpPost httpRequest = new HttpPost(Weibo_URLs.UPDATE);
        List<NameValuePair> params1 = new ArrayList<NameValuePair>();
        params1.add(new BasicNameValuePair(ACCESS_TOKEN, WeiboConstant.ACCESS_TOKEN));
        params1.add(new BasicNameValuePair(STATUS, status));

        try {
            httpRequest.setEntity(new UrlEncodedFormEntity(params1, HTTP.UTF_8));
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                httpResult = EntityUtils.toString(httpResponse.getEntity());
                Log.e(TAG, httpResult);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return httpResult;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
//		pb.setProgress((int) (progress[0]));
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
