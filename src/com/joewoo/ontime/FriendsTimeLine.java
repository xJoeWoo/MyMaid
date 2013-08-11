package com.joewoo.ontime;

import java.util.ArrayList;
import java.util.HashMap;

import com.joewoo.ontime.action.Weibo_FriendsTimeLine;
import com.joewoo.ontime.info.WeiboConstant;
import com.joewoo.ontime.tools.Id2MidUtil;

import static com.joewoo.ontime.info.Defines.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class FriendsTimeLine extends Activity {

	ListView lv;
	ArrayList<HashMap<String, String>> text;
	boolean isRepostedWeibo;

	// MySQLHelper sqlHelper = new MySQLHelper(FriendsTimeLine.this,
	// "MyMaid.db",
	// null, 1);
	// SQLiteDatabase sql;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.friendstimeline);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		setProgressBarIndeterminateVisibility(true);

		Toast.makeText(FriendsTimeLine.this, "正在载入最近50条微博", Toast.LENGTH_SHORT)
				.show();

		new Weibo_FriendsTimeLine(50, mHandler).start();

		lv = (ListView) findViewById(R.id.lv_friends_timeline);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
//				Intent i = new Intent();
//				i.setClass(FriendsTimeLine.this, Comment_Repost.class);
//				i.putExtra(IS_COMMENT, true);
//				i.putExtra(WEIBO_ID, text.get(arg2).get(WEIBO_ID));
//				startActivity(i);
				
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://weibo.com/"
						+ text.get(arg2).get(UID) + "/" + Id2MidUtil.Id2Mid(text.get(arg2).get(WEIBO_ID)))));
				
			}
		});

		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
//				Intent i = new Intent();
//				i.setClass(FriendsTimeLine.this, Comment_Repost.class);
//				i.putExtra(IS_COMMENT, false);
//				i.putExtra(WEIBO_ID, text.get(arg2).get(WEIBO_ID));
//				if (text.get(arg2).get(RETWEETED_STATUS) != null) {
//					i.putExtra(TEXT, "//@" + text.get(arg2).get(SCREEN_NAME)
//							+ ":" + text.get(arg2).get(TEXT));
//				}
//				startActivity(i);
				
				Intent i = new Intent();
				i.setClass(FriendsTimeLine.this, Comment_Repost.class);
				i.putExtra(IS_COMMENT, true);
				i.putExtra(WEIBO_ID, text.get(arg2).get(WEIBO_ID));
				startActivity(i);
				
				return false;
			}
		});

	}

	private Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GOT_FRIENDS_TIMELINE_INFO: {

				text = (ArrayList<HashMap<String, String>>) msg.obj;

				SimpleAdapter data = new SimpleAdapter(
						FriendsTimeLine.this,
						text,
						R.layout.friendstimeline_lv,
						new String[] { SCREEN_NAME, TEXT, COMMENTS_COUNT,
								REPOSTS_COUNT, SOURCE, CREATED_AT,
								RETWEETED_STATUS_SCREEN_NAME, RETWEETED_STATUS },
						new int[] {
								R.id.friendstimeline_screen_name,
								R.id.friendstimeline_text,
								R.id.friendstimeline_comments_count,
								R.id.friendstimeline_reposts_count,
								R.id.friendstimeline_source,
								R.id.friendstimeline_created_at,
								R.id.friendstimeline_retweeted_status_screen_name,
								R.id.friendstimeline_retweeted_status });

				SimpleAdapter.ViewBinder binder = new SimpleAdapter.ViewBinder() {
					@Override
					public boolean setViewValue(View view, Object data,
							String textRepresentation) {
						if (view.equals((TextView) view
								.findViewById(R.id.friendstimeline_retweeted_status_screen_name))) {

							if (!"".equals(textRepresentation)) {
								view.findViewById(
										R.id.friendstimeline_retweeted_status_screen_name)
										.setVisibility(View.VISIBLE);
							} else {
								view.findViewById(
										R.id.friendstimeline_retweeted_status_screen_name)
										.setVisibility(View.GONE);
							}
						}
						if (view.equals((TextView) view
								.findViewById(R.id.friendstimeline_retweeted_status))) {

							if (!"".equals(textRepresentation)) {
								view.findViewById(
										R.id.friendstimeline_retweeted_status)
										.setVisibility(View.VISIBLE);
							} else {
								view.findViewById(
										R.id.friendstimeline_retweeted_status)
										.setVisibility(View.GONE);
							}
						}
						return false;
					}
				};

				data.setViewBinder(binder);

				lv.setAdapter(data);
				setProgressBarIndeterminateVisibility(false);

				break;
			}
			}
		}
	};

	// @Override
	// public boolean onPrepareOptionsMenu(Menu menu) {
	// menu.clear();
	// return true;
	// }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: {
			finish();
			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}

}
