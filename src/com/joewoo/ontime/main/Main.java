package com.joewoo.ontime.main;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.joewoo.ontime.Login;
import com.joewoo.ontime.R;
import com.joewoo.ontime.info.Weibo_Constants;
import com.joewoo.ontime.tools.MyMaidSQLHelper;

import java.util.Locale;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

import static com.joewoo.ontime.info.Constants.LASTUID;
import static com.joewoo.ontime.info.Constants.PREFERENCES;
import static com.joewoo.ontime.info.Constants.TAG;

public class Main extends FragmentActivity {

	MainPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	boolean gotCommentsUnread;
	boolean gotMentionsUnread;
	private PullToRefreshAttacher mPullToRefreshAttacher;

	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	MyMaidSQLHelper sqlHelper = new MyMaidSQLHelper(Main.this,
            MyMaidSQLHelper.SQL_NAME, null, MyMaidSQLHelper.SQL_VERSION);
	SQLiteDatabase sql;
	Cursor c;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.timeline_cmt_atme);

		Log.e(TAG, "MyMaid START!");

		sql = sqlHelper.getWritableDatabase();
		preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
		editor = preferences.edit();

		if (preferences.getString(LASTUID, null) != null) {

			String lastUid = preferences.getString(LASTUID, null);

			c = sql.query(MyMaidSQLHelper.tableName, new String[] { MyMaidSQLHelper.UID,
                    MyMaidSQLHelper.ACCESS_TOKEN, MyMaidSQLHelper.LOCATION,
                    MyMaidSQLHelper.EXPIRES_IN, MyMaidSQLHelper.SCREEN_NAME,
                    MyMaidSQLHelper.DRAFT }, MyMaidSQLHelper.UID + "=?",
					new String[] { lastUid }, null, null, null);

			if (!loadConstant(c)) {
				Toast.makeText(Main.this,
						R.string.toast_login_acquired, Toast.LENGTH_LONG)
						.show();
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
                    .setText(getString(R.string.title_frag_atme).toUpperCase(Locale.US))
                    .setTabListener(tabListener));
			actionBar.addTab(actionBar.newTab()
					.setText(getString(R.string.title_frag_friends_timeline).toUpperCase(Locale.US))
					.setTabListener(tabListener));
			actionBar.addTab(actionBar.newTab()
					.setText(getString(R.string.title_frag_comments_to_me).toUpperCase(Locale.US))
					.setTabListener(tabListener));

			mSectionsPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
			mViewPager.setAdapter(mSectionsPagerAdapter);


			mViewPager
					.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
						@Override
						public void onPageSelected(int arg0) {
							actionBar.setSelectedNavigationItem(arg0);
							Log.e(TAG, "Page: " + String.valueOf(arg0));
							switch (arg0) {
							case MainPagerAdapter.FRAG_FRIENDSTIMELINE_POS: {

								break;
							}
							case MainPagerAdapter.FRAG_COMMENTS_POS: {
								if (!gotCommentsUnread) {
									mSectionsPagerAdapter.getCommentsFrag()
											.getUnreadCommentsCount();
									gotCommentsUnread = true;
								}
								break;
							}
							case MainPagerAdapter.FRAG_MENTIONS_POS: {
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

            mViewPager.setCurrentItem(MainPagerAdapter.FRAG_FRIENDSTIMELINE_POS);

		} else {// 不存在配置文件，需要登录
			Toast.makeText(Main.this,
					R.string.toast_login_acquired, Toast.LENGTH_LONG).show();
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

	// @Override
	// protected void onResume() {
	// super.onResume();
	// if (loadConstant(c))
	// Log.e(TAG, "Reload Weibo_Constants succeed");
	// else
	// Log.e(TAG, "Reload Weibo_Constants failed");
	// }

	ActionBar.TabListener tabListener = new ActionBar.TabListener() {

		@Override
		public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
			mViewPager.setCurrentItem(tab.getPosition());
//            setListViewToTop(tab.getPosition());
		}

		@Override
		public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}
	};

    public void setListViewToTop(int position){
        switch (position){
        case 1:{
            mSectionsPagerAdapter.getFriendsTimeLineFrag().setListViewToTop();
            break;
        }
        }
    }

	public PullToRefreshAttacher getPullToRefreshAttacher() {
		return mPullToRefreshAttacher;
	}

	public SharedPreferences.Editor getEditor() {
		return editor;
	}

	private void jumpToLogin() {
		startActivity(new Intent(Main.this, Login.class));
		finish();
	}

	private boolean loadConstant(Cursor c) {
		if (c.moveToFirst()) {
			Weibo_Constants.UID = c.getString(c.getColumnIndex(MyMaidSQLHelper.UID));
			Weibo_Constants.ACCESS_TOKEN = c.getString(c
					.getColumnIndex(MyMaidSQLHelper.ACCESS_TOKEN));
			Weibo_Constants.LOCATION = c.getString(c
					.getColumnIndex(MyMaidSQLHelper.LOCATION));
			Weibo_Constants.EXPIRES_IN = Integer.valueOf(c.getString(c
					.getColumnIndex(MyMaidSQLHelper.EXPIRES_IN)));
			Weibo_Constants.SCREEN_NAME = c.getString(c
					.getColumnIndex(MyMaidSQLHelper.SCREEN_NAME));
			return true;
		} else
			return false;

	}
}
