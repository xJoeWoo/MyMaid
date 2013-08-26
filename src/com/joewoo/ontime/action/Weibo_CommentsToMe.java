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
import com.joewoo.ontime.bean.CommentsBean;
import com.joewoo.ontime.bean.CommentsToMeBean;
import com.joewoo.ontime.info.WeiboConstant;
import com.joewoo.ontime.tools.MySQLHelper;

public class Weibo_CommentsToMe extends Thread {

	private String count;
	private Handler mHandler;
	public boolean isProvidedResult = false;
	private String httpResult = "{ \"error_code\" : \"233\" }";
	private MySQLHelper sqlHelper;

	public Weibo_CommentsToMe(int count, Handler handler) {
		this.count = String.valueOf(count);
		this.mHandler = handler;
	}

	public Weibo_CommentsToMe(int count, MySQLHelper sqlHelper, Handler handler) {
		this.count = String.valueOf(count);
		this.mHandler = handler;
		this.sqlHelper = sqlHelper;
	}

	public Weibo_CommentsToMe(String httpResult, MySQLHelper sqlHelper,
			Handler handler) {
		this.mHandler = handler;
		this.httpResult = httpResult;
		isProvidedResult = true;
		this.sqlHelper = sqlHelper;
	}

	@Override
	public void run() {
		Log.e(TAG, "Comments To Me Thread Start");

		if (!isProvidedResult) {

			HttpUriRequest httpGet = new HttpGet(COMMENTS_TO_ME_URL
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

			CommentsToMeBean commentsToMe = new Gson().fromJson(httpResult,
					CommentsToMeBean.class);

			List<CommentsBean> comments = commentsToMe.getComments();

			ArrayList<HashMap<String, String>> text = new ArrayList<HashMap<String, String>>();

			String source;

			for (int i = 0; i < comments.size(); i++) {
				HashMap<String, String> map = new HashMap<String, String>();
				source = comments.get(i).getSource();
				source = source.substring(source.indexOf(">") + 1,
						source.length());
				source = source.substring(0, source.indexOf("<"));
				map.put(SOURCE, " · " + source);
				source = comments.get(i).getCreatedAt();
				source = source.substring(source.indexOf(":") - 2,
						source.indexOf(":") + 3);
				map.put(CREATED_AT, source);
				// map.put(UID, comments.get(i).getUser().getId());
				map.put(SCREEN_NAME, comments.get(i).getUser().getScreenName());
				map.put(TEXT, comments.get(i).getText());
				map.put(COMMENT_ID, comments.get(i).getId());
				map.put(WEIBO_ID, comments.get(i).getStatus().getId());

				if (comments.get(i).getReplyComment() != null) {
					map.put(STATUS_USER_SCREEN_NAME, comments.get(i)
							.getReplyComment().getUser().getScreenName());
					map.put(STATUS_TEXT, comments.get(i).getReplyComment()
							.getText());
				} else {
					map.put(STATUS_USER_SCREEN_NAME, comments.get(i)
							.getStatus().getUser().getScreenName());
					map.put(STATUS_TEXT, comments.get(i).getStatus().getText());
				}
				// map.put(STATUS_COMMENTS_COUNT,
				// comments.get(i).getStatus()
				// .getCommentsCount());
				// map.put(STATUS_REPOSTS_COUNT, comments.get(i).getStatus()
				// .getRepostsCount());

				text.add(map);
			}

			mHandler.obtainMessage(GOT_COMMENTS_TO_ME_INFO, text)
					.sendToTarget();
			
			if (sqlHelper != null && !isProvidedResult) {
				SQLiteDatabase sql = sqlHelper.getWritableDatabase();

				ContentValues cv = new ContentValues();
				cv.put(sqlHelper.TO_ME_COMMENTS, httpResult);
				if (sql.update(sqlHelper.tableName, cv, sqlHelper.UID + "='"
						+ WeiboConstant.UID + "'", null) != 0) {
					Log.e(TAG_SQL, "Saved Comments httpResult");
				}
			}

		} catch (Exception e) {
			mHandler.sendEmptyMessage(GOT_COMMENTS_TO_ME_INFO_FAIL);
		}
	}
}
