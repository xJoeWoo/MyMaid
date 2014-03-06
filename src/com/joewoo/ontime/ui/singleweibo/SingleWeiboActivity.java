package com.joewoo.ontime.ui.singleweibo;

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

import com.joewoo.ontime.R;
import com.joewoo.ontime.action.MyMaidActionHelper;
import com.joewoo.ontime.support.adapter.pager.SingleWeiboPagerAdapter;
import com.joewoo.ontime.support.util.GlobalContext;
import com.joewoo.ontime.ui.CommentRepostActivity;

import java.util.Locale;

import static com.joewoo.ontime.support.info.Defines.GOT_FAVOURITE_CREATE_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_FAVOURITE_CREATE_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.GOT_STATUSES_DESTROY_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_STATUSES_DESTROY_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.GOT_STATUSES_SHOW_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.IS_COMMENT;
import static com.joewoo.ontime.support.info.Defines.IS_REPOST;
import static com.joewoo.ontime.support.info.Defines.MENU_COMMENT_CREATE;
import static com.joewoo.ontime.support.info.Defines.MENU_FAVOURITE_CREATE;
import static com.joewoo.ontime.support.info.Defines.MENU_REPOST;
import static com.joewoo.ontime.support.info.Defines.MENU_STATUSES_DESTROY;
import static com.joewoo.ontime.support.info.Defines.STATUS;
import static com.joewoo.ontime.support.info.Defines.STATUS_BEAN_POSITION;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;


/**
 * Created by JoeWoo on 13-10-13.
 */
public class SingleWeiboActivity extends FragmentActivity {


