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
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_DESTROY_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_DESTROY_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.TAG;

public class CommentsDestroy extends Thread {

    private Handler mHandler;
    private String commentID;

    public CommentsDestroy(String commentID, Handler handler) {
        this.mHandler = handler;
        this.commentID = commentID;
    }

    public void run() {
        Log.e(TAG, "Comments Destroy Thread START");
        String httpResult;

        try {
            HashMap<String, String> hm = new HashMap<>();
            hm.put("cid", commentID);
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());

            httpResult = new HttpUtility().executePostTask(URLHelper.COMMENTS_DESTROY, hm);

            hm = null;

        } catch (Exception e) {
            e.printStackTrace();
            mHandler.obtainMessage(GOT_COMMENTS_DESTROY_INFO_FAIL, GlobalContext.getResString(R.string.toast_delete_fail)).sendToTarget();
            return;
        }

        if (ErrorCheck.getError(httpResult) == null)
            mHandler.obtainMessage(GOT_COMMENTS_DESTROY_INFO, GlobalContext.getResString(R.string.toast_delete_success)).sendToTarget();
        else
            mHandler.obtainMessage(GOT_COMMENTS_DESTROY_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();
    }
}
