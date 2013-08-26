package com.joewoo.ontime.action;

import static com.joewoo.ontime.info.Defines.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.bean.MentionsBean;
import com.joewoo.ontime.bean.StatusesBean;
import com.joewoo.ontime.info.WeiboConstant;
import com.joewoo.ontime.tools.MySQLHelper;

public class Weibo_Mentions extends Thread {

	private String count;
	private Handler mHandler;
	public boolean isProvidedResult = false;
	private String httpResult = "{ \"error_code\" : \"233\" }";
	private MySQLHelper sqlHelper;

	public Weibo_Mentions(int count, Handler handler) {
		this.count = String.valueOf(count);
		this.mHandler = handler;
	}

	public Weibo_Mentions(int count, MySQLHelper sqlHelper, Handler handler) {
		this.count = String.valueOf(count);
		this.mHandler = handler;
		this.sqlHelper = sqlHelper;
	}

	public Weibo_Mentions(String httpResult, MySQLHelper sqlHelper,
			Handler handler) {
		this.mHandler = handler;
		this.httpResult = httpResult;
		isProvidedResult = true;
		this.sqlHelper = sqlHelper;
	}

	@Override
	public void run() {
		Log.e(TAG, "Mentions Thread Start");

		if (!isProvidedResult) {

			HttpUriRequest httpGet = new HttpGet(MENTIONS_URL
					+ "?access_token=" + WeiboConstant.ACCESS_TOKEN + "&count="
					+ count);
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

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {

			MentionsBean mentions = new Gson().fromJson(httpResult,
					MentionsBean.class);

			List<StatusesBean> statuses = mentions.getStatuses();
			ArrayList<HashMap<String, String>> text = new ArrayList<HashMap<String, String>>();

			String source;

			for (int i = 0; i < statuses.size(); i++) {
				HashMap<String, String> map = new HashMap<String, String>();
				source = statuses.get(i).getSource();
				source = source.substring(source.indexOf(">") + 1,
						source.length());
				source = source.substring(0, source.indexOf("<"));
				map.put(SOURCE, " · " + source);
				source = statuses.get(i).getCreatedAt();
				source = source.substring(source.indexOf(":") - 2,
						source.indexOf(":") + 3);
				map.put(CREATED_AT, source);
				map.put(SCREEN_NAME, statuses.get(i).getUser().getScreenName());
				map.put(TEXT, statuses.get(i).getText());
				map.put(WEIBO_ID, statuses.get(i).getId());
				map.put(COMMENTS_COUNT, statuses.get(i).getCommentsCount());
				map.put(REPOSTS_COUNT, statuses.get(i).getRepostsCount());
				map.put(UID, statuses.get(i).getUser().getId());

				if (statuses.get(i).getRetweetedStatus() != null) {
					map.put(IS_REPOST, " ");
					map.put(UID, statuses.get(i).getRetweetedStatus().getUser()
							.getId());
					map.put(RETWEETED_STATUS_SCREEN_NAME, statuses.get(i)
							.getRetweetedStatus().getUser().getScreenName());
					map.put(RETWEETED_STATUS, statuses.get(i)
							.getRetweetedStatus().getText());
					if (statuses.get(i).getRetweetedStatus().getThumbnailPic() != null) {
						map.put(HAVE_PIC, " ");
					}

				}

				if (statuses.get(i).getThumbnailPic() != null) {
					map.put(HAVE_PIC, " ");
				}

				text.add(map);
			}

			mHandler.obtainMessage(GOT_MENTIONS_INFO, text).sendToTarget();

			if (sqlHelper != null && !isProvidedResult) {
				SQLiteDatabase sql = sqlHelper.getWritableDatabase();

				ContentValues cv = new ContentValues();
				cv.put(sqlHelper.MENTIONS, httpResult);
				if (sql.update(sqlHelper.tableName, cv, sqlHelper.UID + "='"
						+ WeiboConstant.UID + "'", null) != 0) {
					Log.e(TAG_SQL, "Saved Mentions httpResult");
				}
			}

		} catch (Exception e) {
			mHandler.sendEmptyMessage(GOT_MENTIONS_INFO_FAIL);
		}
	}

}
