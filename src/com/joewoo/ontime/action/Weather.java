package com.joewoo.ontime.action;

import java.io.IOException;

import static com.joewoo.ontime.support.info.Defines.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.joewoo.ontime.support.bean.WeatherBean;

import android.os.Handler;
import android.util.Log;

public class Weather extends Thread {

    private Handler mHandler;
    private String httpResult;
    private int timeoutConnection = 10000;
    private int timeoutSocket = 10000;

    public Weather(Handler handler) {
        mHandler = handler;
    }

    public void run() {
        Log.e(TAG, "Weather Thread Start");
        HttpGet httpGet = new HttpGet(
                "http://m.weather.com.cn/data/101280901.html");
        try {
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            HttpResponse httpResponse = new DefaultHttpClient(httpParameters)
                    .execute(httpGet);
            httpResult = EntityUtils.toString(httpResponse.getEntity());
            Log.e(TAG, httpResult);
            httpResult = httpResult.replace("{\"weatherinfo\":", "");
            httpResult = httpResult.substring(0, httpResult.length() - 1);
            Gson gson = new Gson();
            WeatherBean weather = gson
                    .fromJson(httpResult, WeatherBean.class);
            mHandler.obtainMessage(GOT_WEATHER_INFO, weather).sendToTarget();
            Log.e(TAG, weather.getWeather1() + weather.getIndex_uv());

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
