package com.joewoo.ontime.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import com.joewoo.ontime.Comment_Repost;
import com.joewoo.ontime.Post;
import com.joewoo.ontime.R;
import com.joewoo.ontime.action.Weibo_Mentions;
import com.joewoo.ontime.action.Weibo_UnreadCount;
import com.joewoo.ontime.bean.UnreadCountBean;
import com.joewoo.ontime.info.WeiboConstant;

import static com.joewoo.ontime.info.Defines.*;

import com.joewoo.ontime.tools.Id2MidUtil;
import com.joewoo.ontime.tools.MySQLHelper;

import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class Frag_Mentions extends Fragment {

	ArrayList<HashMap<String, String>> text;
	ListView lv;
	MySQLHelper sqlHelper;
	SQLiteDatabase sql;
	boolean isRefreshing;
	String unreadCount;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		return inflater.inflate(R.layout.friendstimeline, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		lv = (ListView) getView().findViewById(R.id.lv_friends_timeline);
		lv.setDivider(null);

		sqlHelper = new MySQLHelper(getActivity(), SQL_NAME, null, SQL_VERSION);
		sql = sqlHelper.getReadableDatabase();
		Cursor c = sql.query(sqlHelper.tableName,
				new String[] { sqlHelper.MENTIONS }, sqlHelper.UID + "=?",
				new String[] { WeiboConstant.UID }, null, null, null);

		if (c != null && c.moveToFirst()) {
			new Weibo_Mentions(
					c.getString(c.getColumnIndex(sqlHelper.MENTIONS)),
					sqlHelper, mHandler).start();
		} else {
			new Weibo_Mentions(20, sqlHelper, mHandler).start();
		}
		
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent i = new Intent();
				i.setClass(getActivity(), Comment_Repost.class);
				i.putExtra(IS_COMMENT, true);
				i.putExtra(WEIBO_ID, text.get(arg2).get(WEIBO_ID));
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

	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {

		menu.clear();

		// if (!isRefreshing)
		
		menu.add(0, MENU_POST, 0, "发Po").setIcon(R.drawable.social_send_now)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		if (unreadCount == null)
			menu.add(0, MENU_UNREAD_COUNT, 0, "未读").setShowAsAction(
					MenuItem.SHOW_AS_ACTION_ALWAYS);
		else
			menu.add(0, MENU_UNREAD_COUNT, 0, unreadCount).setShowAsAction(
					MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		menu.add(0, MENU_REFRESH, 0, "刷新")
		.setIcon(R.drawable.navigation_refresh)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: {

			break;
		}
		case MENU_REFRESH: {
			new Weibo_Mentions(20, sqlHelper, mHandler).start();
			isRefreshing = true;
			break;
		}
		case MENU_POST: {
			startActivity(new Intent(getActivity(), Post.class));
			break;
		}
		case MENU_UNREAD_COUNT: {
			new Weibo_UnreadCount(mHandler).start();
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
			case GOT_MENTIONS_INFO: {

				text = (ArrayList<HashMap<String, String>>) msg.obj;
				setListView(text);

				break;
			}
			case GOT_MENTIONS_INFO_FAIL: {
				Toast.makeText(getActivity(), "获取提及失败…", Toast.LENGTH_SHORT)
						.show();
				break;
			}
			case GOT_UNREAD_COUNT_INFO: {
				UnreadCountBean b = (UnreadCountBean) msg.obj;
				if(b.getMentionStatusCount() != null)
					unreadCount = b.getMentionStatusCount();
				else 
					Toast.makeText(getActivity(), "获取未读数失败…", Toast.LENGTH_SHORT)
					.show();
				
				getActivity().invalidateOptionsMenu();
				break;
			}
			}
		}

	};

	private void setListView(ArrayList<HashMap<String, String>> arrayList) {

		String[] from = { SOURCE, CREATED_AT, SCREEN_NAME, TEXT,
				COMMENTS_COUNT, REPOSTS_COUNT, RETWEETED_STATUS_SCREEN_NAME,
				RETWEETED_STATUS, HAVE_PIC, IS_REPOST };
		int[] to = { R.id.mentions_source, R.id.mentions_created_at,
				R.id.mentions_screen_name, R.id.mentions_text,
				R.id.mentions_comments_count, R.id.mentions_reposts_count,
				R.id.mentions_retweeted_status_screen_name,
				R.id.mentions_retweeted_status, R.id.mentions_have_image,
				R.id.mentions_retweeted_status_rl };

		SimpleAdapter data = new SimpleAdapter(getActivity(), arrayList,
				R.layout.mentions_lv, from, to);

		SimpleAdapter.ViewBinder binder = new SimpleAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {

				if (view.equals((TextView) view
						.findViewById(R.id.mentions_retweeted_status_rl))) {
					if (" ".equals(textRepresentation)) {
						view.setVisibility(View.VISIBLE);
					} else {
						view.setVisibility(View.GONE);
					}
				}

				if (view.equals((TextView) view
						.findViewById(R.id.mentions_retweeted_status_screen_name))) {
					if (!"".equals(textRepresentation)) {
						view.setVisibility(View.VISIBLE);
					} else {
						view.setVisibility(View.GONE);
					}
				}

				if (view.equals((TextView) view
						.findViewById(R.id.mentions_retweeted_status))) {
					if (!"".equals(textRepresentation)) {
						view.setVisibility(View.VISIBLE);
					} else {
						view.setVisibility(View.GONE);
					}
				}
				if (view.equals((TextView) view
						.findViewById(R.id.mentions_have_image))) {
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
