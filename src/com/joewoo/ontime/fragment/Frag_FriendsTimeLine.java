package com.joewoo.ontime.fragment;

import static com.joewoo.ontime.info.Defines.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.joewoo.ontime.Post;
import com.joewoo.ontime.R;
import com.joewoo.ontime.SingleWeibo;
import com.joewoo.ontime.action.Weibo_FriendsTimeLine;
import com.joewoo.ontime.info.WeiboConstant;
import com.joewoo.ontime.tools.Id2MidUtil;
import com.joewoo.ontime.tools.MySQLHelper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class Frag_FriendsTimeLine extends Fragment {

	ArrayList<HashMap<String, String>> text;
	ListView lv;
	MySQLHelper sqlHelper;
	SQLiteDatabase sql;
	boolean isRefreshing = true;
	String unreadCount;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		return inflater.inflate(R.layout.friendstimeline, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		lv = (ListView) getView().findViewById(R.id.lv_friends_timeline);
		lv.setDivider(null);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent i = new Intent();
				i.setClass(getActivity(), SingleWeibo.class);
				// i.putExtra(IS_COMMENT, true);
				// i.putExtra(WEIBO_ID, text.get(arg2).get(WEIBO_ID));

				HashMap<String, String> map = text.get(arg2);

				i.putExtra(SCREEN_NAME, map.get(SCREEN_NAME));
				i.putExtra(CREATED_AT, map.get(CREATED_AT));
				i.putExtra(TEXT, map.get(TEXT));
				i.putExtra(PROFILE_IMAGE_URL, map.get(PROFILE_IMAGE_URL));
				i.putExtra(IS_REPOST, map.get(IS_REPOST));
				i.putExtra(RETWEETED_STATUS_SCREEN_NAME,
						map.get(RETWEETED_STATUS_SCREEN_NAME));
				i.putExtra(RETWEETED_STATUS, map.get(RETWEETED_STATUS));
				i.putExtra(RETWEETED_STATUS_COMMENTS_COUNT,
						map.get(RETWEETED_STATUS_COMMENTS_COUNT));
				i.putExtra(RETWEETED_STATUS_REPOSTS_COUNT,
						map.get(RETWEETED_STATUS_REPOSTS_COUNT));
				i.putExtra(RETWEETED_STATUS_SOURCE,
						map.get(RETWEETED_STATUS_SOURCE));
				i.putExtra(RETWEETED_STATUS_CREATED_AT,
						map.get(RETWEETED_STATUS_CREATED_AT));
				i.putExtra(RETWEETED_STATUS_THUMBNAIL_PIC,
						map.get(RETWEETED_STATUS_THUMBNAIL_PIC));
				i.putExtra(COMMENTS_COUNT, map.get(COMMENTS_COUNT));
				i.putExtra(REPOSTS_COUNT, map.get(REPOSTS_COUNT));
				i.putExtra(SOURCE, map.get(SOURCE));
				i.putExtra(THUMBNAIL_PIC, map.get(THUMBNAIL_PIC));
				i.putExtra(WEIBO_ID, map.get(WEIBO_ID));
				i.putExtra(HAVE_PIC, map.get(HAVE_PIC));
				i.putExtra(RETWEETED_STATUS_HAVE_PIC,
						map.get(RETWEETED_STATUS_HAVE_PIC));

				startActivity(i);
			}
		});

		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://weibo.com/"
								+ text.get(arg2).get(UID)
								+ "/"
								+ Id2MidUtil.Id2Mid(text.get(arg2)
										.get(WEIBO_ID)))));
				return false;
			}
		});

		sqlHelper = new MySQLHelper(getActivity(), SQL_NAME, null, SQL_VERSION);
		sql = sqlHelper.getReadableDatabase();
		Cursor c = sql.query(sqlHelper.tableName,
				new String[] { sqlHelper.FRIENDS_TIMELINE }, sqlHelper.UID
						+ "=?", new String[] { WeiboConstant.UID }, null, null,
				null);

		if (c != null && c.moveToFirst()) {
			Log.e(TAG_SQL,
					"SQL Timeline:\n"
							+ c.getString(c
									.getColumnIndex(sqlHelper.FRIENDS_TIMELINE)));
			new Weibo_FriendsTimeLine(c.getString(c
					.getColumnIndex(sqlHelper.FRIENDS_TIMELINE)), sqlHelper,
					mHandler).start();
		} else {
			new Weibo_FriendsTimeLine(50, sqlHelper, mHandler).start();
		}
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {

		menu.clear();

		menu.add(0, MENU_POST, 0, "发Po").setIcon(R.drawable.social_send_now)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		menu.add(0, MENU_UNREAD_COUNT, 0,
				WeiboConstant.SCREEN_NAME.toUpperCase(Locale.US))
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		menu.add(0, MENU_REFRESH, 0, "刷新").setIcon(R.drawable.navigation_refresh)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: {

			break;
		}
		case MENU_REFRESH: {
			new Weibo_FriendsTimeLine(50, sqlHelper, mHandler).start();
			isRefreshing = true;
			break;
		}
		case MENU_POST: {
			startActivity(new Intent(getActivity(), Post.class));
			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}

	Handler mHandler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			// getActivity().setProgressBarIndeterminateVisibility(false);
			isRefreshing = false;
			switch (msg.what) {
			case GOT_FRIENDS_TIMELINE_INFO: {

				text = (ArrayList<HashMap<String, String>>) msg.obj;
				setListView(text);

				break;
			}
			case GOT_FRIENDS_TIMELINE_INFO_FAIL: {
				Toast.makeText(getActivity(), "获取信息失败…", Toast.LENGTH_SHORT)
						.show();
				break;
			}
			}
		}

	};

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Add your menu entries here
		super.onCreateOptionsMenu(menu, inflater);
	}

	private void setListView(ArrayList<HashMap<String, String>> arrayList) {
		SimpleAdapter data = new SimpleAdapter(getActivity(), arrayList,
				R.layout.friendstimeline_lv_new, new String[] { SCREEN_NAME,
						TEXT, COMMENTS_COUNT, REPOSTS_COUNT, SOURCE,
						CREATED_AT, RETWEETED_STATUS_SCREEN_NAME,
						RETWEETED_STATUS,
						// RETWEETED_STATUS_COMMENTS_COUNT,
						// RETWEETED_STATUS_REPOSTS_COUNT,
						IS_REPOST, HAVE_PIC, RETWEETED_STATUS_HAVE_PIC },
				new int[] {
						R.id.friendstimeline_screen_name,
						R.id.friendstimeline_text,
						R.id.friendstimeline_comments_count,
						R.id.friendstimeline_reposts_count,
						R.id.friendstimeline_source,
						R.id.friendstimeline_created_at,
						R.id.friendstimeline_retweeted_status_screen_name,
						R.id.friendstimeline_retweeted_status,
						// R.id.friendstimeline_retweeted_status_comments_count,
						// R.id.friendstimeline_retweeted_status_reposts_count,
						R.id.friendstimeline_retweeted_status_rl,
						R.id.friendstimeline_have_image,
						R.id.friendstimeline_have_image });

		SimpleAdapter.ViewBinder binder = new SimpleAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {

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
				// if (view.equals((TextView) view
				// .findViewById(R.id.friendstimeline_retweeted_status_comments_count)))
				// {
				// if (!"".equals(textRepresentation)) {
				// view.setVisibility(View.VISIBLE);
				// } else {
				// view.setVisibility(View.GONE);
				// }
				// }
				//
				// if (view.equals((TextView) view
				// .findViewById(R.id.friendstimeline_retweeted_status_reposts_count)))
				// {
				//
				// if (!"".equals(textRepresentation)) {
				// view.setVisibility(View.VISIBLE);
				// } else {
				// view.setVisibility(View.GONE);
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
	}

}
