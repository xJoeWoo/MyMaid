package com.joewoo.ontime.action;

import static com.joewoo.ontime.info.Defines.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.joewoo.ontime.R;
import com.joewoo.ontime.bean.AtSuggestionBean;
import com.joewoo.ontime.info.WeiboConstant;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;

public class Weibo_At_Suggestions extends Thread {

	private String user;
	private Handler mHandler;
	private Context context;

	public Weibo_At_Suggestions(String user, Context context, Handler handler) {
		this.user = user;
		this.context = context;
		this.mHandler = handler;
	}

	public void run() {
		Log.e(TAG, "At User Suggestions Thread Start");
		String httpResult = "{ \"error_code\" : \"233\" }";

		HttpGet httpGet = new HttpGet(WeiboConstant.AT_SUGGESTIONS_URL
				+ "?access_token=" + WeiboConstant.ACCESS_TOKEN + "&q=" + user
				+ "&type=0");

		try {
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpGet);

			httpResult = EntityUtils.toString(httpResponse.getEntity());

			Log.e(TAG, "GOT: " + httpResult);

			Gson gson = new Gson();

			// ===========================SimpleAdapter==========================================
			// Json_At_Suggestions[] obj = gson.fromJson(httpResult,
			// Json_At_Suggestions[].class);
			//
			// List<HashMap<String, String>> fillMaps = new
			// ArrayList<HashMap<String, String>>();
			// for (int i = 0; i < obj.length; i++) {
			// HashMap<String, String> map = new HashMap<String, String>();
			// map.put(NICKNAME, obj[i].getNickname());
			// // map.put(UID, obj[i].getUid());
			// fillMaps.add(map);
			// }
			//
			// // SimpleAdapter sa = new SimpleAdapter(context, fillMaps,
			// // android.R.layout.simple_list_item_2, new String[] {
			// // NICKNAME, UID }, new int[] { android.R.id.text1,
			// // android.R.id.text2 });
			//
			// SimpleAdapter sa = new SimpleAdapter(context, fillMaps,
			// R.layout.at_lv, new String[] {
			// NICKNAME }, new int[] {R.id.lv_tv1 });
			// mHandler.obtainMessage(GOT_AT_SUGGESTIONS_INFO, sa)
			// .sendToTarget();

			// ===================================================================================

			Type listType = new TypeToken<List<AtSuggestionBean>>() {
			}.getType();

			List<AtSuggestionBean> events = gson.fromJson(httpResult,
					listType);

			for (int i = 0; i < events.size(); i++) {
				Log.e(TAG, events.get(i).getNickname());
			}

			ArrayAdapter<AtSuggestionBean> files = new ArrayAdapter<AtSuggestionBean>(
					context, R.layout.at_lv, R.id.lv_tv1, events);

			mHandler.obtainMessage(GOT_AT_SUGGESTIONS_INFO, files).sendToTarget();

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
