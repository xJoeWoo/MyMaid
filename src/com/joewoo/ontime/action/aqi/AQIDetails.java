package com.joewoo.ontime.action.aqi;

import android.os.Handler;

import com.google.gson.Gson;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.aqi.AQIBean;
import com.joewoo.ontime.support.bean.aqi.AQIDetailsBean;
import com.joewoo.ontime.support.info.Defines;
import com.joewoo.ontime.support.net.JavaHttpUtility;
import com.joewoo.ontime.support.notification.MyMaidNotificationHelper;
import com.joewoo.ontime.support.util.GlobalContext;

import java.util.HashMap;
import java.util.List;

/**
 * Created by JoeWoo on 14-1-6.
 */
public class AQIDetails extends Thread {

    private Handler handler;
    private String city;


    public AQIDetails(String city) {
        this.city = city;
    }

    public AQIDetails() {

    }

    @Override
    public void run() {

        String httpResult = null;
        HashMap<String, String> hm = new HashMap<>();

        if (city == null)
            hm.put("city", "zhaoqing");
        else
            hm.put("city", city);
        hm.put("stations", "no");
        hm.put("token", Defines.PM25_APP_KEY);

        try {
            httpResult = "{\"aqi\":" + new JavaHttpUtility().doGet(URLHelper.AQI_DETAILS, hm) + "}";

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(httpResult != null) {
            List<AQIBean> aqis = new Gson().fromJson(httpResult, AQIDetailsBean.class).getAQIs();
            if (aqis.size() > 0 && aqis.get(0).getAQI() != null) {
                new MyMaidNotificationHelper(99, null, GlobalContext.getAppContext()).setAQI(aqis.get(0));
            }
        }

    }


}
