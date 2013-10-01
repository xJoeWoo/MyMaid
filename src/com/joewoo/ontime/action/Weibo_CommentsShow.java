package com.joewoo.ontime.action;

import static com.joewoo.ontime.info.Defines.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.joewoo.ontime.bean.CommentsBean;
import com.joewoo.ontime.bean.CommentsToMeBean;
import com.joewoo.ontime.info.WeiboConstant;

import android.os.Handler;
import android.util.Log;

public class Weibo_CommentsShow extends Thread {

	private Handler mHandler;
	private String weibo_id;
	private String max_id = null;

	public Weibo_CommentsShow(String weibo_id, Handler handler) {
		this.mHandler = handler;
		this.weibo_id = weibo_id;
	}

	public Weibo_CommentsShow(String weibo_id, String max_id, Handler handler) {
		this.mHandler = handler;
		this.weibo_id = weibo_id;
		this.max_id = max_id;
	}

	public void run() {

		String httpResult = "{ \"error_code\" : \"233\" }";
		Log.e(TAG, "Comments Show Thread Start");

		HttpUriRequest httpGet;
		if (max_id == null) {

			httpGet = new HttpGet(COMMENTS_SHOW_URL + "?access_token="
					+ WeiboConstant.ACCESS_TOKEN + "&id=" + weibo_id);
		} else {
			httpGet = new HttpGet(COMMENTS_SHOW_URL + "?access_token="
					+ WeiboConstant.ACCESS_TOKEN + "&id=" + weibo_id
					+ "&max_id=" + max_id);
		}
		httpGet.addHeader("Accept-Encoding", "gzip");

		try {

			InputStream is = new DefaultHttpClient().execute(httpGet)
					.getEntity().getContent();

			is = new GZIPInputStream(is);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			int i = -1;
			while ((i = is.read()) != -1) {
				baos.write(i);
			}

			httpResult = baos.toString();

			Log.e(TAG,
					"GOT Statues length: "
							+ String.valueOf(httpResult.length()));
			Log.e(TAG, httpResult);
		} catch (Exception e) {
			mHandler.sendEmptyMessage(GOT_COMMNETS_SHOW_INFO_FAIL);
		}

		try {

			CommentsToMeBean commentsToMe = new Gson().fromJson(httpResult,
					CommentsToMeBean.class);

			List<CommentsBean> comments = commentsToMe.getComments();

			ArrayList<HashMap<String, String>> text = new ArrayList<HashMap<String, String>>();

			for (int i = 0; i < comments.size(); i++) {
				HashMap<String, String> map = new HashMap<String, String>();

				map.put(SCREEN_NAME, comments.get(i).getUser().getScreenName());
				map.put(TEXT, comments.get(i).getText());
				map.put(COMMENT_ID, comments.get(i).getId());
//				map.put(WEIBO_ID, comments.get(i).getStatus().getId());

				text.add(map);
			}

			mHandler.obtainMessage(GOT_COMMNETS_SHOW_INFO, text)
					.sendToTarget();

		} catch (Exception e) {
			mHandler.sendEmptyMessage(GOT_COMMNETS_SHOW_INFO_FAIL);
		}
	}
}
