package com.joewoo.ontime.fragment;

import static com.joewoo.ontime.info.Defines.*;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.joewoo.ontime.R;
import com.joewoo.ontime.info.WeiboConstant;

public class Timeline_Comments_Mentions extends FragmentActivity {

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline_cmt_atme);

		Log.e(TAG, "测试 ACTIVITY");

		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(3);

		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int arg0) {
						actionBar.setSelectedNavigationItem(arg0);
						Log.e(TAG, "Page " + String.valueOf(arg0));
						switch (arg0) {
						case 0: {
							
							break;
						}
						case 1: {
							
							break;
						}
						case 2: {

							break;
						}
						}
					}
				});

		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabReselected(Tab tab,
					android.app.FragmentTransaction ft) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTabSelected(Tab tab,
					android.app.FragmentTransaction ft) {
				mViewPager.setCurrentItem(tab.getPosition());

			}

			@Override
			public void onTabUnselected(Tab tab,
					android.app.FragmentTransaction ft) {
				// TODO Auto-generated method stub

			}
		};

		actionBar.addTab(actionBar.newTab()
				.setText(getString(R.string.title_friends_timeline))
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab()
				.setText(getString(R.string.title_comments_to_me))
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab()
				.setText(getString(R.string.title_atme))
				.setTabListener(tabListener));

		FragmentManager mFragmentManager = getSupportFragmentManager();

		mSectionsPagerAdapter = new SectionsPagerAdapter(mFragmentManager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		Log.e(TAG, WeiboConstant.ACCESS_TOKEN);

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
}
