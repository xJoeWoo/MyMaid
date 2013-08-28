package com.joewoo.ontime.fragment;

import static com.joewoo.ontime.info.Defines.*;

import java.util.ArrayList;
import java.util.HashMap;

import com.joewoo.ontime.Comment_Repost;
import com.joewoo.ontime.Post;
import com.joewoo.ontime.R;
import com.joewoo.ontime.action.Weibo_CommentsToMe;
import com.joewoo.ontime.action.Weibo_UnreadCount;
import com.joewoo.ontime.bean.UnreadCountBean;
import com.joewoo.ontime.info.WeiboConstant;
import com.joewoo.ontime.tools.MySQLHelper;

import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Toast;

@SuppressLint({ "HandlerLeak", "NewApi" })
public class Frag_Comments extends Fragment {

	ArrayList<HashMap<String, String>> text;
	ListView lv;
	boolean isRefreshing;
	MySQLHelper sqlHelper;
	SQLiteDatabase sql;
	Menu mMenu;
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
				i.setClass(getActivity(), Comment_Repost.class);
				i.putExtra(IS_REPLY, true);
				i.putExtra(WEIBO_ID, text.get(arg2).get(WEIBO_ID));
				i.putExtra(COMMENT_ID, text.get(arg2).get(COMMENT_ID));
				startActivity(i);
			}
		});

		sqlHelper = new MySQLHelper(getActivity(), SQL_NAME, null, SQL_VERSION);
		sql = sqlHelper.getReadableDatabase();
		Cursor c = sql.query(sqlHelper.tableName,
				new String[] { sqlHelper.TO_ME_COMMENTS },
				sqlHelper.UID + "=?", new String[] { WeiboConstant.UID }, null,
				null, null);

		if (c != null && c.moveToFirst()) {
			new Weibo_CommentsToMe(c.getString(c
					.getColumnIndex(sqlHelper.TO_ME_COMMENTS)), sqlHelper,
					mHandler).start();
		} else {
			new Weibo_CommentsToMe(20, sqlHelper, mHandler).start();
		}

	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		menu.add(0, MENU_POST, 0, "发Po").setIcon(R.drawable.social_send_now)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		if (unreadCount == null)
			menu.add(0, MENU_UNREAD_COUNT, 0, "未读").setShowAsAction(
					MenuItem.SHOW_AS_ACTION_ALWAYS);
		else
			menu.add(0, MENU_UNREAD_COUNT, 0, unreadCount).setShowAsAction(
					MenuItem.SHOW_AS_ACTION_ALWAYS);

		if (!isRefreshing)
			menu.add(0, MENU_REFRESH, 0, "刷新")
					.setIcon(R.drawable.navigation_refresh)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		else
			menu.add(0, MENU_REFRESH, 0, "刷新").setEnabled(false)
					.setIcon(R.drawable.navigation_refreshing)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: {

			break;
		}
		case MENU_REFRESH: {
			new Weibo_CommentsToMe(20, sqlHelper, mHandler).start();
			// new Weibo_CommentsToMe(50, mHandler).start();
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
		getActivity().invalidateOptionsMenu();
		return super.onOptionsItemSelected(item);
	}

	Handler mHandler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {

			isRefreshing = false;
			switch (msg.what) {
			case GOT_COMMENTS_TO_ME_INFO: {

				text = (ArrayList<HashMap<String, String>>) msg.obj;

				String[] from = { SCREEN_NAME, TEXT, CREATED_AT, SOURCE,
						STATUS_USER_SCREEN_NAME, STATUS_TEXT };
				int[] to = { R.id.comments_to_me_screen_name,
						R.id.comments_to_me_text,
						R.id.comments_to_me_created_at,
						R.id.comments_to_me_source,
						R.id.comments_to_me_status_screen_name,
						R.id.comments_to_me_status };

				SimpleAdapter data = new SimpleAdapter(getActivity(), text,
						R.layout.comments_to_me_lv, from, to);

				lv.setAdapter(data);
				break;
			}
			case GOT_COMMENTS_TO_ME_INFO_FAIL: {
				Toast.makeText(getActivity(), "获取评论失败…", Toast.LENGTH_SHORT)
						.show();
				break;
			}
			case GOT_UNREAD_COUNT_INFO: {
				UnreadCountBean b = (UnreadCountBean) msg.obj;
				if (b.getMentionCmtCount() != null)
					unreadCount = b.getCmtCount();
				else
					Toast.makeText(getActivity(), "获取未读数失败…",
							Toast.LENGTH_SHORT).show();
				break;
			}
			}
			getActivity().invalidateOptionsMenu();
		}

	};

}
