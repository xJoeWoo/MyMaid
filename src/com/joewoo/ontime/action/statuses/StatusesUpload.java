package com.joewoo.ontime.action.statuses;

import android.os.Handler;
import android.util.Log;

import com.joewoo.ontime.R;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.error.ErrorCheck;
import com.joewoo.ontime.support.listener.MyMaidListeners;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.util.GlobalContext;

import java.util.HashMap;

import static com.joewoo.ontime.support.info.Defines.ACCESS_TOKEN;
import static com.joewoo.ontime.support.info.Defines.GOT_UPLOAD_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_UPLOAD_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.STATUS;
import static com.joewoo.ontime.support.info.Defines.TAG;

public class StatusesUpload extends Thread {

    private String status;
    private String filePath;
    private Handler mHandler;
    private MyMaidListeners.UploadProgressListener listener;

    public StatusesUpload(String status, String filePath, MyMaidListeners.UploadProgressListener listener, Handler handler) {
        this.status = status;
        this.filePath = filePath;
        this.mHandler = handler;
        this.listener = listener;
    }


    public void run() {
        Log.e(TAG, "Statuses Upload Weibo Thread START");
        String httpResult = null;
        try {
            HashMap<String, String> hm = new HashMap<String, String>();

            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());
            hm.put(STATUS, status);

            httpResult = new HttpUtility().executeUploadImageTask(URLHelper.UPLOAD, hm, filePath, listener);


        } catch (Exception e) {
            e.printStackTrace();
            mHandler.obtainMessage(GOT_UPLOAD_INFO_FAIL, GlobalContext.getResString(R.string.error_network_not_avaiable)).sendToTarget();
            return;
        }

        if (ErrorCheck.getError(httpResult) == null) {
            mHandler.sendEmptyMessage(GOT_UPLOAD_INFO);
        } else {
            mHandler.obtainMessage(GOT_UPLOAD_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();
        }
    }

}
