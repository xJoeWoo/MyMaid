package com.joewoo.ontime.action.statuses;

import static com.joewoo.ontime.support.info.Defines.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.R;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.WeiboBackBean;
import com.joewoo.ontime.support.error.ErrorCheck;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.util.GlobalContext;

public class StatusesRepost extends Thread {
    private String weibo_id;
    private Handler mHandler;
    private String status;
    private boolean isComment = false;

    public StatusesRepost(String status, String weibo_id, Handler handler) {
        this.weibo_id = weibo_id;
        this.mHandler = handler;
        this.status = status;
    }

    public StatusesRepost(String comment, String weibo_id, boolean isComment,
                          Handler handler) {
        this.weibo_id = weibo_id;
        this.mHandler = handler;
        this.status = comment;
        this.isComment = isComment;
    }

    public void run() {
        Log.e(TAG, "StatusesRepost Thread START");
        String httpResult;

        try {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(WEIBO_ID, weibo_id);
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());
            hm.put(STATUS, status);
            if(isComment)
                hm.put("is_comment", "1");

            httpResult = new HttpUtility().executePostTask(URLHelper.REPOST, hm);

            hm = null;

        } catch (Exception e) {
            e.printStackTrace();
            mHandler.obtainMessage(GOT_REPOST_INFO_FAIL, GlobalContext.getAppContext().getString(R.string.toast_repost_fail)).sendToTarget();
            return;
        }

        if(ErrorCheck.getError(httpResult) == null)
            mHandler.obtainMessage(GOT_REPOST_INFO, GlobalContext.getAppContext().getString(R.string.toast_repost_success)).sendToTarget();
        else
            mHandler.obtainMessage(GOT_REPOST_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();
    }
}
