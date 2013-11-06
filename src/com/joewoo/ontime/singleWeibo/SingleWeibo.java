package com.joewoo.ontime.singleWeibo;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.joewoo.ontime.Comment_Repost;
import com.joewoo.ontime.R;
import com.joewoo.ontime.action.Weibo_FavoritesCreate;
import com.joewoo.ontime.action.Weibo_StatusesDestroy;
import com.joewoo.ontime.bean.WeiboBackBean;
import com.joewoo.ontime.info.Weibo_Constants;

import java.util.HashMap;
import java.util.Locale;

import static com.joewoo.ontime.info.Constants.*;


/**
 * Created by JoeWoo on 13-10-13.
 */
public class SingleWeibo extends FragmentActivity {

    private SingleWeiboPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Intent i;
    private long downTime;
    private boolean isShowedComments = false;
    private boolean isShowedReposts = false;
    HashMap<String, String> map;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.singelweibo);

        i = getIntent();
        map = (HashMap<String, String>)i.getSerializableExtra(SINGLE_WEIBO_MAP);

        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(3);


        String titleRepost = getResources().getString(R.string.title_frag_single_weibo_reposts).toUpperCase(Locale.US);
        String titleComment = getResources().getString(R.string.title_frag_single_weibo_comments).toUpperCase(Locale.US);
        String repostCount = map.get(REPOSTS_COUNT);
        String commentsCount = map.get(COMMENTS_COUNT);

        if (Locale.getDefault().getLanguage().equals("en")) {
            if (!repostCount.equals("1"))
                titleRepost = titleRepost + "S";

            if (!commentsCount.equals("1"))
                titleComment = titleComment + "S";
        }

        actionBar.addTab(actionBar.newTab()
                .setText(repostCount + " " + titleRepost)
                .setTabListener(tabListener));

        actionBar.addTab(actionBar.newTab()
                .setText(getString(R.string.title_frag_single_weibo).toUpperCase(Locale.US))
                .setTabListener(tabListener));

        actionBar.addTab(actionBar.newTab()
                .setText(commentsCount + " " + titleComment)
                .setTabListener(tabListener));

        titleRepost = null;
        titleComment = null;
        repostCount = null;
        commentsCount = null;


        mSectionsPagerAdapter = new SingleWeiboPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int arg0) {
                        actionBar.setSelectedNavigationItem(arg0);
                        Log.e(TAG, "Page: " + String.valueOf(arg0));
                        switch (arg0) {
                            case SingleWeiboPagerAdapter.FRAG_SINGLEWEIBO_POS: {

                                break;
                            }
                            case SingleWeiboPagerAdapter.FRAG_SINGLEWEIBOCOMMENTS_POS: {

                                if (!isShowedComments)
                                    mSectionsPagerAdapter.getSingleWeiboCommentsFrag().showComments(map.get(WEIBO_ID));
                                isShowedComments = true;

                                break;
                            }
                            case SingleWeiboPagerAdapter.FRAG_SINGLEWEIBOREPOSTS_POS: {

                                if (!isShowedReposts)
                                    mSectionsPagerAdapter.getSingleWeiboRepostsFrag().showReposts(map.get(WEIBO_ID));
                                isShowedReposts = true;
                                break;
                            }
                        }
                    }
                });

        mViewPager.setCurrentItem(SingleWeiboPagerAdapter.FRAG_SINGLEWEIBO_POS);
    }

    Handler mHandler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {

            setProgressBarIndeterminateVisibility(false);

            switch (msg.what) {
                case GOT_FAVOURITE_CREATE_INFO: {

                    WeiboBackBean b = (WeiboBackBean) msg.obj;

                    if (b.getFavoritedTime() != null) {
                        Toast.makeText(SingleWeibo.this,
                                R.string.toast_add_favourite_success,
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(SingleWeibo.this,
                                R.string.toast_add_favourite_fail,
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case GOT_STATUSES_DESTROY_INFO: {
                    WeiboBackBean b = (WeiboBackBean) msg.obj;

                    if (b.getId() != null) {
                        Toast.makeText(SingleWeibo.this,
                                R.string.toast_delete_success, Toast.LENGTH_SHORT)
                                .show();
                        finish();
                    } else {
                        Toast.makeText(SingleWeibo.this,
                                R.string.toast_delete_fail, Toast.LENGTH_SHORT)
                                .show();
                    }
                    break;
                }

            }
        }

    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.clear();

        if (map.get(SCREEN_NAME).equals(Weibo_Constants.SCREEN_NAME)) {
            menu.add(0, MENU_STATUSES_DESTROY, 0, R.string.menu_delete)
                    .setIcon(R.drawable.content_discard)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        menu.add(0, MENU_FAVOURITE_CREATE, 0, R.string.menu_add_favourite)
                .setIcon(R.drawable.rating_favorite)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(0, MENU_REPOST, 0, R.string.menu_repost)
                .setIcon(R.drawable.social_reply)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(0, MENU_COMMENT_CREATE, 0, R.string.menu_comment)
                .setIcon(R.drawable.content_edit)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
            case MENU_REPOST: {
                Intent it = new Intent();
                it.setClass(SingleWeibo.this, Comment_Repost.class);
                if (map.get(IS_REPOST) != null)
                    it.putExtra(TEXT, "//@" + map.get(SCREEN_NAME) + ":"
                            + map.get(TEXT));
                it.putExtra(IS_REPOST, true);
                it.putExtra(WEIBO_ID, map.get(WEIBO_ID));
                startActivity(it);
                break;
            }
            case MENU_COMMENT_CREATE: {
                Intent it = new Intent();
                it.setClass(SingleWeibo.this, Comment_Repost.class);
                it.putExtra(IS_COMMENT, true);
                it.putExtra(WEIBO_ID, map.get(WEIBO_ID));
                startActivity(it);
                break;
            }
            case MENU_FAVOURITE_CREATE: {
                new Weibo_FavoritesCreate(map.get(WEIBO_ID), mHandler)
                        .start();
                setProgressBarIndeterminateVisibility(true);
                break;
            }
            case MENU_STATUSES_DESTROY: {
                if (System.currentTimeMillis() - downTime > 2000) {
                    Toast.makeText(SingleWeibo.this,
                            R.string.toast_press_again_to_delete_statuse,
                            Toast.LENGTH_SHORT).show();
                    downTime = System.currentTimeMillis();
                } else {
                    new Weibo_StatusesDestroy(map.get(WEIBO_ID), mHandler)
                            .start();
                    setProgressBarIndeterminateVisibility(true);
                }

                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public Intent getSingleWeiboIntent() {
        return i;
    }

    public HashMap<String, String> getSingleWeiboMap() {
        return map;
    }

    ActionBar.TabListener tabListener = new ActionBar.TabListener() {

        @Override
        public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
            mViewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
            // TODO Auto-generated method stub

        }
    };

}