package com.joewoo.ontime.action;

import static com.joewoo.ontime.info.Defines.*;

import java.io.IOException;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.joewoo.ontime.bean.UnreadCountBean;
import com.joewoo.ontime.info.WeiboConstant;

import android.os.Handler;
import android.util.Log;

public class Weibo_UnreadCount extends Thread {

	private Handler mHandler;
	private String httpResult = "{ \"error_code\":\"233\"";

	public Weibo_UnreadCount(Handler handler) {
		this.mHandler = handler;
	}

	public void run() {
		Log.e(TAG, "Unread Count Thread Start");
		
		HttpGet httpGet = new HttpGet(UNREAD_COUNT_URL + "?access_token="
				+ WeiboConstant.ACCESS_TOKEN + "&uid=" + WeiboConstant.UID);

		try {
			httpResult = EntityUtils.toString(new DefaultHttpClient().execute(
					httpGet).getEntity());

			Log.e(TAG, "GOT: " + httpResult);

			mHandler.obtainMessage(GOT_UNREAD_COUNT_INFO,
					new Gson().fromJson(httpResult, UnreadCountBean.class))
					.sendToTarget();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
