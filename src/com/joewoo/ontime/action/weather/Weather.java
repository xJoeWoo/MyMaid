package com.joewoo.ontime.action.weather;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.aqi.AQIBean;
import com.joewoo.ontime.support.bean.aqi.AQIDetailsBean;
import com.joewoo.ontime.support.bean.weather.WeatherNowBean;
import com.joewoo.ontime.support.info.CityIDs;
import com.joewoo.ontime.support.info.Defines;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.net.JavaHttpUtility;
import com.joewoo.ontime.support.notification.MyMaidNotificationHelper;
import com.joewoo.ontime.support.setting.MyMaidSettingsHelper;
import com.joewoo.ontime.support.util.GlobalContext;

import java.util.HashMap;
import java.util.List;

/**
 * Created by JoeWoo on 14-1-6.
 */
public class Weather extends Thread {

    private Handler handler;


    public Weather(Handler handler) {
        this.handler = handler;

    }

    @Override
    public void run() {

        /*AQI*/

        String city = MyMaidSettingsHelper.getString(MyMaidSettingsHelper.WEATHER_CITY);

        Log.e(Defines.TAG, "AQI Details Thread START");

        if (!MyMaidSettingsHelper.getBoolean(MyMaidSettingsHelper.WEATHER_STATUS)) {
            Log.e(Defines.TAG, "AQI Disabled");
            return;
        }

        if (city == null || city.equals("")) {
            Log.e(Defines.TAG, "AQI City Wrong");
            MyMaidSettingsHelper.save(MyMaidSettingsHelper.WEATHER_STATUS, false);
            return;
        }

        String httpResult = null;
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

        AQIBean aqi = null;
        if (httpResult != null) {
            AQIDetailsBean b = new Gson().fromJson(httpResult, AQIDetailsBean.class);
            List<AQIBean> aqis = b.getAQIs();
            if (b.getError() == null && aqis.size() > 0 && aqis.get(0).getAQI() != null) {
//                new MyMaidNotificationHelper(MyMaidNotificationHelper.WEATHER, null, GlobalContext.getAppContext()).setAQI(aqis.get(0));
                aqi = aqis.get(0);
            } else if (b.getError() != null) {
                Log.e(Defines.TAG, b.getError());
                handler.sendEmptyMessage(Defines.GOT_AQI_INFO_FAIL);
                MyMaidSettingsHelper.save(MyMaidSettingsHelper.WEATHER_STATUS, false);
                return;
            }
        }




        /*WEATHER*/
        String cityID = CityIDs.getID(city);
        if (cityID == null)
            return;
        StringBuilder sb = new StringBuilder(URLHelper.WEATHER_NOW);
        sb.append(cityID).append(".html");
        try {
            httpResult = new HttpUtility().executeGetTask(sb.toString(), null);

            httpResult = httpResult.replace("{\"weatherinfo\":", "");
            httpResult = httpResult.substring(0, httpResult.length() - 1);
//            WeatherForecastBean weather = new Gson().fromJson(httpResult, WeatherForecastBean.class);
//            Log.e(TAG, weather.getWeather1() + weather.getIndex_uv());

            WeatherNowBean weather = new Gson().fromJson(httpResult, WeatherNowBean.class);

            new MyMaidNotificationHelper(MyMaidNotificationHelper.WEATHER, null, GlobalContext.getAppContext()).setWeather(aqi, weather);

   } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
