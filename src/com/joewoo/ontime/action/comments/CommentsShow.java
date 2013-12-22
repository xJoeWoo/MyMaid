package com.joewoo.ontime.action.comments;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.R;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.CommentsBean;
import com.joewoo.ontime.support.bean.CommentsToMeBean;
import com.joewoo.ontime.support.error.ErrorCheck;
import com.joewoo.ontime.support.info.AcquireCount;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.util.GlobalContext;

import java.util.HashMap;
import java.util.List;

import static com.joewoo.ontime.support.info.Defines.ACCESS_TOKEN;
import static com.joewoo.ontime.support.info.Defines.COUNT;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMNETS_SHOW_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMNETS_SHOW_ADD_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMNETS_SHOW_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.MAX_ID;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;

public class CommentsShow extends Thread {

    private Handler mHandler;
    private String weiboID;
    private String maxID;

    public CommentsShow(String weiboID, Handler handler) {
        this.mHandler = handler;
        this.weiboID = weiboID;
    }

    public CommentsShow(String weiboID, String maxID, Handler handler) {
        this.mHandler = handler;
        this.weiboID = weiboID;
        this.maxID = maxID;
    }

    public void run() {

        String httpResult;
        Log.e(TAG, "Comments Show Thread START");
        try {
            HashMap<String, String> hm = new HashMap<>();
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());
            hm.put(WEIBO_ID, weiboID);

            if (maxID == null) {
                hm.put(COUNT, AcquireCount.COMMENTS_SHOW_COUNT);
            } else {
                hm.put(COUNT, AcquireCount.COMMENTS_SHOW_ADD_COUNT);
                hm.put(MAX_ID, maxID);
            }

            httpResult = new HttpUtility().executeGetTask(URLHelper.COMMENTS_SHOW, hm);

            hm = null;

        } catch (Exception e) {
            mHandler.obtainMessage(GOT_COMMNETS_SHOW_INFO_FAIL, GlobalContext.getResString(R.string.toast_comments_fail)).sendToTarget();
            e.printStackTrace();
            return;
        }

        if (ErrorCheck.getError(httpResult) == null) {

            List<CommentsBean> comments = new Gson().fromJson(httpResult,
                    CommentsToMeBean.class).getComments();


            if (maxID == null)
                mHandler.obtainMessage(GOT_COMMNETS_SHOW_INFO, comments).sendToTarget();
            else {
                comments.remove(0);
                mHandler.obtainMessage(GOT_COMMNETS_SHOW_ADD_INFO, comments).sendToTarget();
            }


        } else {
            mHandler.obtainMessage(GOT_COMMNETS_SHOW_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();
        }
    }
}
