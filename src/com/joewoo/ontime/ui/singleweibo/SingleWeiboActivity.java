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
import com.joewoo.ontime.support.bean.StatusesBean;
import com.joewoo.ontime.support.info.Defines;
import com.joewoo.ontime.support.listener.MyMaidListeners;
import com.joewoo.ontime.support.util.GlobalContext;
import com.joewoo.ontime.ui.CommentRepost;

import java.util.Locale;

import static com.joewoo.ontime.support.info.Defines.GOT_FAVOURITE_CREATE_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_FAVOURITE_CREATE_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.GOT_SET_SINGLE_WEIBO_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_STATUSES_DESTROY_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_STATUSES_DESTROY_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.GOT_STATUSES_SHOW_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_STATUSES_SHOW_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.IS_COMMENT;
import static com.joewoo.ontime.support.info.Defines.IS_REPOST;
import static com.joewoo.ontime.support.info.Defines.MENU_COMMENT_CREATE;
import static com.joewoo.ontime.support.info.Defines.MENU_FAVOURITE_CREATE;
import static com.joewoo.ontime.support.info.Defines.MENU_REPOST;
import static com.joewoo.ontime.support.info.Defines.MENU_STATUSES_DESTROY;
import static com.joewoo.ontime.support.info.Defines.STATUS;
import static com.joewoo.ontime.support.info.Defines.STATUS_BEAN;
import static com.joewoo.ontime.support.info.Defines.STATUS_BEAN_POSITION;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;


/**
 * Created by JoeWoo on 13-10-13.
 */
public class SingleWeiboActivity extends FragmentActivity implements MyMaidListeners.FragmentReadyListener {


    private SingleWeiboPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Intent i;
    private long downTime;
    private boolean isShowedComments = false;
    private boolean isShowedReposts = false;
    private StatusesBean status = null;
    private ActionBar actionBar;
    private byte[] imgBytes;
    private String weiboID = null;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putByteArray(Defines.PHOTO_BYTES, imgBytes);
        outState.putParcelable(Defines.STATUS_BEAN, status);

        Log.e(TAG, "save buddle");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            Log.e(TAG, "load buddle");
            imgBytes = savedInstanceState.getByteArray(Defines.PHOTO_BYTES);
            status = savedInstanceState.getParcelable(Defines.STATUS_BEAN);
            if (status != null)
                Log.e(TAG, "status not null: " + status.getText());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_singel_weibo);

        Log.e(TAG, "ON CREATE");

        initActionBar();
        addPager();
        addTabs();

        i = getIntent();

        if (i.getStringExtra(WEIBO_ID) != null) {
            weiboID = i.getStringExtra(WEIBO_ID);
        } else if (i.getParcelableExtra(STATUS_BEAN) != null) {
            Log.e(TAG, "get status from intent");
            status = i.getParcelableExtra(STATUS_BEAN);
            weiboID = status.getId();
        }

        if (status == null)
            MyMaidActionHelper.statusesShow(weiboID, mHandler);

        mViewPager.setCurrentItem(SingleWeiboPagerAdapter.FRAG_SINGLE_WEIBO_POS);
    }

    @Override
    public void fragmentReady() {
        if (status != null)
            mHandler.sendEmptyMessage(Defines.GOT_SET_SINGLE_WEIBO_INFO);
        else
            mSectionsPagerAdapter.getSingleWeiboFrag().setViewHide();
    }

    private Handler mHandler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GOT_STATUSES_SHOW_INFO: {
                    status = (StatusesBean) msg.obj;
                    weiboID = status.getId();
                    mHandler.sendEmptyMessage(GOT_SET_SINGLE_WEIBO_INFO);
                    break;
                }
                case GOT_SET_SINGLE_WEIBO_INFO: {

                    mSectionsPagerAdapter.getSingleWeiboFrag().setSingleWeibo(status);

                    setCommentsCount(status.getCommentsCount());
                    setRepostsCount(status.getCommentsCount());

                    invalidateOptionsMenu();

                    break;
                }
                case GOT_STATUSES_DESTROY_INFO: {
                    Intent ii = new Intent();
                    ii.putExtra(STATUS_BEAN_POSITION, i.getIntExtra(STATUS_BEAN_POSITION, -1));
                    setResult(RESULT_OK, ii);
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

        if (status != null && status.getUser().getScreenName() != null && status.getUser().getScreenName().equals(GlobalContext.getScreenName())) {
            menu.add(0, MENU_STATUSES_DESTROY, 0, R.string.menu_delete)
                    .setIcon(R.drawable.content_discard)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        if (status != null)
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
                ii.setClass(SingleWeiboActivity.this, CommentRepost.class);
                if (status.getRetweetedStatus() != null)
                    ii.putExtra(STATUS, "//@" + status.getUser().getScreenName() + ":"
                            + status.getText());
                ii.putExtra(IS_REPOST, true);
                ii.putExtra(WEIBO_ID, status.getId());
                startActivity(ii);
                break;
            }
            case MENU_COMMENT_CREATE: {
                Intent ii = new Intent();
                ii.setClass(SingleWeiboActivity.this, CommentRepost.class);
                ii.putExtra(IS_COMMENT, true);
                ii.putExtra(WEIBO_ID, status.getId());
                startActivity(ii);
                break;
            }
            case MENU_FAVOURITE_CREATE: {
                MyMaidActionHelper.favouriteCreate(status.getId(), mHandler);
                break;
            }
            case MENU_STATUSES_DESTROY: {
                if (System.currentTimeMillis() - downTime > 2000) {
                    Toast.makeText(SingleWeiboActivity.this,
                            R.string.toast_press_again_to_delete_statuse,
                            Toast.LENGTH_SHORT).show();
                    downTime = System.currentTimeMillis();
                } else {
                    MyMaidActionHelper.statusesDestroy(status.getId(), mHandler);
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
                    mSectionsPagerAdapter.getSingleWeiboCommentsFrag().showComments(weiboID);
                    break;
                case SingleWeiboPagerAdapter.FRAG_SINGLE_WEIBO_REPOSTS_POS:
                    mSectionsPagerAdapter.getSingleWeiboRepostsFrag().showReposts(weiboID);
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
        mSectionsPagerAdapter.getSingleWeiboFrag().setFragmentReadyListener(this);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int arg0) {
                        actionBar.setSelectedNavigationItem(arg0);
                        Log.e(TAG, "Page: " + String.valueOf(arg0));
                        switch (arg0) {
                            case SingleWeiboPagerAdapter.FRAG_SINGLE_WEIBO_POS: {

                                break;
                            }
                            case SingleWeiboPagerAdapter.FRAG_SINGLE_WEIBO_COMMENTS_POS: {
                                if (weiboID != null && !isShowedComments) {
                                    mSectionsPagerAdapter.getSingleWeiboCommentsFrag().showComments(weiboID);
                                    isShowedComments = true;
                                }
                                break;
                            }
                            case SingleWeiboPagerAdapter.FRAG_SINGLE_WEIBO_REPOSTS_POS: {
                                if (status != null && !isShowedReposts) {
                                    mSectionsPagerAdapter.getSingleWeiboRepostsFrag().showReposts(weiboID);
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

    public void setImageBytes(byte[] bytes) {
        imgBytes = bytes;
        Log.e(TAG, "Set image bytes: " + String.valueOf(imgBytes.length));
    }

    public byte[] getImageBytes() {
        return imgBytes;
    }

}