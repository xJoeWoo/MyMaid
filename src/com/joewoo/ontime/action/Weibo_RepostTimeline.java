package com.joewoo.ontime.action;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.bean.CommentsBean;
import com.joewoo.ontime.bean.CommentsToMeBean;
import com.joewoo.ontime.bean.RepostTimelineBean;
import com.joewoo.ontime.bean.StatusesBean;
import com.joewoo.ontime.info.WeiboConstant;
import com.joewoo.ontime.info.Weibo_URLs;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static com.joewoo.ontime.info.Defines.*;

/**
 * Created by JoeWoo on 13-10-19.
 */
public class Weibo_RepostTimeline extends Thread {

    private Handler mHandler;
    private String weibo_id;
    private String max_id;
    private String count;

    public Weibo_RepostTimeline(int count, String weibo_id, Handler handler) {
        this.weibo_id = weibo_id;
        this.mHandler = handler;
        this.count = String.valueOf(count);
    }

    public Weibo_RepostTimeline(int count, String weibo_id, String max_id, Handler handler) {
        this.weibo_id = weibo_id;
        this.mHandler = handler;
        this.max_id = max_id;
        this.count = String.valueOf(count);
    }

    public void run() {
        String httpResult = "{ \"error_code\" : \"233\" }";
        Log.e(TAG, "Reposts Timeline Thread Start");

        HttpUriRequest httpGet;
        if (max_id == null) {

            httpGet = new HttpGet(Weibo_URLs.REPOST_TIMELINE + "?access_token="
                    + WeiboConstant.ACCESS_TOKEN + "&id=" + weibo_id + "&count="
                    + count);
        } else {
            httpGet = new HttpGet(Weibo_URLs.REPOST_TIMELINE + "?access_token="
                    + WeiboConstant.ACCESS_TOKEN + "&id=" + weibo_id
                    + "&max_id=" + max_id + "&count="
                    + count);
        }
        httpGet.addHeader("Accept-Encoding", "gzip");

        try {

            InputStream is = new DefaultHttpClient().execute(httpGet)
                    .getEntity().getContent();

            is = new GZIPInputStream(is);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int i = -1;
            while ((i = is.read()) != -1) {
                baos.write(i);
            }

            httpResult = baos.toString();

            Log.e(TAG,
                    "GOT Statues length: "
                            + String.valueOf(httpResult.length()));
            Log.e(TAG, httpResult);
        } catch (Exception e) {
            mHandler.sendEmptyMessage(GOT_REPOST_TIMELINE_INFO_FAIL);
            e.printStackTrace();
        }

        try {
            List<StatusesBean> reposts = new Gson().fromJson(httpResult,
                    RepostTimelineBean.class).getReposts();

            ArrayList<HashMap<String, String>> text = new ArrayList<HashMap<String, String>>();


            for (StatusesBean c : reposts) {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put(SCREEN_NAME, c.getUser().getScreenName());
                map.put(TEXT, c.getText());
                map.put(REPOST_WEIBO_ID, c.getId());

                text.add(map);
            }

            mHandler.obtainMessage(GOT_REPOST_TIMELINE_INFO, text)
                    .sendToTarget();

        } catch (Exception e) {
            mHandler.sendEmptyMessage(GOT_REPOST_TIMELINE_INFO_FAIL);
            e.printStackTrace();
        }
    }
}
