package com.joewoo.ontime.action.weather;

import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.aqi.AQIBean;
import com.joewoo.ontime.support.bean.aqi.AQIDetailsBean;
import com.joewoo.ontime.support.bean.weather.WeatherNowBean;
import com.joewoo.ontime.support.info.Defines;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.net.JavaHttpUtility;
import com.joewoo.ontime.support.notification.MyMaidNotificationHelper;
import com.joewoo.ontime.support.setting.MyMaidSettingsHelper;
import com.joewoo.ontime.support.util.GlobalContext;
import com.joewoo.ontime.support.util.MyMaidUtilites;

import java.util.HashMap;
import java.util.List;

/**
 * Created by JoeWoo on 14-1-6.
 */
public class Weather extends Thread {

    public Weather() {

    }

    @Override
    public void run() {

        if (!MyMaidSettingsHelper.getBoolean(MyMaidSettingsHelper.WEATHER_STATUS))
            return;

        /*AQI*/

        String city = MyMaidSettingsHelper.getString(MyMaidSettingsHelper.WEATHER_CITY);
        Log.e(Defines.TAG, city);
        String httpResult = null;
        AQIBean aqiBean = null;
        String cityID = null;

        Log.e(Defines.TAG, "Weather Thread START");

        if (city != null && !city.equals("")) {
            cityID = MyMaidUtilites.CityIDs.getID(city);
            HashMap<String, String> hm = new HashMap<>();

            hm.put("city", city);
            hm.put("stations", "no");
            hm.put("token", Defines.PM25_APP_KEY);

            try {
                httpResult = new JavaHttpUtility().doGet(URLHelper.AQI_DETAILS, hm);
                if (httpResult.startsWith("["))
                    httpResult = "{\"aqi\":" + httpResult + "}";
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (httpResult != null) {
                AQIDetailsBean b = new Gson().fromJson(httpResult, AQIDetailsBean.class);
                List<AQIBean> aqis = b.getAQIs();
                if (b.getError() == null && aqis.size() > 0 && aqis.get(0).getAQI() != null) {
                    aqiBean = aqis.get(0);
                } else if (b.getError() != null) {
                    Log.e(Defines.TAG, b.getError());
//                    handler.obtainMessage(Defines.GOT_AQI_INFO_FAIL, GlobalContext.getResString(R.string.toast_aqi_city_not_supported)).sendToTarget();
//                    MyMaidSettingsHelper.save(MyMaidSettingsHelper.WEATHER_STATUS, false);
                }
            }

        } else {
            Log.e(Defines.TAG, "AQI Wrong");
            MyMaidSettingsHelper.save(MyMaidSettingsHelper.WEATHER_STATUS, false);
        }




        /*WEATHER*/

        if (cityID == null) {
            Log.e(Defines.TAG, "Weather City Wrong");
            return;
        }

        try {
            httpResult = new HttpUtility().executeGetTask(URLHelper.WEATHER_NOW + cityID + ".html", null);
            httpResult = httpResult.replace("{\"weatherinfo\":", "");
            httpResult = httpResult.substring(0, httpResult.length() - 1);

            WeatherNowBean weatherBean = new Gson().fromJson(httpResult, WeatherNowBean.class);

            if (aqiBean != null)
                new MyMaidNotificationHelper(MyMaidNotificationHelper.WEATHER, null, GlobalContext.getAppContext()).setWeather(aqiBean, weatherBean);
            else
                new MyMaidNotificationHelper(MyMaidNotificationHelper.WEATHER, null, GlobalContext.getAppContext()).setWeather(weatherBean);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
