package com.joewoo.ontime.fragment;

import static com.joewoo.ontime.info.Defines.*;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.joewoo.ontime.Login;
import com.joewoo.ontime.R;
import com.joewoo.ontime.info.WeiboConstant;
import com.joewoo.ontime.tools.MySQLHelper;

public class Timeline_Comments_Mentions extends FragmentActivity {

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	boolean gotCommentsUnread;
	boolean gotMentionsUnread;
	private PullToRefreshAttacher mPullToRefreshAttacher;

	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	MySQLHelper sqlHelper = new MySQLHelper(Timeline_Comments_Mentions.this,
			SQL_NAME, null, SQL_VERSION);
	SQLiteDatabase sql;
	Cursor c;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.timeline_cmt_atme);

		Log.e(TAG, "MyMaid START!");

		sql = sqlHelper.getWritableDatabase();
		preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
		editor = preferences.edit();

		if (preferences.getString(LASTUID, null) != null) {

			String lastUid = preferences.getString(LASTUID, null);

			c = sql.query(sqlHelper.tableName, new String[] { sqlHelper.UID,
					sqlHelper.ACCESS_TOKEN, sqlHelper.LOCATION,
					sqlHelper.EXPIRES_IN, sqlHelper.SCREEN_NAME,
					sqlHelper.DRAFT }, sqlHelper.UID + "=?",
					new String[] { lastUid }, null, null, null);

			if (!loadConstant(c)) {
				Toast.makeText(Timeline_Comments_Mentions.this, R.string.toast_login_acquired,
						Toast.LENGTH_LONG).show();
				jumpToLogin();
			}
			
			mPullToRefreshAttacher = PullToRefreshAttacher.get(this);

			final ActionBar actionBar = getActionBar();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			// actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setDisplayShowHomeEnabled(false);

			mViewPager = (ViewPager) findViewById(R.id.pager);
			mViewPager.setOffscreenPageLimit(3);

			actionBar.addTab(actionBar.newTab()
					.setText(getString(R.string.title_frag_friends_timeline))
					.setTabListener(tabListener));
			actionBar.addTab(actionBar.newTab()
					.setText(getString(R.string.title_frag_comments_to_me))
					.setTabListener(tabListener));
			actionBar.addTab(actionBar.newTab()
					.setText(getString(R.string.title_frag_atme))
					.setTabListener(tabListener));

			FragmentManager mFragmentManager = getSupportFragmentManager();

			mSectionsPagerAdapter = new SectionsPagerAdapter(mFragmentManager);
			mViewPager.setAdapter(mSectionsPagerAdapter);

			mViewPager
					.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
						@Override
						public void onPageSelected(int arg0) {
							actionBar.setSelectedNavigationItem(arg0);
							Log.e(TAG, "Page: " + String.valueOf(arg0));
							switch (arg0) {
							case FRAG_FRIENDSTIMELINE_POS: {

								break;
							}
							case FRAG_COMMENTS_POS: {
								if (!gotCommentsUnread) {
									mSectionsPagerAdapter.getCommentsFrag()
											.getUnreadCommentsCount();
									gotCommentsUnread = true;
								}
								break;
							}
							case FRAG_MENTIONS_POS: {
								if (!gotMentionsUnread) {
									mSectionsPagerAdapter.getMentionsFrag()
											.getUnreadMentionsCount();
									gotMentionsUnread = true;
								}
								break;
							}
							}
						}
					});
			
		} else {// 不存在配置文件，需要登录
			Toast.makeText(Timeline_Comments_Mentions.this, R.string.toast_login_acquired,
					Toast.LENGTH_LONG).show();
			jumpToLogin();
		}

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		return true;
	}

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

//	@Override
//	protected void onResume() {
//		super.onResume();
//		if (loadConstant(c))
//			Log.e(TAG, "Reload WeiboConstant succeed");
//		else
//			Log.e(TAG, "Reload WeiboConstant failed");
//	}

	ActionBar.TabListener tabListener = new ActionBar.TabListener() {

		@Override
		public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
			mViewPager.setCurrentItem(tab.getPosition());

		}

		@Override
		public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}
	};

	public PullToRefreshAttacher getPullToRefreshAttacher() {
		return mPullToRefreshAttacher;
	}
	
	public SharedPreferences.Editor getEditor() {
		return editor;
	}

	private void jumpToLogin() {
		startActivity(new Intent(Timeline_Comments_Mentions.this, Login.class));
		finish();
	}

	private boolean loadConstant(Cursor c) {
		if (c.moveToFirst()) {
			WeiboConstant.UID = c.getString(c.getColumnIndex(sqlHelper.UID));
			WeiboConstant.ACCESS_TOKEN = c.getString(c
					.getColumnIndex(sqlHelper.ACCESS_TOKEN));
			WeiboConstant.LOCATION = c.getString(c
					.getColumnIndex(sqlHelper.LOCATION));
			WeiboConstant.EXPIRES_IN = Integer.valueOf(c.getString(c
					.getColumnIndex(sqlHelper.EXPIRES_IN)));
			WeiboConstant.SCREEN_NAME = c.getString(c
					.getColumnIndex(sqlHelper.SCREEN_NAME));
			return true;
		} else 
			return false;
		
	}
}
