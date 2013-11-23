package com.joewoo.ontime.action.comments;

import android.os.Handler;
import android.util.Log;

import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.error.ErrorCheck;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.util.GlobalContext;

import java.util.HashMap;

import static com.joewoo.ontime.support.info.Defines.ACCESS_TOKEN;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENT_CREATE_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENT_CREATE_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;

public class CommentsCreate extends Thread {

    private String weibo_id;
    private Handler mHandler;
    private String comment;
    private boolean comment_ori = false;

    public CommentsCreate(String comment, String weibo_id, Handler handler) {
        this.weibo_id = weibo_id;
        this.mHandler = handler;
        this.comment = comment;
    }

    public CommentsCreate(String comment, String weibo_id,
                          boolean comment_ori, Handler handler) {
        this.weibo_id = weibo_id;
        this.mHandler = handler;
        this.comment = comment;
        this.comment_ori = comment_ori;
    }

    public void run() {
        Log.e(TAG, "Comment Create Thread START");
        String httpResult = null;

        try {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(WEIBO_ID, weibo_id);
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());
            hm.put("comment", comment);

            if(comment_ori)
                hm.put("comment_ori", "1");

            httpResult = new HttpUtility().executePostTask(URLHelper.COMMENT_CREATE, hm);

            hm = null;

        } catch (Exception e) {
            Log.e(TAG, "Comment Create Thread FAILED");
            e.printStackTrace();
            mHandler.sendEmptyMessage(GOT_COMMENT_CREATE_INFO_FAIL);
            return;
        }

        if(ErrorCheck.getError(httpResult) == null)
            mHandler.sendEmptyMessage(GOT_COMMENT_CREATE_INFO);
        else
            mHandler.obtainMessage(GOT_COMMENT_CREATE_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();
    }
}
