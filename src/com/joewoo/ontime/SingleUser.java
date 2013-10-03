package com.joewoo.ontime;

import static com.joewoo.ontime.info.Defines.*;

import java.util.ArrayList;
import java.util.HashMap;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

import com.joewoo.ontime.action.Weibo_ProfileImage;
import com.joewoo.ontime.action.Weibo_UserShow;
import com.joewoo.ontime.action.Weibo_UserTimeLine;
import com.joewoo.ontime.bean.WeiboBackBean;
import com.joewoo.ontime.tools.MyMaidAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class SingleUser extends Activity {

	private ListView lv;
	private ArrayList<HashMap<String, String>> text;
	private String uid = null;
	private String screenName = null;
	private String followersCount = null;
	private String friendsCount = null;
	private String statusesCount = null;
	private PullToRefreshAttacher mPullToRefreshAttacher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.singleuser);
		setProgressBarIndeterminateVisibility(false);
		getActionBar().setDisplayUseLogoEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		findViews();
		lv.setDivider(null);
		mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
		mPullToRefreshAttacher.setRefreshing(true);

		final Intent i = getIntent();

		screenName = i.getStringExtra(SCREEN_NAME);
		setTitle(screenName);

		uid = i.getStringExtra(UID);

		if (uid == null) {
			Log.e(TAG, "Screen Name: " + screenName);
			new Weibo_UserTimeLine(false, screenName, 20, mHandler).start();
			new Weibo_UserShow(false, screenName, mHandler).start();
		} else {
			Log.e(TAG, "UID: " + uid);
			new Weibo_UserTimeLine(true, uid, 20, mHandler).start();
			new Weibo_UserShow(true, uid, mHandler).start();
		}

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent i = new Intent();
				i.setClass(SingleUser.this, SingleWeibo.class);
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
				i.putExtra(USER_WEIBO, "yep");

				startActivity(i);
			}
		});
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GOT_SHOW_INFO: {
				WeiboBackBean b = (WeiboBackBean) msg.obj;

				followersCount = b.getFollowersCount();
				friendsCount = b.getFriendsCount();
				statusesCount = b.getStatusesCount();

				new Weibo_ProfileImage(b.getProfileImageUrl(), mHandler)
						.start();
				break;
			}
			case GOT_PROFILEIMG_INFO: {

				byte[] img = (byte[]) msg.obj;

				getActionBar().setLogo(
						new BitmapDrawable(getResources(), BitmapFactory
								.decodeByteArray(img, 0, img.length)));

				break;
			}
			case GOT_USER_TIMELINE_INFO: {

				text = (ArrayList<HashMap<String, String>>) msg.obj;
				setListView(text);
				mPullToRefreshAttacher.setRefreshing(false);
				break;
			}
			case GOT_USER_TIMELINE_INFO_FAIL: {
				Toast.makeText(SingleUser.this, R.string.toast_user_timeline_fail, Toast.LENGTH_SHORT)
						.show();
				mPullToRefreshAttacher.setRefreshing(false);
				break;
			}
			}
			rfBar();
		}
	};

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		if (friendsCount == null)
			menu.add(0, MENU_FRIENDS_COUNT, 0, "").setShowAsAction(
					MenuItem.SHOW_AS_ACTION_ALWAYS);
		else
			menu.add(0, MENU_FRIENDS_COUNT, 0, getResources().getString(R.string.menu_fan_xxx) + friendsCount)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		if (followersCount == null)
			menu.add(0, MENU_FOLLOWERS_COUNT, 0, "").setShowAsAction(
					MenuItem.SHOW_AS_ACTION_ALWAYS);
		else
			menu.add(0, MENU_FOLLOWERS_COUNT, 0, followersCount + getResources().getString(R.string.menu_xxx_fans))
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		if (statusesCount == null)
			menu.add(0, MENU_STATUSES_COUNT, 0, "").setShowAsAction(
					MenuItem.SHOW_AS_ACTION_ALWAYS);
		else
			menu.add(0, MENU_STATUSES_COUNT, 0, statusesCount + getResources().getString(R.string.menu_xxx_statuses))
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_FRIENDS_COUNT: {

			break;
		}
		case MENU_FOLLOWERS_COUNT: {

			break;
		}
		case MENU_STATUSES_COUNT: {

			break;
		}
		case android.R.id.home: {
			finish();
			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}

	private void findViews() {
		lv = (ListView) findViewById(R.id.lv_single_user);
	}

	private void rfBar() {
		invalidateOptionsMenu();
	}

	private void setListView(ArrayList<HashMap<String, String>> arrayList) {
		MyMaidAdapter mAdapter = new MyMaidAdapter(SingleUser.this, arrayList);
		lv.setAdapter(mAdapter);
	}

}
