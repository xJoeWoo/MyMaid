package com.joewoo.ontime.time;

import static com.joewoo.ontime.info.Defines.*;

import java.util.Calendar;

import com.joewoo.ontime.R;
import com.joewoo.ontime.action.Weibo_Update;
import com.joewoo.ontime.bean.WeiboBackBean;
import com.joewoo.ontime.info.WeiboConstant;
import com.joewoo.ontime.tools.Id2MidUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class Getup_Noon extends Activity {

	TextView tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.getup);

		ConnectivityManager cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cManager.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isAvailable()) {

		} else {

		}

		SharedPreferences preferences;
		preferences = getSharedPreferences("3220385287", MODE_PRIVATE);

		tv = (TextView) findViewById(R.id.tv_getup);

		WeiboConstant.ACCESS_TOKEN = preferences.getString(ACCESS_TOKEN, null);
		WeiboConstant.UID = preferences.getString(UID, null);
		WeiboConstant.EXPIRES_IN = preferences.getLong(EXPIRES_IN, 0);
		
		Calendar c = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c2.clear();
		c2.set(2014, (6 - 1), 7);

		long leftTime = (c2.getTimeInMillis() - c.getTimeInMillis()) / 1000;
		int leftDays = (int) (leftTime / (24 * 60 * 60));
		
		c2.clear();
		c2.set(2014, (6 - 1), 7, 9, 0);
		
		leftTime = (c2.getTimeInMillis() - c.getTimeInMillis()) / 1000;
		
		String minute;
		if (c.get(Calendar.MINUTE) < 10) {
			minute = "0" + String.valueOf(c.get(Calendar.MINUTE));
		}else{
			minute = String.valueOf(c.get(Calendar.MINUTE));
		}
		
		String getupWeibo = "#起床时间# #" + c.get(Calendar.HOUR_OF_DAY)
				+ ":" + minute + "# " + (c.get(Calendar.MONTH) + 1)
				+ "月" + c.get(Calendar.DAY_OF_MONTH) + "日 "
				+ dayNames[c.get(Calendar.DAY_OF_WEEK)] + " 离高考还有#" + leftDays + "天#、#"
						+ leftTime + "秒#。   （¯﹃¯） @JouYiu ~";
		

		new Weibo_Update(getupWeibo ,mHandler).execute();

	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GOT_UPDATE_INFO: {
				final WeiboBackBean update = (WeiboBackBean) msg.obj;

				if (update.getId() == null) {
					if (update.getErrorCode().equals("233")) {
						tv.setText("授权过期了……");
					} else {
						tv.setText(update.getError());
					}
				} else {

					int day = (int) (WeiboConstant.EXPIRES_IN / 86400);
					int hour = (int) (WeiboConstant.EXPIRES_IN / 3600 % 24);
					int minute = (int) (WeiboConstant.EXPIRES_IN / 60 % 60);

					tv.setText("SUCCESS!\n" + "剩余时间 - " + day + "天" + hour
							+ "小时" + minute + "分钟" + "\n\n" + update.getText()
							+ "\n\n> 点击这里查看微博 <");
					tv.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							String mid = Id2MidUtil.Id2Mid(update.getId());
							Uri link = Uri.parse("http://weibo.com/"
									+ WeiboConstant.UID + "/" + mid);
							startActivity(new Intent(Intent.ACTION_VIEW, link));
						}
					});
				}
				break;
			}
			}
		}

	};

}
