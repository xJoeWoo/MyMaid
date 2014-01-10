package com.joewoo.ontime.action.aqi;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.support.bean.WeatherBean;
import com.joewoo.ontime.support.net.HttpUtility;

import static com.joewoo.ontime.support.info.Defines.GOT_WEATHER_INFO;
import static com.joewoo.ontime.support.info.Defines.TAG;

public class Weather extends Thread {

    private Handler mHandler;
    private String httpResult;

    public Weather(Handler handler) {
        mHandler = handler;
    }

    public void run() {
        Log.e(TAG, "Weather Thread Start");
        try {
            httpResult = new HttpUtility().executeGetTask("http://m.weather.com.cn/data/101280901.html", null);

            Log.e(TAG, httpResult);
            httpResult = httpResult.replace("{\"weatherinfo\":", "");
            httpResult = httpResult.substring(0, httpResult.length() - 1);
            Gson gson = new Gson();
            WeatherBean weather = gson
                    .fromJson(httpResult, WeatherBean.class);
            mHandler.obtainMessage(GOT_WEATHER_INFO, weather).sendToTarget();
            Log.e(TAG, weather.getWeather1() + weather.getIndex_uv());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
