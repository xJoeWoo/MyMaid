package com.joewoo.ontime.action;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.bean.WeiboBackBean;
import com.joewoo.ontime.info.WeiboConstant;
import static com.joewoo.ontime.info.Defines.*;

public class Weibo_UserShow extends Thread {

	private Handler mHandler;
	private String screenNameOrUid = null;
	private boolean isUid = false;

	public Weibo_UserShow(Handler handler) {
		this.mHandler = handler;
	}

	public Weibo_UserShow(boolean isUid, String screenNameOrUid, Handler handler) {
		this.isUid = isUid;
		this.screenNameOrUid = screenNameOrUid;
		this.mHandler = handler;
	}

	public void run() {
		Log.e(TAG, "Show User Info Thread Start");
		String httpResult = "{ \"error_code\" : \"233\" }";

		HttpUriRequest httpGet;
		
		if (screenNameOrUid == null)
			httpGet = new HttpGet(SHOW_URL + "?access_token="
					+ WeiboConstant.ACCESS_TOKEN + "&uid=" + WeiboConstant.UID);
		else if (!isUid)
			httpGet = new HttpGet(SHOW_URL + "?access_token="
					+ WeiboConstant.ACCESS_TOKEN + "&screen_name=" + screenNameOrUid);
		else
			httpGet = new HttpGet(SHOW_URL + "?access_token="
					+ WeiboConstant.ACCESS_TOKEN + "&uid=" + screenNameOrUid);

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

			Gson gson = new Gson();
			WeiboBackBean show = gson.fromJson(httpResult, WeiboBackBean.class);

			mHandler.obtainMessage(GOT_SHOW_INFO, show).sendToTarget();

		} catch (Exception e) {
			mHandler.obtainMessage(GOT_SHOW_INFO_FAIL, httpResult)
					.sendToTarget();
		}
	}
}
