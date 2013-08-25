package com.joewoo.ontime;

import java.util.ArrayList;
import java.util.HashMap;

import com.joewoo.ontime.action.Weibo_FriendsTimeLine;
import com.joewoo.ontime.fragment.Timeline_Comments_Mentions;
import com.joewoo.ontime.info.WeiboConstant;
import com.joewoo.ontime.tools.Id2MidUtil;
import com.joewoo.ontime.tools.MySQLHelper;

import static com.joewoo.ontime.info.Defines.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FriendsTimeLine extends Activity {

	ListView lv;
	ArrayList<HashMap<String, String>> text;
	boolean isRepostedWeibo;
	boolean isRefreshing = true;

	private MySQLHelper sqlHelper = new MySQLHelper(FriendsTimeLine.this,
			SQL_NAME, null, SQL_VERSION);
	private SQLiteDatabase sql;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.friendstimeline);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		setProgressBarIndeterminateVisibility(true);

		lv = (ListView) findViewById(R.id.lv_friends_timeline);
		lv.setDivider(null);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// Intent i = new Intent();
				// i.setClass(FriendsTimeLine.this, Comment_Repost.class);
				// i.putExtra(IS_COMMENT, true);
				// i.putExtra(WEIBO_ID, text.get(arg2).get(WEIBO_ID));
				// startActivity(i);

				startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://weibo.com/"
								+ text.get(arg2).get(UID)
								+ "/"
								+ Id2MidUtil.Id2Mid(text.get(arg2)
										.get(WEIBO_ID)))));

			}
		});

		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// Intent i = new Intent();
				// i.setClass(FriendsTimeLine.this, Comment_Repost.class);
				// i.putExtra(IS_COMMENT, false);
				// i.putExtra(WEIBO_ID, text.get(arg2).get(WEIBO_ID));
				// if (text.get(arg2).get(RETWEETED_STATUS) != null) {
				// i.putExtra(TEXT, "//@" + text.get(arg2).get(SCREEN_NAME)
				// + ":" + text.get(arg2).get(TEXT));
				// }
				// startActivity(i);

				Intent i = new Intent();
				i.setClass(FriendsTimeLine.this, Comment_Repost.class);
				i.putExtra(IS_COMMENT, true);
				i.putExtra(WEIBO_ID, text.get(arg2).get(WEIBO_ID));
				startActivity(i);

				return false;
			}
		});

		sql = sqlHelper.getWritableDatabase();
		Cursor c = sql.query(sqlHelper.tableName,
				new String[] { sqlHelper.FRIENDS_TIMELINE }, sqlHelper.UID
						+ "=?", new String[] { WeiboConstant.UID }, null, null,
				null);

		if (c.moveToFirst()) {
			new Weibo_FriendsTimeLine(c.getString(c
					.getColumnIndex(sqlHelper.FRIENDS_TIMELINE)), sqlHelper,
					mHandler).start();
		} else {
			new Weibo_FriendsTimeLine(50, mHandler).start();
		}

	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GOT_FRIENDS_TIMELINE_INFO: {

				text = (ArrayList<HashMap<String, String>>) msg.obj;

				setListView(text);

				break;
			}
			case GOT_FRIENDS_TIMELINE_INFO_FAIL: {
				Toast.makeText(FriendsTimeLine.this, "获取信息失败",
						Toast.LENGTH_SHORT).show();
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

	private void setListView(ArrayList<HashMap<String, String>> arrayList) {
		SimpleAdapter data = new SimpleAdapter(FriendsTimeLine.this, arrayList,
				R.layout.friendstimeline_lv_new, new String[] { SCREEN_NAME,
						TEXT, COMMENTS_COUNT, REPOSTS_COUNT, SOURCE,
						CREATED_AT, RETWEETED_STATUS_SCREEN_NAME,
//						RETWEETED_STATUS, RETWEETED_STATUS_COMMENTS_COUNT,
//						RETWEETED_STATUS_REPOSTS_COUNT, 
						IS_REPOST, HAVE_PIC },
				new int[] { R.id.friendstimeline_screen_name,
						R.id.friendstimeline_text,
						R.id.friendstimeline_comments_count,
						R.id.friendstimeline_reposts_count,
						R.id.friendstimeline_source,
						R.id.friendstimeline_created_at,
						R.id.friendstimeline_retweeted_status_screen_name,
						R.id.friendstimeline_retweeted_status,
//						R.id.friendstimeline_retweeted_status_comments_count,
//						R.id.friendstimeline_retweeted_status_reposts_count,
						R.id.friendstimeline_retweeted_status_rl,
						R.id.friendstimeline_have_image });

		SimpleAdapter.ViewBinder binder = new SimpleAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {

				// if (view.equals((TextView) view
				// .findViewById(R.id.friendstimeline_text))
				// || view.equals((TextView)
				// findViewById(R.id.friendstimeline_retweeted_status)))
				// {
				//
				// char strarray[];
				//
				// SpannableString ss = new SpannableString(
				// textRepresentation);
				//
				// strarray = textRepresentation.toCharArray();
				//
				// int l = textRepresentation.length() - 10;
				//
				// for (int i = 0; i < l; i++) {
				// if (strarray[i] == 'h' && strarray[i + 1] == 't'
				// && strarray[i + 2] == 't' && strarray[i + 3] == 'p'
				// && strarray[i + 4] == ':' && strarray[i + 5] == '/'
				// && strarray[i + 6] == '/') {
				// StringBuffer sb = new StringBuffer("http://");
				// for (int j = i + 7; true; j++) {
				// if (strarray[j] != ' ')
				// sb.append(strarray[j]);
				// else {
				// Log.d("http", sb.toString());
				// ss.setSpan(new URLSpan(sb.toString()), i, j,
				// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				// i = j;
				// break;
				// }
				// }
				// }
				// }
				//
				// l = textRepresentation.length();
				// StringBuffer sb = null;
				// boolean start = false;
				// int startIndex = 0;
				// for (int i = 0; i < l; i++) {
				// if (strarray[i] == '@') {
				// start = true;
				// sb = new StringBuffer("weibo://weibo.view/");
				// startIndex = i;
				// } else {
				// if (start) {
				// if (strarray[i] == ':') {
				// ss.setSpan(new URLSpan(sb.toString()), startIndex, i,
				// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				// sb = null;
				// start = false;
				// } else {
				// sb.append(strarray[i]);
				// }
				// }
				// }
				//
				// }
				//
				// start = false;
				// startIndex = 0;
				// for (int i = 0; i < l; i++) {
				// if (strarray[i] == '#') {
				// if (!start) {
				// start = true;
				// sb = new StringBuffer("weibo://weibo.view/");
				// startIndex = i;
				// } else {
				// sb.append('#');
				// ss.setSpan(new URLSpan(sb.toString()), startIndex, i
				// + 1,
				// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				// sb = null;
				// start = false;
				// }
				// } else {
				// if (start) {
				// sb.append(strarray[i]);
				// }
				// }
				// }
				//
				// TextView tv;
				//
				// if (view.equals((TextView) view
				// .findViewById(R.id.friendstimeline_text)))
				// {
				// tv =
				// (TextView)view.findViewById(R.id.friendstimeline_text);
				// }else{
				// tv =
				// (TextView)view.findViewById(R.id.friendstimeline_retweeted_status);
				// }
				//
				// tv.setMovementMethod(LinkMovementMethod.getInstance());
				// tv.setText(ss);
				//
				// }

				// if (view.equals((TextView) view
				// .findViewById(R.id.friendstimeline_text))) {
				// String[] t = textRepresentation.split("@");
				// if (t.length > 0) {
				// for (int i = 0; i < t.length; i++) {
				// Log.e(TAG, t[i]);
				// // StringBuffer y = null;
				// // char[] x = t[i].toCharArray();
				// // for (int j = 0; j < t[i].length(); j++) {
				// // if(x[j] == ':' || x[j] == ' ' || x[j] == '#'){
				// // break;
				// // }
				// // y.append(x[j]);
				// // }
				// // Log.e(TAG, y.toString());
				// }
				// }
				// }

				if (view.equals((TextView) view
						.findViewById(R.id.friendstimeline_retweeted_status_rl))) {
					if (" ".equals(textRepresentation)) {
						view.setVisibility(View.VISIBLE);
					} else {
						view.setVisibility(View.GONE);
					}
				}

				if (view.equals((TextView) view
						.findViewById(R.id.friendstimeline_retweeted_status_screen_name))) {
					if (!"".equals(textRepresentation)) {
						view.setVisibility(View.VISIBLE);
					} else {
						view.setVisibility(View.GONE);
					}
				}

				if (view.equals((TextView) view
						.findViewById(R.id.friendstimeline_retweeted_status))) {
					if (!"".equals(textRepresentation)) {
						view.setVisibility(View.VISIBLE);
					} else {
						view.setVisibility(View.GONE);
					}
				}
				//
//				if (view.equals((TextView) view
//						.findViewById(R.id.friendstimeline_retweeted_status_comments_count))) {
//					if (!"".equals(textRepresentation)) {
//						view.setVisibility(View.VISIBLE);
//					} else {
//						view.setVisibility(View.GONE);
//					}
//				}
//
//				if (view.equals((TextView) view
//						.findViewById(R.id.friendstimeline_retweeted_status_reposts_count))) {
//
//					if (!"".equals(textRepresentation)) {
//						view.setVisibility(View.VISIBLE);
//					} else {
//						view.setVisibility(View.GONE);
//					}
//				}
				//
				// if (view.equals((TextView) view
				// .findViewById(R.id.friendstimeline_reposts_count))) {
				//
				// if (!"0".equals(textRepresentation)
				// && !"".equals(textRepresentation)) {
				// view.findViewById(R.id.friendstimeline_reposts_count).setVisibility(View.VISIBLE);
				// } else {
				// view.findViewById(R.id.friendstimeline_reposts_count).setVisibility(View.GONE);
				// }
				// }
				//
				// if (view.equals((TextView) view
				// .findViewById(R.id.friendstimeline_comments_count)))
				// {
				//
				// if (!"0".equals(textRepresentation)
				// && !"".equals(textRepresentation)) {
				// view.findViewById(R.id.friendstimeline_comments_count).setVisibility(View.VISIBLE);
				// } else {
				// view.findViewById(R.id.friendstimeline_comments_count).setVisibility(View.INVISIBLE);
				// }
				// }

				if (view.equals((TextView) view
						.findViewById(R.id.friendstimeline_have_image))) {
					if (" ".equals(textRepresentation)) {
						view.setVisibility(View.VISIBLE);

					} else {
						view.setVisibility(View.GONE);
					}
				}
				return false;
			}
		};

		data.setViewBinder(binder);

		lv.setAdapter(data);
		isRefreshing = false;
		setProgressBarIndeterminateVisibility(false);
		rfBar();

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		if (!isRefreshing) {
			menu.add(0, MENU_REFRESH, 0, "刷新").setShowAsAction(
					MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: {
			startActivity(new Intent(FriendsTimeLine.this,
					Timeline_Comments_Mentions.class));
			finish();
			break;
		}

		case MENU_REFRESH: {
			new Weibo_FriendsTimeLine(50, sqlHelper, mHandler).start();
			isRefreshing = true;
			setProgressBarIndeterminateVisibility(true);
			rfBar();
			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}

	private void rfBar() {
		getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL); // 刷新ActionBar
	}

}
