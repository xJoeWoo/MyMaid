package com.joewoo.ontime.main;

import static com.joewoo.ontime.info.Defines.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;

import com.joewoo.ontime.Login;
import com.joewoo.ontime.Post;
import com.joewoo.ontime.R;
import com.joewoo.ontime.SingleUser;
import com.joewoo.ontime.info.Weibo_AcquireCount;
import com.joewoo.ontime.singleWeibo.SingleWeibo;
import com.joewoo.ontime.action.Weibo_FriendsTimeLine;
import com.joewoo.ontime.info.WeiboConstant;
import com.joewoo.ontime.tools.Id2MidUtil;
import com.joewoo.ontime.tools.MyMaidAdapter;
import com.joewoo.ontime.tools.MySQLHelper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class Frag_FriendsTimeLine extends Fragment implements OnRefreshListener {

	ArrayList<HashMap<String, String>> text;
	ListView lv;
	MySQLHelper sqlHelper;
	SQLiteDatabase sql;
	boolean isRefreshing = true;
	MyMaidAdapter mAdapter;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	byte[] profileImg;

	@Override
	public void onRefreshStarted(View view) {
		Log.e(TAG, "Refresh FriendsTimeLine");
		refreshFriendsTimeLine();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		return inflater.inflate(R.layout.friendstimeline, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		sqlHelper = new MySQLHelper(getActivity(), SQL_NAME, null, SQL_VERSION);
		sql = sqlHelper.getReadableDatabase();
		Cursor c = sql.query(sqlHelper.tableName, new String[] {
				sqlHelper.FRIENDS_TIMELINE, sqlHelper.PROFILEIMG },
				sqlHelper.UID + "=?", new String[] { WeiboConstant.UID }, null,
				null, null);

		if (c != null && c.moveToFirst()) {
			new Weibo_FriendsTimeLine(c.getString(c
					.getColumnIndex(sqlHelper.FRIENDS_TIMELINE)), sqlHelper,
					mHandler).start();
		} else {
			refreshFriendsTimeLine();
		}

		profileImg = c.getBlob(c.getColumnIndex(sqlHelper.PROFILEIMG));

		lv = (ListView) getView().findViewById(R.id.lv_friends_timeline);
		lv.setDivider(null);

		mPullToRefreshAttacher = ((Main) getActivity())
				.getPullToRefreshAttacher();
		mPullToRefreshAttacher.addRefreshableView(lv, this);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent i = new Intent();
//				i.setClass(getActivity(), SingleWeibo.class);
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
				i.putExtra(WEIBO_ID, map.get(WEIBO_ID));
				i.putExtra(RETWEETED_STATUS_BMIDDLE_PIC,
						map.get(RETWEETED_STATUS_BMIDDLE_PIC));
				i.putExtra(BMIDDLE_PIC, map.get(BMIDDLE_PIC));
				i.putExtra(UID, map.get(UID));
				i.putExtra(RETWEETED_STATUS_UID, map.get(RETWEETED_STATUS_UID));

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

		lv.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE: { // 已经停止
					if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
						if (!isRefreshing) {
							Log.e(TAG, "到底");
							new Weibo_FriendsTimeLine(text.get(
									view.getLastVisiblePosition())
									.get(WEIBO_ID), Weibo_AcquireCount.FRIENDS_TIMELINE_ADD_COUNT, mHandler).start();
							isRefreshing = true;
							mPullToRefreshAttacher.setRefreshing(true);
						}
					}
					break;
				}
				case OnScrollListener.SCROLL_STATE_FLING: { // 开始滚动

					break;
				}
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: { // 正在滚动

					break;
				}
				}
			}
		});
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {

		menu.clear();

		// menu.add(0, MENU_PROFILE_IMAGE, 0, R.string.menu_user_statuses)
		// .setIcon(
		// new BitmapDrawable(getResources(), BitmapFactory
		// .decodeByteArray(profileImg, 0,
		// profileImg.length)))
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		try {
			menu.add(0, MENU_PROFILE_IMAGE, 0, R.string.menu_user_statuses)
					.setIcon(
							new BitmapDrawable(getResources(), BitmapFactory
									.decodeByteArray(profileImg, 0,
											profileImg.length)))
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		} catch (Exception e) {
			Log.e(TAG, "Profile image length: (Timeline) ERROR!");
		}

		menu.add(0, MENU_UNREAD_COUNT, 0,
				WeiboConstant.SCREEN_NAME.toUpperCase(Locale.US))
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

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
		case MENU_REFRESH: {
			refreshFriendsTimeLine();
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

			Cursor cursor = sql.query(sqlHelper.tableName, new String[] {
					sqlHelper.UID, sqlHelper.SCREEN_NAME }, null, null, null,
					null, null);
			Log.e(TAG_SQL, "Queried users");

			if (cursor.getCount() > 0) {
				final String[] singleUid = new String[cursor.getCount() + 2];
				final String[] singleUser = new String[cursor.getCount() + 2];

				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
						.moveToNext()) {

					singleUid[cursor.getPosition()] = cursor.getString(0);
					singleUser[cursor.getPosition()] = cursor.getString(1);
					Log.e(TAG, "Cursor position - " + cursor.getPosition());
					Log.e(TAG, "Single Uid - "
							+ singleUid[cursor.getPosition()]);
					Log.e(TAG,
							"Single User - " + singleUser[cursor.getPosition()]);
					Log.e(TAG, LOG_DEVIDER);
				}

				singleUser[cursor.getCount()] = getActivity()
						.getResources()
						.getString(
								R.string.frag_ftl_dialog_choose_account_add_account);
				singleUid[cursor.getCount()] = "0";

				singleUser[cursor.getCount() + 1] = getActivity()
						.getResources().getString(
								R.string.frag_ftl_dialog_choose_account_logout);
				singleUid[cursor.getCount() + 1] = "1";

				new AlertDialog.Builder(getActivity())
						.setTitle(R.string.frag_ftl_dialog_choose_account_title)
						.setItems(singleUser, new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								SharedPreferences uids = getActivity()
										.getSharedPreferences(PREFERENCES,
												Context.MODE_PRIVATE);
								SharedPreferences.Editor uidsE = uids.edit();

								Log.e(TAG, "Chose UID: " + singleUid[which]);
								Log.e(TAG, "Chose Screen Name: "
										+ singleUid[which]);

								if (!singleUid[which].equals("0")
										&& !singleUid[which].equals("1")) {
									uidsE.putString(LASTUID, singleUid[which]);
									uidsE.commit();
									getActivity().finish();
									startActivity(new Intent(getActivity(),
											Main.class));
								} else if (singleUid[which].equals("0")) {
									startActivity(new Intent(getActivity(),
											Login.class));
									getActivity().finish();
								} else if (singleUid[which].equals("1")) {

									new AlertDialog.Builder(getActivity(),
											AlertDialog.THEME_HOLO_LIGHT)
											.setTitle(
													R.string.frag_ftl_dialog_confirm_logout_title)
											.setPositiveButton(
													R.string.frag_ftl_dialog_confirm_logout_btn_ok,
													new OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															SharedPreferences.Editor editor = ((Main) getActivity())
																	.getEditor();

															editor.putString(
																	LASTUID, "");
															editor.commit();

															if (sql.delete(
																	sqlHelper.tableName,
																	sqlHelper.UID
																			+ "=?",
																	new String[] { WeiboConstant.UID }) > 0) {
																Log.e(TAG_SQL,
																		"LOGOUT - Cleared user info");

																Toast.makeText(
																		getActivity(),
																		"<(￣︶￣)>",
																		Toast.LENGTH_SHORT)
																		.show();
																startActivity(new Intent(
																		getActivity(),
																		Login.class));
																getActivity()
																		.finish();
															}
														}
													})
											.setNegativeButton(
													R.string.frag_ftl_dialog_confirm_logout_btn_cancle,
													null).show();
								}
							}
						}).show();
			} else {

			}

			break;
		}
		case MENU_PROFILE_IMAGE: {
			Intent i = new Intent();
			i.setClass(getActivity(), SingleUser.class);

			i.putExtra(SCREEN_NAME, WeiboConstant.SCREEN_NAME);

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
			mPullToRefreshAttacher.setRefreshComplete();
			isRefreshing = false;
			switch (msg.what) {
			case GOT_FRIENDS_TIMELINE_INFO: {
				text = (ArrayList<HashMap<String, String>>) msg.obj;
				setListView(text);

				break;
			}
			case GOT_FRIENDS_TIMELINE_ADD_INFO: {
				text.addAll((ArrayList<HashMap<String, String>>) msg.obj);
				addListView(text);
				break;
			}
			case GOT_FRIENDS_TIMELINE_INFO_FAIL: {
				Toast.makeText(getActivity(),
						R.string.toast_user_timeline_fail, Toast.LENGTH_SHORT)
						.show();
				break;
			}
			case GOT_FRIENDS_TIMELINE_EXTRA_INFO: {

				break;
			}
			}
			getActivity().invalidateOptionsMenu();
		}

	};

	private void setListView(ArrayList<HashMap<String, String>> arrayList) {
		mAdapter = new MyMaidAdapter(getActivity(), arrayList);
		lv.setAdapter(mAdapter);
	}

	private void addListView(ArrayList<HashMap<String, String>> arrayList) {
		mAdapter.addItem(arrayList);
		mAdapter.notifyDataSetChanged();
	}

	public void refreshFriendsTimeLine() {
		new Weibo_FriendsTimeLine(Weibo_AcquireCount.FRIENDS_TIMELINE_COUNT, sqlHelper, mHandler).start();
		isRefreshing = true;
	}
}
