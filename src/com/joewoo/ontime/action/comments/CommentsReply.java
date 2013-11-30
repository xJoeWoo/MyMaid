package com.joewoo.ontime.action.comments;

import android.os.Handler;
import android.util.Log;

import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.error.ErrorCheck;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.util.GlobalContext;

import java.util.HashMap;

import static com.joewoo.ontime.support.info.Defines.ACCESS_TOKEN;
import static com.joewoo.ontime.support.info.Defines.GOT_REPLY_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_REPLY_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;

public class CommentsReply extends Thread {

    private String comment;
    private String weibo_id;
    private String comment_id;
    private Handler mHandler;
    private boolean comment_ori;

    public CommentsReply(String comment, String weibo_id, String comment_id, boolean comment_ori,
                         Handler handler) {
        this.comment = comment;
        this.weibo_id = weibo_id;
        this.comment_id = comment_id;
        this.mHandler = handler;
        this.comment_ori = comment_ori;
    }

    public void run() {
        Log.e(TAG, "Comment Reply Thread START");
        String httpResult;

        try {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(WEIBO_ID, weibo_id);
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());
            hm.put("comment", comment);
            hm.put("cid", comment_id);

            if(comment_ori)
                hm.put("comment_ori", "1");

            httpResult = new HttpUtility().executePostTask(URLHelper.REPLY, hm);

            hm = null;

        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(GOT_REPLY_INFO_FAIL);
            return;
        }

        if(ErrorCheck.getError(httpResult) == null)
            mHandler.sendEmptyMessage(GOT_REPLY_INFO);
        else
            mHandler.obtainMessage(GOT_REPLY_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();
    }

}
