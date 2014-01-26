package com.joewoo.ontime.action.comments;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.R;
import com.joewoo.ontime.action.MyMaidActionHelper;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.action.remind.RemindSetCount;
import com.joewoo.ontime.support.bean.CommentsBean;
import com.joewoo.ontime.support.bean.CommentsMentionsBean;
import com.joewoo.ontime.support.info.AcquireCount;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.sql.MyMaidSQLHelper;
import com.joewoo.ontime.support.util.GlobalContext;
import com.joewoo.ontime.support.util.TimeFormat;

import java.util.HashMap;
import java.util.List;

import static com.joewoo.ontime.support.info.Defines.ACCESS_TOKEN;
import static com.joewoo.ontime.support.info.Defines.COUNT;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_MENTIONS_ADD_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_MENTIONS_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_MENTIONS_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.MAX_ID;
import static com.joewoo.ontime.support.info.Defines.TAG;

/**
 * Created by JoeWoo on 13-10-26.
 * OOOOOH I HAVE GRADE 3 IN SENIOR HIGH!!!
 */
public class CommentsMentions extends Thread {

    private Handler mHandler;
    public boolean isProvidedResult = false;
    private String httpResult;
    private String maxID;

    public CommentsMentions(boolean isProvided,  Handler handler) {
        this.mHandler = handler;
        this.isProvidedResult = isProvided;
    }

    public CommentsMentions(String maxID, Handler handler) {
        this.maxID = maxID;
        this.mHandler = handler;
    }

    public void run() {
        Log.e(TAG, "Comments StatusesMentions Thread START");

        if (!isProvidedResult) {
            if (!fresh())
                return;
        } else {
            httpResult = MyMaidSQLHelper.getOneString(MyMaidSQLHelper.COMMENTS_MENTIONS);
            if (httpResult == null)
                if (!fresh())
                    return;
        }

        try {
            Log.e(TAG, httpResult);

            List<CommentsBean> comments = new Gson().fromJson(httpResult,
                    CommentsMentionsBean.class).getComments();

            String source;

            for (CommentsBean c : comments) {

                c.setCreatedAt(TimeFormat.parse(c.getCreatedAt()));

                source = c.getSource();
                source = source.substring(source.indexOf(">") + 1,
                        source.indexOf("</a>"));
                c.setSource(source);
            }

            if (maxID == null)
                mHandler.obtainMessage(GOT_COMMENTS_MENTIONS_INFO, comments).sendToTarget();
            else {
                comments.remove(0);
                mHandler.obtainMessage(GOT_COMMENTS_MENTIONS_ADD_INFO, comments).sendToTarget();
            }

            comments = null;

            if (!isProvidedResult && maxID == null)
                MyMaidActionHelper.remindSetCount(RemindSetCount.COMMENT_MENTIONS_COUNT);

        } catch (Exception e) {
            mHandler.obtainMessage(GOT_COMMENTS_MENTIONS_INFO_FAIL, GlobalContext.getResString(R.string.toast_mentions_fail)).sendToTarget();
            e.printStackTrace();
        }
    }

    private boolean fresh() {
        try {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());
            hm.put(COUNT, AcquireCount.COMMENTS_MENTIONS_COUNT);

            if (maxID != null) {
                hm.put(MAX_ID, maxID);
            }

            httpResult = new HttpUtility().executeGetTask(URLHelper.COMMENTS_MENTIONS, hm);

            hm = null;

            if(maxID == null)
                MyMaidSQLHelper.saveOneString(MyMaidSQLHelper.COMMENTS_MENTIONS, httpResult);

            return true;
        } catch (Exception e) {
            mHandler.obtainMessage(GOT_COMMENTS_MENTIONS_INFO_FAIL, GlobalContext.getResString(R.string.toast_mentions_fail)).sendToTarget();
            e.printStackTrace();
            return false;
        }
    }

}
