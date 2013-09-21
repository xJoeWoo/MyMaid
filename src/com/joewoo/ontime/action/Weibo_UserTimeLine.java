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
import com.joewoo.ontime.bean.StatusesBean;
import com.joewoo.ontime.bean.UserTimeLineBean;
import com.joewoo.ontime.info.WeiboConstant;
import android.os.Handler;
import android.util.Log;

public class Weibo_UserTimeLine extends Thread {

	private String count;
	private Handler mHandler;
	private String screenNameOfUid;
	private boolean isUid;

	public Weibo_UserTimeLine(boolean isUid, String screenNameOfUid, int count,
			Handler handler) {
		this.isUid = isUid;
		this.screenNameOfUid = screenNameOfUid;
		this.count = String.valueOf(count);
		this.mHandler = handler;
	}

	public void run() {
		Log.e(TAG, "User Time Line Thread Start");
		String httpResult = "{ \"error_code\" : \"233\" }";

		HttpUriRequest httpGet;

		// httpGet = new HttpGet(USER_TIMELINE_URL + "?access_token="
		// + WeiboConstant.ACCESS_TOKEN + "&screen_name=" + screenName
		// + "&count=" + count + "&trim_user=1");

		if (!isUid)
			httpGet = new HttpGet(USER_TIMELINE_URL + "?access_token="
					+ WeiboConstant.ACCESS_TOKEN + "&screen_name="
					+ screenNameOfUid + "&count=" + count);
		else
			httpGet = new HttpGet(USER_TIMELINE_URL + "?access_token="
					+ WeiboConstant.ACCESS_TOKEN + "&uid=" + screenNameOfUid
					+ "&count=" + count);

		httpGet.addHeader("Accept-Encoding", "gzip");

		try {

			InputStream is = new DefaultHttpClient().execute(httpGet)
					.getEntity().getContent();

			is = new GZIPInputStream(is);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			int ii = -1;
			while ((ii = is.read()) != -1) {
				baos.write(ii);
			}

			httpResult = baos.toString();

			Log.e(TAG,
					"GOT Statues length: "
							+ String.valueOf(httpResult.length()));
			Log.e(TAG, httpResult);

		} catch (Exception e) {
			Log.e(TAG, "User Time Line Thread Network Failed");
			mHandler.sendEmptyMessage(GOT_USER_TIMELINE_INFO_FAIL);
		}

		try {
			UserTimeLineBean timeline = new Gson().fromJson(httpResult,
					UserTimeLineBean.class);

			ArrayList<HashMap<String, String>> text = new ArrayList<HashMap<String, String>>();

			List<StatusesBean> statuses = timeline.getStatuses();

			String source;
			String rt_source;

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
				map.put(UID, statuses.get(i).getUser().getId());
				map.put(SCREEN_NAME, statuses.get(i).getUser().getScreenName());
				map.put(TEXT, statuses.get(i).getText());
				map.put(COMMENTS_COUNT, statuses.get(i).getCommentsCount());
				map.put(REPOSTS_COUNT, statuses.get(i).getRepostsCount());
				map.put(WEIBO_ID, statuses.get(i).getId());
				map.put(PROFILE_IMAGE_URL, statuses.get(i).getUser()
						.getProfileImageUrl());

				try {

					map.put(RETWEETED_STATUS_UID, statuses.get(i)
							.getRetweetedStatus().getUser().getId());
					rt_source = statuses.get(i).getRetweetedStatus()
							.getSource();
					rt_source = rt_source.substring(rt_source.indexOf(">") + 1,
							rt_source.length());
					rt_source = rt_source.substring(0, rt_source.indexOf("<"));
					map.put(RETWEETED_STATUS_SOURCE, " · " + rt_source);
					rt_source = statuses.get(i).getRetweetedStatus()
							.getCreatedAt();
					rt_source = rt_source.substring(rt_source.indexOf(":") - 2,
							rt_source.indexOf(":") + 3);
					map.put(RETWEETED_STATUS_CREATED_AT, rt_source);

					map.put(RETWEETED_STATUS_COMMENTS_COUNT, statuses.get(i)
							.getRetweetedStatus().getCommentsCount());
					map.put(RETWEETED_STATUS_REPOSTS_COUNT, statuses.get(i)
							.getRetweetedStatus().getRepostsCount());
					map.put(RETWEETED_STATUS_SCREEN_NAME, statuses.get(i)
							.getRetweetedStatus().getUser().getScreenName());
					map.put(RETWEETED_STATUS, statuses.get(i)
							.getRetweetedStatus().getText());

					if (statuses.get(i).getRetweetedStatus().getThumbnailPic() != null) {
						map.put(RETWEETED_STATUS_THUMBNAIL_PIC, statuses.get(i)
								.getRetweetedStatus().getThumbnailPic());
						map.put(RETWEETED_STATUS_BMIDDLE_PIC, statuses.get(i)
								.getRetweetedStatus().getBmiddlePic());
					}
					map.put(IS_REPOST, " ");

				} catch (Exception e) {

				}

				if (statuses.get(i).getThumbnailPic() != null) {
					map.put(THUMBNAIL_PIC, statuses.get(i).getThumbnailPic());
					map.put(BMIDDLE_PIC, statuses.get(i).getBmiddlePic());
				}
				text.add(map);
			}

			mHandler.obtainMessage(GOT_USER_TIMELINE_INFO, text).sendToTarget();

		} catch (Exception e) {
			mHandler.sendEmptyMessage(GOT_USER_TIMELINE_INFO_FAIL);
		}

	}
}
