package com.joewoo.ontime.action.statuses;

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
import com.joewoo.ontime.support.bean.StatusesBean;
import com.joewoo.ontime.support.bean.UserTimelineBean;
import com.joewoo.ontime.support.error.ErrorCheck;
import com.joewoo.ontime.support.util.GlobalContext;

import android.os.Handler;
import android.util.Log;

public class StatusesUserTimeLine extends Thread {

    private String count;
    private Handler mHandler;
    private String screenName;
    private String max_id;

    public StatusesUserTimeLine(String screenName, int count,
                                Handler handler) {
        this.screenName = screenName;
        this.count = String.valueOf(count);
        this.mHandler = handler;
    }

    public StatusesUserTimeLine(String screenName, int count, String max_id, Handler handler) {
        this.screenName = screenName;
        this.count = String.valueOf(count);
        this.mHandler = handler;
        this.max_id = max_id;
    }


    public void run() {
        Log.e(TAG, "User Time Line Thread START");
        String httpResult;

        HttpUriRequest httpGet;
        Log.e(TAG, screenName);

        if (max_id == null)
            httpGet = new HttpGet(URLHelper.USER_TIMELINE + "?access_token="
                    + GlobalContext.getAccessToken() + "&screen_name="
                    + screenName + "&count=" + count);
        else
            httpGet = new HttpGet(URLHelper.USER_TIMELINE + "?access_token="
                    + GlobalContext.getAccessToken() + "&screen_name="
                    + screenName + "&count=" + count + "&max_id=" + max_id);


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
            is.close();
            baos.close();

            Log.e(TAG,
                    "GOT Statues length: "
                            + String.valueOf(httpResult.length()));
            Log.e(TAG, httpResult);

        } catch (Exception e) {
            Log.e(TAG, "User Timeline Thread Network Failed");
            e.printStackTrace();
            mHandler.sendEmptyMessage(GOT_USER_TIMELINE_INFO_FAIL);
            return;
        }

        if (ErrorCheck.getError(httpResult) == null) {

            UserTimelineBean timeline = new Gson().fromJson(httpResult,
                    UserTimelineBean.class);

            ArrayList<HashMap<String, String>> text = new ArrayList<HashMap<String, String>>();

            if (max_id == null) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(BLANK, " ");
                text.add(map);
            }

            List<StatusesBean> statuses = timeline.getStatuses();

            String source;
            String rt_source;

            for (int i = 0; i < statuses.size(); i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                source = statuses.get(i).getSource();
                source = source.substring(source.indexOf(">") + 1,
                        source.length());
                source = source.substring(0, source.indexOf("<"));
                map.put(SOURCE, " · " + source);
                source = statuses.get(i).getCreatedAt();
                source = source.substring(source.indexOf(":") - 2,
                        source.indexOf(":") + 3);
                map.put(CREATED_AT, source);
                map.put(UID, statuses.get(i).getUser().getId());
                map.put(SCREEN_NAME, statuses.get(i).getUser().getScreenName());
                map.put(TEXT, statuses.get(i).getText());
                map.put(COMMENTS_COUNT, statuses.get(i).getCommentsCount());
                map.put(REPOSTS_COUNT, statuses.get(i).getRepostsCount());
                map.put(WEIBO_ID, statuses.get(i).getId());
                map.put(PROFILE_IMAGE_URL, statuses.get(i).getUser()
                        .getProfileImageUrl());

                try {

                    map.put(RETWEETED_STATUS_UID, statuses.get(i)
                            .getRetweetedStatus().getUser().getId());
                    rt_source = statuses.get(i).getRetweetedStatus()
                            .getSource();
                    rt_source = rt_source.substring(rt_source.indexOf(">") + 1,
                            rt_source.length());
                    rt_source = rt_source.substring(0, rt_source.indexOf("<"));
                    map.put(RETWEETED_STATUS_SOURCE, " · " + rt_source);
                    rt_source = statuses.get(i).getRetweetedStatus()
                            .getCreatedAt();
                    rt_source = rt_source.substring(rt_source.indexOf(":") - 2,
                            rt_source.indexOf(":") + 3);
                    map.put(RETWEETED_STATUS_CREATED_AT, rt_source);

                    map.put(RETWEETED_STATUS_COMMENTS_COUNT, statuses.get(i)
                            .getRetweetedStatus().getCommentsCount());
                    map.put(RETWEETED_STATUS_REPOSTS_COUNT, statuses.get(i)
                            .getRetweetedStatus().getRepostsCount());
                    map.put(RETWEETED_STATUS_SCREEN_NAME, statuses.get(i)
                            .getRetweetedStatus().getUser().getScreenName());
                    map.put(RETWEETED_STATUS, statuses.get(i)
                            .getRetweetedStatus().getText());

                    if (statuses.get(i).getRetweetedStatus().getThumbnailPic() != null) {
                        map.put(RETWEETED_STATUS_THUMBNAIL_PIC, statuses.get(i)
                                .getRetweetedStatus().getThumbnailPic());
                        map.put(RETWEETED_STATUS_BMIDDLE_PIC, statuses.get(i)
                                .getRetweetedStatus().getBmiddlePic());
                    }
                    map.put(IS_REPOST, " ");

                } catch (Exception e) {
//                    e.printStackTrace();
                }

                if (statuses.get(i).getThumbnailPic() != null) {
                    map.put(THUMBNAIL_PIC, statuses.get(i).getThumbnailPic());
                    map.put(BMIDDLE_PIC, statuses.get(i).getBmiddlePic());
                }
                text.add(map);
            }

            mHandler.obtainMessage(GOT_USER_TIMELINE_INFO, text).sendToTarget();
        } else {
            mHandler.obtainMessage(GOT_USER_TIMELINE_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();
        }
    }
}
