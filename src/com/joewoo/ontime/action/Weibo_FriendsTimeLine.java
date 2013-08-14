package com.joewoo.ontime.action;

import static com.joewoo.ontime.info.Defines.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.joewoo.ontime.bean.FriendsTimeLineBean;
import com.joewoo.ontime.bean.StatusesBean;
import com.joewoo.ontime.info.WeiboConstant;

import android.os.Handler;
import android.util.Log;

public class Weibo_FriendsTimeLine extends Thread {

	private String count;
	private Handler mHandler;

	public Weibo_FriendsTimeLine(int count, Handler handler) {
		this.count = String.valueOf(count);
		this.mHandler = handler;
	}

	@Override
	public void run() {
		Log.e(TAG, "TimeLine Thread Start");
		String httpResult = "{ \"error_code\" : \"233\" }";

		HttpGet httpGet = new HttpGet(TIMELINE_URL
				+ "?access_token=" + WeiboConstant.ACCESS_TOKEN + "&count="
				+ count);

		try {

			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpGet);

			httpResult = EntityUtils.toString(httpResponse.getEntity());

			Log.e(TAG,
					"GOT Statues length: "
							+ String.valueOf(httpResult.length()));

			Gson gson = new Gson();

			FriendsTimeLineBean timeline = gson.fromJson(httpResult,
					FriendsTimeLineBean.class);

			List<StatusesBean> statuses = timeline.getStatuses();

			ArrayList<HashMap<String, String>> text = new ArrayList<HashMap<String, String>>();

			for (int i = 0; i < statuses.size(); i++) {
				HashMap<String, String> map = new HashMap<String, String>();
				String source = statuses.get(i).getSource();
				source = source.substring(source.indexOf(">") + 1,
						source.length());
				source = source.substring(0, source.indexOf("<"));
				map.put(SOURCE, source);
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
				if (statuses.get(i).getRetweetedStatus() != null) {
					map.put(IS_REPOST, " ");
					String created_at;
					created_at = statuses.get(i).getRetweetedStatus().getCreatedAt();
					created_at = created_at.substring(created_at.indexOf(":") - 2,
							created_at.indexOf(":") + 3);
					map.put(RETWEETED_STATUS_CREATED_AT, created_at);
					map.put(RETWEETED_STATUS_COMMENTS_COUNT, statuses.get(i).getRetweetedStatus().getCommentsCount());
					map.put(RETWEETED_STATUS_REPOSTS_COUNT, statuses.get(i).getRetweetedStatus().getRepostsCount());
					map.put(RETWEETED_STATUS_SCREEN_NAME, statuses.get(i).getRetweetedStatus().getUser().getScreenName());
					map.put(RETWEETED_STATUS, statuses.get(i).getRetweetedStatus().getText());
					if(statuses.get(i).getRetweetedStatus().getThumbnailPic() != null)
					{
						map.put(HAVE_PIC, " ");
					}
					
				}
				if(statuses.get(i).getThumbnailPic() != null)
				{
//					map.put(HAVE_PIC, statuses.get(i).getThumbnailPic());
					map.put(HAVE_PIC, " ");
				}

				text.add(map);
			}

			

			mHandler.obtainMessage(GOT_FRIENDS_TIMELINE_INFO, text)
					.sendToTarget();

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
