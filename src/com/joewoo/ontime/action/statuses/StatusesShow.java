package com.joewoo.ontime.action.statuses;

import android.os.Handler;

import com.google.gson.Gson;
import com.joewoo.ontime.R;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.StatusesBean;
import com.joewoo.ontime.support.error.ErrorCheck;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.util.GlobalContext;
import com.joewoo.ontime.support.util.TimeFormat;

import java.util.HashMap;

import static com.joewoo.ontime.support.info.Defines.ACCESS_TOKEN;
import static com.joewoo.ontime.support.info.Defines.BMIDDLE_PIC;
import static com.joewoo.ontime.support.info.Defines.COMMENTS_COUNT;
import static com.joewoo.ontime.support.info.Defines.CREATED_AT;
import static com.joewoo.ontime.support.info.Defines.GOT_STATUSES_SHOW_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_STATUSES_SHOW_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.IS_REPOST;
import static com.joewoo.ontime.support.info.Defines.PIC_URLS;
import static com.joewoo.ontime.support.info.Defines.PROFILE_IMAGE_URL;
import static com.joewoo.ontime.support.info.Defines.REPOSTS_COUNT;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_BMIDDLE_PIC;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_COMMENTS_COUNT;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_CREATED_AT;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_REPOSTS_COUNT;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_SOURCE;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_THUMBNAIL_PIC;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_UID;
import static com.joewoo.ontime.support.info.Defines.SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.SOURCE;
import static com.joewoo.ontime.support.info.Defines.TEXT;
import static com.joewoo.ontime.support.info.Defines.THUMBNAIL_PIC;
import static com.joewoo.ontime.support.info.Defines.UID;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;

/**
 * Created by JoeWoo on 13-12-6.
 */
public class StatusesShow extends Thread {

    private String weiboID;
    private Handler mHandler;

    public StatusesShow(String weiboID, Handler handler) {
        this.weiboID = weiboID;
        this.mHandler = handler;
    }

    @Override
    public void run() {

        String httpResult;

        try {
            HashMap<String, String> hm = new HashMap<>();
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());
            hm.put(WEIBO_ID, weiboID);


            httpResult = new HttpUtility().executeGetTask(URLHelper.STATUSES_SHOW, hm);

            hm = null;


        } catch (Exception e) {
            mHandler.obtainMessage(GOT_STATUSES_SHOW_INFO_FAIL, GlobalContext.getResString(R.string.toast_statuses_show_fail)).sendToTarget();
            e.printStackTrace();
            return;
        }

        if(ErrorCheck.getError(httpResult) == null) {

            String source;

            StatusesBean s = new Gson().fromJson(httpResult, StatusesBean.class);

            HashMap<String, String> map = new HashMap<>();

            source = s.getSource();

            map.put(SOURCE, " · " + source.substring(source.indexOf(">") + 1,
                    source.indexOf("</a>")));

            map.put(CREATED_AT, TimeFormat.parse(s.getCreatedAt()));

            map.put(UID, s.getUser().getId());
            map.put(SCREEN_NAME, s.getUser().getScreenName());
            map.put(TEXT, s.getText());
            map.put(COMMENTS_COUNT, s.getCommentsCount());
            map.put(REPOSTS_COUNT, s.getRepostsCount());
            map.put(WEIBO_ID, s.getId());
            map.put(PROFILE_IMAGE_URL, s.getUser()
                    .getProfileImageUrl());

            if (s.getPicURLs() != null && s.getPicURLs().size() > 1)
                map.put(PIC_URLS, " ");


            try {

                map.put(RETWEETED_STATUS_UID, s
                        .getRetweetedStatus().getUser().getId());

                source = s.getRetweetedStatus()
                        .getSource();

                map.put(RETWEETED_STATUS_SOURCE, " · " + source.substring(source.indexOf(">") + 1,
                        source.indexOf("</a>")));

                map.put(RETWEETED_STATUS_CREATED_AT, TimeFormat.parse(s.getRetweetedStatus().getCreatedAt()));

                map.put(RETWEETED_STATUS_COMMENTS_COUNT, s
                        .getRetweetedStatus().getCommentsCount());
                map.put(RETWEETED_STATUS_REPOSTS_COUNT, s
                        .getRetweetedStatus().getRepostsCount());
                map.put(RETWEETED_STATUS_SCREEN_NAME, s
                        .getRetweetedStatus().getUser().getScreenName());
                map.put(RETWEETED_STATUS, s
                        .getRetweetedStatus().getText());

                if (s.getRetweetedStatus().getPicURLs() != null && s.getRetweetedStatus().getPicURLs().size() > 1)
                    map.put(PIC_URLS, " ");

                if (s.getRetweetedStatus().getThumbnailPic() != null) {
                    map.put(RETWEETED_STATUS_THUMBNAIL_PIC, s
                            .getRetweetedStatus().getThumbnailPic());
                    map.put(RETWEETED_STATUS_BMIDDLE_PIC, s
                            .getRetweetedStatus().getBmiddlePic());
                }
                map.put(IS_REPOST, " ");

            } catch (Exception e) {

            }

            if (s.getThumbnailPic() != null) {
                map.put(THUMBNAIL_PIC, s.getThumbnailPic());
                map.put(BMIDDLE_PIC, s.getBmiddlePic());
            }

            mHandler.obtainMessage(GOT_STATUSES_SHOW_INFO, map).sendToTarget();
        } else {
            mHandler.obtainMessage(GOT_STATUSES_SHOW_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();
        }

    }
}
