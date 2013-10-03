package com.joewoo.ontime.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;

import com.joewoo.ontime.Comment_Repost;
import com.joewoo.ontime.Post;
import com.joewoo.ontime.R;
import com.joewoo.ontime.SingleUser;
import com.joewoo.ontime.SingleWeibo;
import com.joewoo.ontime.action.Weibo_Mentions;
import com.joewoo.ontime.action.Weibo_RemindSetCount;
import com.joewoo.ontime.action.Weibo_UnreadCount;
import com.joewoo.ontime.bean.UnreadCountBean;
import com.joewoo.ontime.info.WeiboConstant;

import static com.joewoo.ontime.info.Defines.*;

import com.joewoo.ontime.tools.MySQLHelper;

import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
public class Frag_Mentions extends Fragment implements OnRefreshListener {

	ArrayList<HashMap<String, String>> text;
	ListView lv;
	MySQLHelper sqlHelper;
	SQLiteDatabase sql;
	boolean isRefreshing;
	String unreadCount;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	int mentionsCount = 20;
	byte[] profileImg;

	@Override
	public void onRefreshStarted(View view) {
		Log.e(TAG, "Refresh Mentions");
		refreshMentions();
		new Weibo_RemindSetCount(mHandler)
				.execute(Weibo_RemindSetCount.setMentionsCount);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		return inflater.inflate(R.layout.friendstimeline, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		

		sqlHelper = new MySQLHelper(getActivity(), SQL_NAME, null, SQL_VERSION);
		sql = sqlHelper.getReadableDatabase();
		Cursor c = sql.query(sqlHelper.tableName, new String[] {
				sqlHelper.MENTIONS, sqlHelper.PROFILEIMG }, sqlHelper.UID
				+ "=?", new String[] { WeiboConstant.UID }, null, null, null);

		if (c != null && c.moveToFirst()) {
			new Weibo_Mentions(
					c.getString(c.getColumnIndex(sqlHelper.MENTIONS)),
					sqlHelper, mHandler).start();
		} else {
			refreshMentions();
		}
		
		profileImg = c.getBlob(c.getColumnIndex(sqlHelper.PROFILEIMG));
		
		lv = (ListView) getView().findViewById(R.id.lv_friends_timeline);
		lv.setDivider(null);
		
		mPullToRefreshAttacher = ((Timeline_Comments_Mentions) getActivity())
				.getPullToRefreshAttacher();
		mPullToRefreshAttacher.addRefreshableView(lv, this);

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

				Intent i = new Intent();
				i.setClass(getActivity(), SingleWeibo.class);

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
				i.putExtra(WEIBO_ID, map.get(WEIBO_ID));
				i.putExtra(RETWEETED_STATUS_BMIDDLE_PIC,
						map.get(RETWEETED_STATUS_BMIDDLE_PIC));
				i.putExtra(BMIDDLE_PIC, map.get(BMIDDLE_PIC));
				i.putExtra(UID, map.get(UID));
				i.putExtra(RETWEETED_STATUS_UID, map.get(RETWEETED_STATUS_UID));

				startActivity(i);

				// startActivity(new Intent(Intent.ACTION_VIEW, Uri
				// .parse("http://weibo.com/"
				// + text.get(arg2).get(UID)
				// + "/"
				// + Id2MidUtil.Id2Mid(text.get(arg2)
				// .get(WEIBO_ID)))));
				return false;
			}
		});
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {

		menu.clear();

		try {
			menu.add(0, MENU_PROFILE_IMAGE, 0, R.string.menu_coming)
					.setIcon(
							new BitmapDrawable(getResources(), BitmapFactory
									.decodeByteArray(profileImg, 0,
											profileImg.length)))
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		} catch (Exception e) {
		}

		if (unreadCount == null)
			menu.add(0, MENU_UNREAD_COUNT, 0, R.string.menu_unread)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		else
			menu.add(0, MENU_UNREAD_COUNT, 0, unreadCount).setShowAsAction(
					MenuItem.SHOW_AS_ACTION_ALWAYS);

		menu.add(0, MENU_POST, 0, R.string.menu_post)
				.setIcon(R.drawable.social_send_now)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: {

			break;
		}
		case MENU_POST: {
			Intent i = new Intent();
			i.setClass(getActivity(), Post.class);
			i.putExtra(IS_FRAG_POST, true);
			i.putExtra(PROFILE_IMAGE, profileImg);
			startActivity(i);
			break;
		}
		case MENU_UNREAD_COUNT: {
			getUnreadMentionsCount();
			break;
		}
		case MENU_PROFILE_IMAGE: {
			Intent i = new Intent();
			i.setClass(getActivity(), SingleUser.class);
			i.putExtra(UID, "1893689251");
			i.putExtra(SCREEN_NAME, "Selley__LauChingYee");
			startActivity(i);
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
			// getActivity().setProgressBarIndeterminateVisibility(false);
			isRefreshing = false;
			mPullToRefreshAttacher.setRefreshComplete();
			switch (msg.what) {
			case GOT_MENTIONS_INFO: {

				text = (ArrayList<HashMap<String, String>>) msg.obj;
				setListView(text);

				// new
				// Weibo_RemindSetCount(mHandler).execute(SET_MENTIONS_COUNT);

				break;
			}
			case GOT_MENTIONS_INFO_FAIL: {
				Toast.makeText(getActivity(), R.string.toast_mentions_fail,
						Toast.LENGTH_SHORT).show();
				break;
			}
			case GOT_UNREAD_COUNT_INFO: {
				UnreadCountBean b = (UnreadCountBean) msg.obj;
				if (b.getMentionStatusCount() != null)
					unreadCount = b.getMentionStatusCount();
				else
					Toast.makeText(getActivity(),
							R.string.toast_unread_count_fail,
							Toast.LENGTH_SHORT).show();
				break;
			}
			case GOT_SET_REMIND_COUNT_INFO_FAIL: {
				Toast.makeText(getActivity(),
						R.string.toast_clear_unread_count_fail,
						Toast.LENGTH_SHORT).show();
				break;
			}
			}
			getActivity().invalidateOptionsMenu();
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

	public void getUnreadMentionsCount() {
		new Weibo_UnreadCount(mHandler).start();
	}

	public void refreshMentions() {
		new Weibo_Mentions(mentionsCount, sqlHelper, mHandler).start();
		isRefreshing = true;
	}

}