    private SingleWeiboPagerAdapter mSectionsPagerAdapter;
    private SingleWeiboFragment singleWeiboFragment;
    private ViewPager mViewPager;
    private long downTime;
    private boolean isShowedComments = false;
    private boolean isShowedReposts = false;
    private ActionBar actionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_singel_weibo);

        Log.e(TAG, "ON CREATE");

        initActionBar();
        addPager();
        addTabs();

        mViewPager.setCurrentItem(SingleWeiboPagerAdapter.FRAG_SINGLE_WEIBO_POS);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GOT_STATUSES_DESTROY_INFO: {
                    Intent ii = new Intent();
                    ii.putExtra(STATUS_BEAN_POSITION, getIntent().getIntExtra(STATUS_BEAN_POSITION, -1));
                    setResult(RESULT_OK, ii);
                    finish();
                    break;
                }
                case GOT_FAVOURITE_CREATE_INFO: {
                    Toast.makeText(SingleWeiboActivity.this,
                            (String) msg.obj,
                            Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                }

                case GOT_STATUSES_SHOW_INFO_FAIL:
                case GOT_FAVOURITE_CREATE_INFO_FAIL:
                case GOT_STATUSES_DESTROY_INFO_FAIL: {
                    Toast.makeText(SingleWeiboActivity.this,
                            (String) msg.obj,
                            Toast.LENGTH_SHORT).show();
                    break;
                }

            }
            invalidateOptionsMenu();
        }

    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.clear();

        if (singleWeiboFragment.getStatus() != null && singleWeiboFragment.getStatus().getUser().getScreenName() != null && singleWeiboFragment.getStatus().getUser().getScreenName().equals(GlobalContext.getScreenName())) {
            menu.add(0, MENU_STATUSES_DESTROY, 0, R.string.menu_delete)
                    .setIcon(R.drawable.content_discard)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        if (singleWeiboFragment.getWeiboID() != null)
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
                Intent ii = new Intent();
                ii.setClass(SingleWeiboActivity.this, CommentRepostActivity.class);
                if (singleWeiboFragment.getStatus().getRetweetedStatus() != null)
                    ii.putExtra(STATUS, "//@" + singleWeiboFragment.getStatus().getUser().getScreenName() + ":"
                            + singleWeiboFragment.getStatus().getText());
                ii.putExtra(IS_REPOST, true);
                ii.putExtra(WEIBO_ID, singleWeiboFragment.getStatus().getId());
                startActivity(ii);
                break;
            }
            case MENU_COMMENT_CREATE: {
                Intent ii = new Intent();
                ii.setClass(SingleWeiboActivity.this, CommentRepostActivity.class);
                ii.putExtra(IS_COMMENT, true);
                ii.putExtra(WEIBO_ID, singleWeiboFragment.getStatus().getId());
                startActivity(ii);
                break;
            }
            case MENU_FAVOURITE_CREATE: {
                MyMaidActionHelper.favouriteCreate(singleWeiboFragment.getStatus().getId(), mHandler);
                break;
            }
            case MENU_STATUSES_DESTROY: {
                if (System.currentTimeMillis() - downTime > 2000) {
                    Toast.makeText(SingleWeiboActivity.this,
                            R.string.toast_press_again_to_delete_statuse,
                            Toast.LENGTH_SHORT).show();
                    downTime = System.currentTimeMillis();
                } else {
                    MyMaidActionHelper.statusesDestroy(singleWeiboFragment.getStatus().getId(), mHandler);
                }

                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private ActionBar.TabListener tabListener = new ActionBar.TabListener() {

        @Override
        public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
            switch (tab.getPosition()) {
                case SingleWeiboPagerAdapter.FRAG_SINGLE_WEIBO_COMMENTS_POS:
                    mSectionsPagerAdapter.getSingleWeiboCommentsFrag().showComments();
                    break;
                case SingleWeiboPagerAdapter.FRAG_SINGLE_WEIBO_REPOSTS_POS:
                    mSectionsPagerAdapter.getSingleWeiboRepostsFrag().showReposts();
                    break;
            }

        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
            mViewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

        }
    };

    public void setCommentsCount(int commentsCount) {
        ActionBar.Tab tab = actionBar.getTabAt(SingleWeiboPagerAdapter.FRAG_SINGLE_WEIBO_COMMENTS_POS);
        tab.setText(commentsCount + " " + checkPlural(getString(R.string.title_frag_single_weibo_comments), commentsCount));
    }

    public void setRepostsCount(int repostsCount) {
        ActionBar.Tab tab = actionBar.getTabAt(SingleWeiboPagerAdapter.FRAG_SINGLE_WEIBO_REPOSTS_POS);
        tab.setText(repostsCount + " " + checkPlural(getString(R.string.title_frag_single_weibo_reposts), repostsCount));
    }

    private String checkPlural(String title, int count) {
        if (Locale.getDefault().getLanguage().equals("en")) {
            if (count != 1 && !title.endsWith("S"))
                title += "S";
        }
        return title;
    }

    private void initActionBar() {
        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
    }

    private void addPager() {
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(3);
        mSectionsPagerAdapter = new SingleWeiboPagerAdapter(getSupportFragmentManager());
        singleWeiboFragment = mSectionsPagerAdapter.getSingleWeiboFrag();
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int arg0) {
                        actionBar.setSelectedNavigationItem(arg0);
                        switch (arg0) {
                            case SingleWeiboPagerAdapter.FRAG_SINGLE_WEIBO_POS: {

                                break;
                            }
                            case SingleWeiboPagerAdapter.FRAG_SINGLE_WEIBO_COMMENTS_POS: {
                                if (singleWeiboFragment.getWeiboID() != null && !isShowedComments) {
                                    mSectionsPagerAdapter.getSingleWeiboCommentsFrag().showComments();
                                    isShowedComments = true;
                                }
                                break;
                            }
                            case SingleWeiboPagerAdapter.FRAG_SINGLE_WEIBO_REPOSTS_POS: {
                                if (singleWeiboFragment.getWeiboID() != null && !isShowedReposts) {
                                    mSectionsPagerAdapter.getSingleWeiboRepostsFrag().showReposts();
                                    isShowedReposts = true;
                                }
                                break;
                            }
                        }
                    }
                });


    }

    private void addTabs() {
        actionBar.addTab(actionBar.newTab()
                .setText(getString(R.string.title_frag_single_weibo_reposts).toUpperCase(Locale.US))
                .setTabListener(tabListener));

        actionBar.addTab(actionBar.newTab()
                .setText(getString(R.string.title_frag_single_weibo).toUpperCase(Locale.US))
                .setTabListener(tabListener));

        actionBar.addTab(actionBar.newTab()
                .setText(getString(R.string.title_frag_single_weibo_comments).toUpperCase(Locale.US))
                .setTabListener(tabListener));
    }

    public Intent getActIntent() {
        return getIntent();
    }

    public SingleWeiboFragment getSingleWeiboFragment() {
        return singleWeiboFragment;
    }
}