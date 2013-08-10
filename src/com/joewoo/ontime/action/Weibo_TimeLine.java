package com.joewoo.ontime.action;

import static com.joewoo.ontime.info.Defines.*;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.joewoo.ontime.info.WeiboConstant;

import android.os.Handler;
import android.util.Log;

public class Weibo_TimeLine extends Thread {

	private String count;
	private Handler mHandler;

	public Weibo_TimeLine(int count, Handler handler) {
		this.count = String.valueOf(count);
		this.mHandler = handler;
	}

	@Override
	public void run() {
		Log.e(TAG, "TimeLine Thread Start");
		String httpResult = "{ \"error_code\" : \"233\" }";

		HttpGet httpGet = new HttpGet(WeiboConstant.TIMELINE_URL
				+ "?access_token=" + WeiboConstant.ACCESS_TOKEN + "&count="
				+ count);

		try {

			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpGet);
			
			httpResult = EntityUtils.toString(httpResponse.getEntity());
			
			Log.e(TAG, "GOT Statues length: " + String.valueOf(httpResult.length()));

			mHandler.obtainMessage(GOT_FRIENDS_TIMELINE_INFO, httpResult)
					.sendToTarget();

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
