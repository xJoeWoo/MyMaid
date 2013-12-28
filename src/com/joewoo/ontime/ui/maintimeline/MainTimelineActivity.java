package com.joewoo.ontime.ui.maintimeline;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.joewoo.ontime.support.adapter.pager.MainPagerAdapter;
import com.joewoo.ontime.support.net.NetworkStatus;
import com.joewoo.ontime.support.util.GlobalContext;
import com.joewoo.ontime.ui.Login;
import com.joewoo.ontime.R;
import com.joewoo.ontime.support.sql.MyMaidSQLHelper;

import java.util.Locale;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

import static com.joewoo.ontime.support.info.Defines.LASTUID;
import static com.joewoo.ontime.support.info.Defines.PREFERENCES;
import static com.joewoo.ontime.support.info.Defines.TAG;

public class MainTimelineActivity extends FragmentActivity {

    MainPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    boolean gotCommentsUnread;
    boolean gotMentionsUnread;
    BitmapDrawable profileImg;
    byte[] profileImgBytes;
    private PullToRefreshAttacher mPullToRefreshAttacher;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Cursor c;

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.timeline_cmt_atme);

        Log.e(TAG, "MyMaid START!");

        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = preferences.edit();

        if (preferences.getString(LASTUID, null) != null) {

            String lastUid = preferences.getString(LASTUID, null);

            c = GlobalContext.getSQL().query(MyMaidSQLHelper.tableName, new String[]{MyMaidSQLHelper.UID,
                    MyMaidSQLHelper.ACCESS_TOKEN, MyMaidSQLHelper.SCREEN_NAME,
                    MyMaidSQLHelper.DRAFT, MyMaidSQLHelper.PROFILEIMG}, MyMaidSQLHelper.UID + "=?",
                    new String[]{lastUid}, null, null, null);

            if (c.moveToFirst() && c.getCount() > 0) {

                if (!setGlobalContext(c)) {
                    Toast.makeText(MainTimelineActivity.this,
                            R.string.toast_login_acquired, Toast.LENGTH_LONG)
                            .show();
                    jumpToLogin();
                }

                profileImgBytes = c.getBlob(c.getColumnIndex(MyMaidSQLHelper.PROFILEIMG));
                profileImg = new BitmapDrawable(getResources(), BitmapFactory
                        .decodeByteArray(profileImgBytes, 0, profileImgBytes.length));

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
                                }
                            }
                        });

                mViewPager.setCurrentItem(MainPagerAdapter.FRAG_FRIENDSTIMELINE_POS);
            } else { // 数据库没有信息
                Toast.makeText(MainTimelineActivity.this,
                        R.string.toast_login_acquired, Toast.LENGTH_LONG).show();
                jumpToLogin();
            }
        } else {// 不存在配置文件，需要登录
            Toast.makeText(MainTimelineActivity.this,
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

    public SharedPreferences.Editor getEditor() {
        return editor;
    }

    private void jumpToLogin() {
        startActivity(new Intent(MainTimelineActivity.this, Login.class));
        finish();
    }

    private boolean setGlobalContext(Cursor c) {
        try {
            GlobalContext.setUID(c.getString(c.getColumnIndex(MyMaidSQLHelper.UID)));
            GlobalContext.setAccessToken(c.getString(c
                    .getColumnIndex(MyMaidSQLHelper.ACCESS_TOKEN)));
            GlobalContext.setScreenName(c.getString(c
                    .getColumnIndex(MyMaidSQLHelper.SCREEN_NAME)));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }

    public void setActionBarLowProfile() {
        if (getActionBar().isShowing()) {
            getActionBar().hide();
            switch (Build.VERSION.SDK_INT) {
                case Build.VERSION_CODES.KITKAT:
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE);
                    break;

                default:
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    break;
            }
        }
    }

    public void setActionBarVisible() {
        if (!getActionBar().isShowing()) {
            getActionBar().show();
            getWindow().getDecorView().setSystemUiVisibility(View.VISIBLE);
        }
    }

    public boolean checkNetwork() {
        if (NetworkStatus.isNetworkAvailable()) {
            return true;
        } else {
            Toast.makeText(this, R.string.toast_no_network, Toast.LENGTH_SHORT).show();
            mPullToRefreshAttacher.setRefreshing(false);
            return false;
        }
    }

    public BitmapDrawable getProfileImage() {
        return profileImg;
    }

    public byte[] getProfileImgBytes() {
        return profileImgBytes;
    }
}
