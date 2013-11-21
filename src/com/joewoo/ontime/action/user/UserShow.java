package com.joewoo.ontime.action.user;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.WeiboBackBean;
import com.joewoo.ontime.support.info.Constants;
import com.joewoo.ontime.support.error.ErrorCheck;

import static com.joewoo.ontime.support.info.Defines.*;

public class UserShow extends Thread {

    private Handler mHandler;
    private String screenNameOrUid = null;
    private boolean isUid = false;

    public UserShow(Handler handler) {
        this.mHandler = handler;
    }

    public UserShow(String screenNameOrUid, Handler handler) {
        this.isUid = isUid;
        this.screenNameOrUid = screenNameOrUid;
        this.mHandler = handler;
    }

    public void run() {
        Log.e(TAG, "Show User Info Thread START");
        String httpResult = "{ \"error_code\" : \"233\" }";

        HttpUriRequest httpGet;

        if (screenNameOrUid == null)
            httpGet = new HttpGet(URLHelper.USER_SHOW + "?access_token="
                    + Constants.ACCESS_TOKEN + "&uid=" + Constants.UID);
        else if (!isUid)
            httpGet = new HttpGet(URLHelper.USER_SHOW + "?access_token="
                    + Constants.ACCESS_TOKEN + "&screen_name=" + screenNameOrUid);
        else
            httpGet = new HttpGet(URLHelper.USER_SHOW + "?access_token="
                    + Constants.ACCESS_TOKEN + "&uid=" + screenNameOrUid);

        httpGet.addHeader("Accept-Encoding", "gzip");
        try {
            // HttpResponse httpResponse = new DefaultHttpClient()
            // .execute(httpGet);

            InputStream is = new DefaultHttpClient().execute(httpGet)
                    .getEntity().getContent();

            is = new GZIPInputStream(is);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int i = -1;
            while ((i = is.read()) != -1) {
                baos.write(i);
            }

            httpResult = baos.toString();
            Log.e(TAG, httpResult);
            is.close();
            baos.close();
        } catch (Exception e) {
            mHandler.sendEmptyMessage(GOT_SHOW_INFO_FAIL);
            e.printStackTrace();
        }

        if (ErrorCheck.getError(httpResult) == null) {
            mHandler.obtainMessage(GOT_SHOW_INFO, new Gson().fromJson(httpResult, WeiboBackBean.class))
                    .sendToTarget();
        } else {
            mHandler.obtainMessage(GOT_SHOW_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();
        }


    }
}
