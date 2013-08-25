package com.joewoo.ontime.action;

import static com.joewoo.ontime.info.Defines.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.joewoo.ontime.bean.WeiboBackBean;
import com.joewoo.ontime.info.WeiboConstant;

import android.os.Handler;
import android.util.Log;

public class Weibo_Reply extends Thread {

	private String comment;
	private String weibo_id;
	private String comment_id;
	private Handler mHandler;
	private boolean comment_ori;
	
	public Weibo_Reply(String comment, String weibo_id, String comment_id,
			Handler handler) {
		this.comment = comment;
		this.weibo_id = weibo_id;
		this.comment_id = comment_id;
		this.mHandler = handler;
	}
	
	public Weibo_Reply( String comment, String weibo_id, String comment_id, boolean comment_ori,
			Handler handler) {
		this.comment = comment;
		this.weibo_id = weibo_id;
		this.comment_id = comment_id;
		this.mHandler = handler;
		this.comment_ori = comment_ori;
	}
	
	public void run(){
		Log.e(TAG, "Comment Reply Thread start");
		String httpResult = "{ \"error_code\" : \"233\" }";

		HttpPost httpRequest = new HttpPost(REPLY_URL);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(ACCESS_TOKEN,
				WeiboConstant.ACCESS_TOKEN));
		params.add(new BasicNameValuePair("id", weibo_id));
		params.add(new BasicNameValuePair("comment", comment));
		params.add(new BasicNameValuePair("cid", comment_id));
		if (comment_ori) {
			params.add(new BasicNameValuePair("comment_ori", "1"));
		}
		try {
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			httpResult = EntityUtils.toString(new DefaultHttpClient()
			.execute(httpRequest).getEntity());
			Log.e(TAG, "GOT: " + httpResult);


			mHandler.obtainMessage(GOT_REPLY_INFO, new Gson().fromJson(httpResult, WeiboBackBean.class)).sendToTarget(); 

		} catch (UnsupportedEncodingException e) {

		} catch (ClientProtocolException e) {

		} catch (IOException e) {

		}
	}

}
