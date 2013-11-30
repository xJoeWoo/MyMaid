package com.joewoo.ontime.action.comments;

import static com.joewoo.ontime.support.info.Defines.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.CommentsBean;
import com.joewoo.ontime.support.bean.CommentsToMeBean;
import com.joewoo.ontime.support.info.AcquireCount;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.util.GlobalContext;

import android.os.Handler;
import android.util.Log;

public class CommentsShow extends Thread {

    private Handler mHandler;
    private String weibo_id;
    private String max_id;

    public CommentsShow(String weibo_id, Handler handler) {
        this.mHandler = handler;
        this.weibo_id = weibo_id;
    }

    public CommentsShow(String weibo_id, String max_id, Handler handler) {
        this.mHandler = handler;
        this.weibo_id = weibo_id;
        this.max_id = max_id;
    }

    public void run() {

        String httpResult = null;
        Log.e(TAG, "Comments Show Thread START");
        try{
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());
            hm.put(WEIBO_ID, weibo_id);

            if(max_id == null) {
                hm.put(COUNT, AcquireCount.COMMENTS_SHOW_COUNT);
            } else {
                hm.put(COUNT, AcquireCount.COMMENTS_SHOW_ADD_COUNT);
                hm.put(MAX_ID, max_id);
            }

            httpResult = new HttpUtility().executeGetTask(URLHelper.COMMENTS_SHOW, hm);

            hm = null;

        } catch (Exception e) {
            mHandler.sendEmptyMessage(GOT_COMMNETS_SHOW_INFO_FAIL);
            e.printStackTrace();
            return;
        }

        try {
            List<CommentsBean> comments = new Gson().fromJson(httpResult,
                    CommentsToMeBean.class).getComments();

            ArrayList<HashMap<String, String>> text = new ArrayList<HashMap<String, String>>();

//			for (int i = 0; i < comments.size(); i++) {
//				HashMap<String, String> map = new HashMap<String, String>();
//
//				map.put(SCREEN_NAME, comments.get(i).getUser().getScreenName());
//				map.put(TEXT, comments.get(i).getText());
//				map.put(COMMENT_ID, comments.get(i).getId());
////				map.put(WEIBO_ID, comments.get(i).getStatus().getId());
//
//				text.add(map);
//			}

            for (CommentsBean c : comments) {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put(SCREEN_NAME, c.getUser().getScreenName());
                map.put(TEXT, c.getText());
                map.put(COMMENT_ID, c.getId());

                text.add(map);

            }

            mHandler.obtainMessage(GOT_COMMNETS_SHOW_INFO, text)
                    .sendToTarget();

        } catch (Exception e) {
            mHandler.sendEmptyMessage(GOT_COMMNETS_SHOW_INFO_FAIL);
            e.printStackTrace();
        }
    }
}
