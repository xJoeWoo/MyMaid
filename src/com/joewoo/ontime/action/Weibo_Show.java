package com.joewoo.ontime.action;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.bean.WeiboBackBean;
import com.joewoo.ontime.info.WeiboConstant;
import static com.joewoo.ontime.info.Defines.*;

public class Weibo_Show extends Thread {

	private Handler mHandler;

	public Weibo_Show(Handler handler) {
		this.mHandler = handler;
	}

	public void run() {
		Log.e(TAG, "Show User Info Thread Start");
		String httpResult = "NO_MESSAGES";

		HttpGet httpGet = new HttpGet(WeiboConstant.SHOW_URL + "?access_token="
				+ WeiboConstant.ACCESS_TOKEN + "&uid=" + WeiboConstant.UID);
		try {
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				httpResult = EntityUtils.toString(httpResponse.getEntity());
				Log.e(TAG, "GOT: " + httpResult);

				Gson gson = new Gson();
				WeiboBackBean show = gson.fromJson(httpResult, WeiboBackBean.class);

				mHandler.obtainMessage(GOT_SHOW_INFO, show).sendToTarget();

			}
		} catch (UnsupportedEncodingException e) {
			mHandler.obtainMessage(GOT_SHOW_INFO_FAIL, httpResult)
					.sendToTarget();
			Log.e("OnTime --- ", "Thread Fail 1");
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			mHandler.obtainMessage(GOT_SHOW_INFO_FAIL, httpResult)
					.sendToTarget();
			Log.e("OnTime --- ", "Thread Fail 2");
			e.printStackTrace();
		} catch (IOException e) {
			mHandler.obtainMessage(GOT_SHOW_INFO_FAIL, httpResult)
					.sendToTarget();
			Log.e("OnTime --- ", "Thread Fail 3");
			e.printStackTrace();
		}
	}
}
