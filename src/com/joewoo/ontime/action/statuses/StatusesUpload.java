package com.joewoo.ontime.action.statuses;

import java.util.HashMap;

import com.google.gson.Gson;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.WeiboBackBean;
import com.joewoo.ontime.support.image.ImageUploadHelper;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.util.GlobalContext;

import static com.joewoo.ontime.support.info.Defines.*;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

public class StatusesUpload extends AsyncTask<String, Integer, String> implements ImageUploadHelper.ProgressListener {

    private String status;
    private String filePath;
    private ProgressBar pb;
    private Handler mHandler;

    public StatusesUpload(String status, String filePath, ProgressBar pb,
                          Handler handler) {
        this.status = status;
        this.filePath = filePath;
        this.pb = pb;
        this.mHandler = handler;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... params) {
        Log.e(TAG, "Statuses Upload Weibo Thread START");
        try {
            HashMap<String, String> hm = new HashMap<String, String>();

            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());
            hm.put(STATUS, status);

            return new HttpUtility().executeUploadImageTask(URLHelper.UPLOAD, hm
                    , filePath, PIC, this);


        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(GOT_UPLOAD_INFO_FAIL);
            return null;
        }
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

    @Override
    public void uploadProgress(int data, int contentLength) {
        publishProgress((int) (((float) data / (float) contentLength) * 100) + 1);
    }

    @Override
    public void waitResponse() {

    }
}
