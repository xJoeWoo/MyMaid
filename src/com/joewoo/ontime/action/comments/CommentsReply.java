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
import static com.joewoo.ontime.support.info.Defines.GOT_REPLY_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_REPLY_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;

public class CommentsReply extends Thread {

    private String comment;
    private String weiboID;
    private String commentID;
    private Handler mHandler;
    private boolean commentOri;

    public CommentsReply(String comment, String weiboID, String commentID, boolean commentOri,
                         Handler handler) {
        this.comment = comment;
        this.weiboID = weiboID;
        this.commentID = commentID;
        this.mHandler = handler;
        this.commentOri = commentOri;
    }

    public void run() {
        Log.e(TAG, "Comment Reply Thread START");
        String httpResult;

        try {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(WEIBO_ID, weiboID);
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());
            hm.put("comment", comment);
            hm.put("cid", commentID);

            if(commentOri)
                hm.put("comment_ori", "1");

            httpResult = new HttpUtility().executePostTask(URLHelper.REPLY, hm);

            hm = null;

        } catch (Exception e) {
            e.printStackTrace();
            mHandler.obtainMessage(GOT_REPLY_INFO_FAIL, GlobalContext.getResString(R.string.error_network_not_avaiable)).sendToTarget();
            return;
        }

        if(ErrorCheck.getError(httpResult) == null)
            mHandler.obtainMessage(GOT_REPLY_INFO, GlobalContext.getResString(R.string.toast_reply_success)).sendToTarget();
        else
            mHandler.obtainMessage(GOT_REPLY_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();
    }

}
