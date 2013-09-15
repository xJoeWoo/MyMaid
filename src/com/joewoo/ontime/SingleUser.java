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
import android.view.Window;
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

		if (uid == null){
			Log.e(TAG, "Screen Name: " + screenName);
			new Weibo_UserTimeLine(false, screenName, 20, mHandler).start();
			new Weibo_UserShow(false, screenName, mHandler).start();
		}
		else {
			Log.e(TAG, "UID: " + uid);
			new Weibo_UserTimeLine(true, uid, 20, mHandler).start();
			new Weibo_UserShow(true, uid, mHandler).start();
		}
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
				Toast.makeText(SingleUser.this, "获取微博失败…", Toast.LENGTH_SHORT)
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
			menu.add(0, MENU_FRIENDS_COUNT, 0, "粉 " + friendsCount)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		if (followersCount == null)
			menu.add(0, MENU_FOLLOWERS_COUNT, 0, "").setShowAsAction(
					MenuItem.SHOW_AS_ACTION_ALWAYS);
		else
			menu.add(0, MENU_FOLLOWERS_COUNT, 0, followersCount + " 粉")
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		if (statusesCount == null)
			menu.add(0, MENU_STATUSES_COUNT, 0, "").setShowAsAction(
					MenuItem.SHOW_AS_ACTION_ALWAYS);
		else
			menu.add(0, MENU_STATUSES_COUNT, 0, statusesCount + " 博")
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
		invalidateOptionsMenu(); // 刷新ActionBar
	}

	private void setListView(ArrayList<HashMap<String, String>> arrayList) {
		MyMaidAdapter mAdapter = new MyMaidAdapter(SingleUser.this, arrayList);
		lv.setAdapter(mAdapter);
	}

}
