package com.joewoo.ontime.action.statuses;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.WeiboBackBean;
import com.joewoo.ontime.support.net.CustomMultipartEntity;
import com.joewoo.ontime.support.net.CustomMultipartEntity.ProgressListener;
import com.joewoo.ontime.support.util.GlobalContext;

import static com.joewoo.ontime.support.info.Defines.*;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

public class StatusesUpload extends AsyncTask<String, Integer, String> {

    private String status;
    private File file;
    private ProgressBar pb;
    private long totalSize;
    private Handler mHandler;

    public StatusesUpload(String status, File file, ProgressBar pb,
                          Handler handler) {
        this.status = status;
        this.file = file;
        this.pb = pb;
        this.mHandler = handler;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... params) {
        Log.e(TAG, "StatusesUpload Weibo Thread START");
        String httpResult = null;

        HttpClient httpClient = new DefaultHttpClient();
        HttpContext httpContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(URLHelper.UPLOAD);

        CustomMultipartEntity multipartContent = new CustomMultipartEntity(
                new ProgressListener() {
                    @Override
                    public void transferred(long num) {
                        publishProgress((int) ((num / (float) totalSize) * 100));
                    }
                });

        try {
            multipartContent.addPart(PIC, new FileBody(file));
            multipartContent.addPart(ACCESS_TOKEN, new StringBody(
                    GlobalContext.getAccessToken()));
            multipartContent.addPart(STATUS, new StringBody(status));
            Log.e(TAG, GlobalContext.getAccessToken());
            Log.e(TAG, status);
            Log.e(TAG, file.getName());
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
        totalSize = multipartContent.getContentLength();

        try {

            httpPost.setEntity(multipartContent);
            httpResult = EntityUtils.toString(httpClient.execute(httpPost,
                    httpContext).getEntity());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return httpResult;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        pb.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        Log.e(TAG, result);
        Gson gson = new Gson();
        WeiboBackBean j = gson.fromJson(result, WeiboBackBean.class);

        mHandler.obtainMessage(GOT_UPLOAD_INFO, j).sendToTarget();
    }

}
