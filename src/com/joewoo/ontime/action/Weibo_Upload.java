package com.joewoo.ontime.action;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.joewoo.ontime.bean.WeiboBackBean;
import com.joewoo.ontime.info.WeiboConstant;
import com.joewoo.ontime.tools.CustomMultipartEntity;
import com.joewoo.ontime.tools.CustomMultipartEntity.ProgressListener;
import static com.joewoo.ontime.info.Defines.*;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

public class Weibo_Upload extends AsyncTask<String, Integer, String> {

	private String status;
	private File file;
	private ProgressBar pb;
	private long totalSize;
	private Handler mHandler;
	

	public Weibo_Upload(String status, File file, ProgressBar pb, Handler handler) {
		this.status = status;
		this.file = file;
		this.pb = pb;
		this.mHandler = handler;
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected String doInBackground(String... params) {
		Log.e(TAG, "Upload Weibo Thread Start");
		String httpResult = "{ \"error_code\" : \"233\" }";

		HttpClient httpClient = new DefaultHttpClient();
		HttpContext httpContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(UPLOAD_URL);

		CustomMultipartEntity multipartContent = new CustomMultipartEntity(
				new ProgressListener() {
					@Override
					public void transferred(long num) {
						publishProgress((int) ((num / (float) totalSize) * 100));
					}
				});

		try {
			multipartContent.addPart(PIC, new FileBody(file));
			multipartContent.addPart(ACCESS_TOKEN, new StringBody(
					WeiboConstant.ACCESS_TOKEN));
			multipartContent.addPart(STATUS, new StringBody(status));
			
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		totalSize = multipartContent.getContentLength();

		try {
			
			httpPost.setEntity(multipartContent);
			HttpResponse httpResponse = httpClient.execute(
					httpPost, httpContext);
			httpResult = EntityUtils.toString(httpResponse.getEntity());
			
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return httpResult;
	}
	
	@Override
	protected void onProgressUpdate(Integer... progress) {
		pb.setProgress((int) (progress[0]));
	}

	@Override
	protected void onPostExecute(String result) {
		Log.e(TAG, result);
		Gson gson = new Gson();
		WeiboBackBean j = gson.fromJson(result, WeiboBackBean.class);
		
		mHandler.obtainMessage(GOT_UPLOAD_INFO, j).sendToTarget();
	}
	
	@Override
	protected void onCancelled() {
	}
	
}
