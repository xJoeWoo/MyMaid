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

		// try {
		// URL url = new URL(WeiboConstant.TIMELINE_URL + "?access_token="
		// + WeiboConstant.ACCESS_TOKEN + "&count=" + count);
		//
		// HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// conn.setConnectTimeout(CONNECT_TIMEOUT);
		// conn.setRequestMethod("GET");
		//
		// if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
		//
		//
		// InputStream is = conn.getInputStream();
		//
		// if (null != is && "gzip".equals(conn.getContentEncoding())) {
		// // the response is gzipped
		// is = new GZIPInputStream(is);
		// Log.e(TAG, "GZIP");
		// }
		//
		// BufferedReader in = new BufferedReader(new InputStreamReader(is));
		// StringBuffer buffer = new StringBuffer();
		// String line = "";
		// try {
		// while ((line = in.readLine()) != null){
		// buffer.append(line);
		// }
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// // int i = -1;
		// //
		// // ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// //
		// // while((i=is.read()) != -1)
		// // {
		// // baos.write(i);
		// // }
		// //
		// // httpResult = baos.toString();
		// Log.e(TAG, "GOT: " + buffer.toString());
		// //
		//

		//
		// }
		//
		//
		// } catch (ClientProtocolException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// Gson gson = new Gson();
		// List<FriendsTimelineBean> list = gson.fromJson(httpResult,
		// new TypeToken<List<FriendsTimelineBean>>() {
		// }.getType());
		//
		// for (int i = 0; i < list.size(); i++) {
		// Log.e(TAG, "No. " + String.valueOf(i));
		// Log.e(TAG, list.get(i).getStatuses());
		// Log.e(TAG, LOG_DEVIDER);
		// }

	}

}
