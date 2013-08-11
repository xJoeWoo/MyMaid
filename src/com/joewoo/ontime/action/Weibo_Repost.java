package com.joewoo.ontime.action;

import static com.joewoo.ontime.info.Defines.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.bean.WeiboBackBean;
import com.joewoo.ontime.info.WeiboConstant;

public class Weibo_Repost extends Thread {
	private String weibo_id;
	private Handler mHandler;
	private String status;
	private boolean is_comment = false;

	public Weibo_Repost(String status, String weibo_id, Handler handler) {
		this.weibo_id = weibo_id;
		this.mHandler = handler;
		this.status = status;
	}

	public Weibo_Repost(String comment, String weibo_id, boolean is_comment,
			Handler handler) {
		this.weibo_id = weibo_id;
		this.mHandler = handler;
		this.status = comment;
		this.is_comment = is_comment;
	}

	public void run() {
		Log.e(TAG, "Repost Thread start");
		String httpResult = "{ \"error_code\" : \"233\" }";

		HttpPost httpRequest = new HttpPost(REPOST_URL);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(ACCESS_TOKEN,
				WeiboConstant.ACCESS_TOKEN));
		params.add(new BasicNameValuePair("id", weibo_id));
		params.add(new BasicNameValuePair("status", status));
		if (is_comment) {
			params.add(new BasicNameValuePair("is_comment", "1"));
		}
		try {
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);

			httpResult = EntityUtils.toString(httpResponse.getEntity());
			Log.e(TAG, "GOT: " + httpResult);

			Gson gson = new Gson();
			WeiboBackBean b = gson.fromJson(httpResult, WeiboBackBean.class);

			mHandler.obtainMessage(GOT_REPOST_INFO, b).sendToTarget();

		} catch (UnsupportedEncodingException e) {

		} catch (ClientProtocolException e) {

		} catch (IOException e) {

		}
	}
}
