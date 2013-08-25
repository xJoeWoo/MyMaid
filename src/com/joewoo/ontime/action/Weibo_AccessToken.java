package com.joewoo.ontime.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.joewoo.ontime.bean.WeiboBackBean;
import com.joewoo.ontime.info.WeiboConstant;
import static com.joewoo.ontime.info.Defines.*;

import android.os.Handler;
import android.util.Log;

public class Weibo_AccessToken extends Thread {

	private Handler mHandler;

	public Weibo_AccessToken(Handler handler) {
		this.mHandler = handler;
	}

	public void run() {
		Log.e(TAG, "Request Access Token Thread Start");
		String httpResult = "NO_MESSAGES";
		HttpPost httpRequest = new HttpPost(TOKEN_URL);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("client_id", APP_KEY));
		params.add(new BasicNameValuePair("client_secret", APP_SECRET));
		params.add(new BasicNameValuePair("grant_type", "authorization_code"));
		params.add(new BasicNameValuePair("code", WeiboConstant.AUTH_CODE));
		params.add(new BasicNameValuePair("redirect_uri", CALLBACK_URL));
		try {
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				httpResult = EntityUtils.toString(httpResponse.getEntity());
				Log.e(TAG, "GOT: " + httpResult);

				Gson gson = new Gson();
				WeiboBackBean j = gson
						.fromJson(httpResult, WeiboBackBean.class);

				httpClient.getConnectionManager().shutdown();

				mHandler.obtainMessage(GOT_ACCESS_TOKEN, j).sendToTarget();

			}
		} catch (UnsupportedEncodingException e) {
			mHandler.obtainMessage(GOT_ACCESS_TOKEN_FAIL, httpResult)
					.sendToTarget();
			Log.e("OnTime --- ", "Thread Fail 1");
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			mHandler.obtainMessage(GOT_ACCESS_TOKEN_FAIL, httpResult)
					.sendToTarget();
			Log.e("OnTime --- ", "Thread Fail 2");
			e.printStackTrace();
		} catch (IOException e) {
			mHandler.obtainMessage(GOT_ACCESS_TOKEN_FAIL, httpResult)
					.sendToTarget();
			Log.e("OnTime --- ", "Thread Fail 3");
			e.printStackTrace();
		}
	}
};
