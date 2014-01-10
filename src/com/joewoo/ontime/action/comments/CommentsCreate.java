package com.joewoo.ontime.action.comments;

import android.os.Handler;
import android.util.Log;

import com.joewoo.ontime.R;
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

    private String weiboID;
    private Handler mHandler;
    private String comment;
    private boolean commentOri = false;

    public CommentsCreate(String comment, String weiboID, Handler handler) {
        this.weiboID = weiboID;
        this.mHandler = handler;
        this.comment = comment;
    }

    public CommentsCreate(String comment, String weiboID,
                          boolean commentOri, Handler handler) {
        this.weiboID = weiboID;
        this.mHandler = handler;
        this.comment = comment;
        this.commentOri = commentOri;
    }

    public void run() {
        Log.e(TAG, "Comment Create Thread START");
        String httpResult = null;

        try {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(WEIBO_ID, weiboID);
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());
            hm.put("comment", comment);

            if(commentOri)
                hm.put("comment_ori", "1");

            httpResult = new HttpUtility().executePostTask(URLHelper.COMMENT_CREATE, hm);

            hm = null;

        } catch (Exception e) {
            Log.e(TAG, "Comment Create Thread FAILED");
            e.printStackTrace();
            mHandler.obtainMessage(GOT_COMMENT_CREATE_INFO_FAIL, GlobalContext.getResString(R.string.error_network_not_avaiable)).sendToTarget();
            return;
        }

        if(ErrorCheck.getError(httpResult) == null)
            mHandler.obtainMessage(GOT_COMMENT_CREATE_INFO, GlobalContext.getResString(R.string.toast_comment_success)).sendToTarget();

        else
            mHandler.obtainMessage(GOT_COMMENT_CREATE_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();
    }
}
