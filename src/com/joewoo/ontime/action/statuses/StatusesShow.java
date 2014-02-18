package com.joewoo.ontime.action.statuses;

import android.os.Handler;

import com.google.gson.Gson;
import com.joewoo.ontime.R;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.StatusesBean;
import com.joewoo.ontime.support.error.ErrorCheck;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.util.GlobalContext;
import com.joewoo.ontime.support.util.MyMaidUtilites;

import java.util.HashMap;

import static com.joewoo.ontime.support.info.Defines.ACCESS_TOKEN;
import static com.joewoo.ontime.support.info.Defines.GOT_STATUSES_SHOW_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_STATUSES_SHOW_INFO_FAIL;
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

        if (ErrorCheck.getError(httpResult) == null) {

            String source;

            StatusesBean s = new Gson().fromJson(httpResult, StatusesBean.class);

            s.setCreatedAt(MyMaidUtilites.TimeFormat.parse(s.getCreatedAt()));

            source = s.getSource();
            source = source.substring(source.indexOf(">") + 1,
                    source.indexOf("</a>"));
            s.setSource(source);

            if (s.getRetweetedStatus() != null) {

                s.getRetweetedStatus().setCreatedAt(MyMaidUtilites.TimeFormat.parse(s.getRetweetedStatus().getCreatedAt()));

                source = s.getRetweetedStatus().getSource();
                source = source.substring(source.indexOf(">") + 1,
                        source.indexOf("</a>"));
                s.getRetweetedStatus().setSource(source);
            }

            mHandler.obtainMessage(GOT_STATUSES_SHOW_INFO, s).sendToTarget();
        } else {
            mHandler.obtainMessage(GOT_STATUSES_SHOW_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();
        }

    }
}
