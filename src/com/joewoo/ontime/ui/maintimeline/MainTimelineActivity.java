package com.joewoo.ontime.ui.maintimeline;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.joewoo.ontime.R;
import com.joewoo.ontime.action.aqi.AQIDetails;
import com.joewoo.ontime.support.adapter.pager.MainPagerAdapter;
import com.joewoo.ontime.support.dialog.UserChooserDialog;
import com.joewoo.ontime.support.setting.MyMaidSettingsHelper;
import com.joewoo.ontime.support.util.GlobalContext;

import java.util.Locale;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

import static com.joewoo.ontime.support.info.Defines.TAG;

public class MainTimelineActivity extends FragmentActivity {

    private MainPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private boolean gotCommentsUnread;
    private boolean gotMentionsUnread;
    private PullToRefreshAttacher mPullToRefreshAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        Log.e(TAG, "MyMaid Main Activity CREATE!");

        if (MyMaidSettingsHelper.getBoolean(MyMaidSettingsHelper.WEATHER_STATUS))
            new AQIDetails().start();

        if (GlobalContext.getAccessToken() != null) {

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
                            if (arg0 < mSectionsPagerAdapter.getCount() - 1)
                                actionBar.setSelectedNavigationItem(arg0);
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
                                    setActionBarVisible();
                                    break;
                                }
                                case MainPagerAdapter.FRAG_MENTIONS_POS: {
                                    if (!gotMentionsUnread) {
                                        mSectionsPagerAdapter.getMentionsFrag()
                                                .getUnreadMentionsCount();
                                        gotMentionsUnread = true;
                                    }
                                    setActionBarVisible();
                                    break;
                                }
                                case MainPagerAdapter.FRAG_SETTINGS_POS: {
                                    actionBar.hide();
                                    break;
                                }
                            }
                        }
                    });

            mViewPager.setCurrentItem(MainPagerAdapter.FRAG_FRIENDSTIMELINE_POS);

        } else {// 不存在用户信息
            new UserChooserDialog().show(this);
        }

    }

    @Override
    protected void onResume() {
        setActionBarVisible();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        super.onResume();
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

    private ActionBar.TabListener tabListener = new ActionBar.TabListener() {

        @Override
        public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
            switch (tab.getPosition()) {
                case MainPagerAdapter.FRAG_FRIENDSTIMELINE_POS:
                    mSectionsPagerAdapter.getFriendsTimeLineFrag().scrollListViewToTop();
                    break;
                case MainPagerAdapter.FRAG_MENTIONS_POS:
                    mSectionsPagerAdapter.getMentionsFrag().scrollListViewToTop();
                    break;
                case MainPagerAdapter.FRAG_COMMENTS_POS:
                    mSectionsPagerAdapter.getCommentsFrag().scrollListViewToTop();
                    break;
            }
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

    public void setActionBarLowProfile() {
        if (getActionBar().isShowing()) {
            getActionBar().hide();
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

        }
    }

    public void setActionBarVisible() {
        if (!getActionBar().isShowing()) {
            getActionBar().show();
            getWindow().getDecorView().setSystemUiVisibility(View.VISIBLE);
        }
    }
}
