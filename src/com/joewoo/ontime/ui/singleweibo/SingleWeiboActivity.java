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
import com.joewoo.ontime.action.favorites.FavoritesCreate;
import com.joewoo.ontime.action.statuses.StatusesDestroy;
import com.joewoo.ontime.action.statuses.StatusesShow;
import com.joewoo.ontime.support.adapter.pager.SingleWeiboPagerAdapter;
import com.joewoo.ontime.support.bean.StatusesBean;
import com.joewoo.ontime.support.util.GlobalContext;
import com.joewoo.ontime.ui.CommentRepost;

import java.util.Locale;

import static com.joewoo.ontime.support.info.Defines.GOT_FAVOURITE_CREATE_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_FAVOURITE_CREATE_INFO_FAIL;
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
import static com.joewoo.ontime.support.info.Defines.STATUS_BEAN;
import static com.joewoo.ontime.support.info.Defines.STATUS_BEAN_POSITION;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.TEXT;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;


/**
 * Created by JoeWoo on 13-10-13.
 */
public class SingleWeiboActivity extends FragmentActivity {

    private SingleWeiboPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Intent i;
    private long downTime;
    private boolean isShowedComments = false;
    private boolean isShowedReposts = false;
    private StatusesBean status = null;
    private ActionBar actionBar;
    private String titleRepost;
    private String titleComment;
    private String weiboID = null;
    private boolean isFreshing;


    public void setSingleWeiboFragment() {
        if(status != null) {
            setSingleWeibo(false);
        } else {
            mSectionsPagerAdapter.getSingleWeiboFrag().setViewHide();
            new StatusesShow(weiboID, mHandler).start();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.singelweibo);

        i = getIntent();

        if(i.getStringExtra(WEIBO_ID) != null){
            weiboID = i.getStringExtra(WEIBO_ID);
        } else {
            status = i.getParcelableExtra(STATUS_BEAN);
            weiboID = status.getId();
        }

        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(3);

        titleRepost = getResources().getString(R.string.title_frag_single_weibo_reposts).toUpperCase(Locale.US);
        titleComment = getResources().getString(R.string.title_frag_single_weibo_comments).toUpperCase(Locale.US);

        actionBar.addTab(actionBar.newTab()
                .setText(titleRepost)
                .setTabListener(tabListener));

        actionBar.addTab(actionBar.newTab()
                .setText(getString(R.string.title_frag_single_weibo).toUpperCase(Locale.US))
                .setTabListener(tabListener));

        actionBar.addTab(actionBar.newTab()
                .setText(titleComment)
                .setTabListener(tabListener));

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
                                if (weiboID != null && !isShowedComments) {
                                    mSectionsPagerAdapter.getSingleWeiboCommentsFrag().showComments(weiboID);
                                    isShowedComments = true;
                                }
                                break;
                            }
                            case SingleWeiboPagerAdapter.FRAG_SINGLEWEIBOREPOSTS_POS: {
                                if (status != null && !isShowedReposts) {
                                    mSectionsPagerAdapter.getSingleWeiboRepostsFrag().showReposts(weiboID);
                                    isShowedReposts = true;
                                }
                                break;
                            }
                        }
                    }
                });

        mViewPager.setCurrentItem(SingleWeiboPagerAdapter.FRAG_SINGLEWEIBO_POS);

    }


    private Handler mHandler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {

            setProgressBarIndeterminateVisibility(false);

            switch (msg.what) {
                case GOT_STATUSES_SHOW_INFO: {
                    status = (StatusesBean) msg.obj;
                    weiboID = status.getId();
                    setSingleWeibo(true);
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
                it.setClass(SingleWeiboActivity.this, CommentRepost.class);
                if (status.getRetweetedStatus() != null)
                    it.putExtra(TEXT, "//@" + status.getUser().getScreenName() + ":"
                            + status.getText());
                it.putExtra(IS_REPOST, true);
                it.putExtra(WEIBO_ID, status.getId());
                startActivity(it);
                break;
            }
            case MENU_COMMENT_CREATE: {
                Intent it = new Intent();
                it.setClass(SingleWeiboActivity.this, CommentRepost.class);
                it.putExtra(IS_COMMENT, true);
                it.putExtra(WEIBO_ID, status.getId());
                startActivity(it);
                break;
            }
            case MENU_FAVOURITE_CREATE: {
                new FavoritesCreate(status.getId(), mHandler)
                        .start();
                setProgressBarIndeterminateVisibility(true);
                break;
            }
            case MENU_STATUSES_DESTROY: {
                if (System.currentTimeMillis() - downTime > 2000) {
                    Toast.makeText(SingleWeiboActivity.this,
                            R.string.toast_press_again_to_delete_statuse,
                            Toast.LENGTH_SHORT).show();
                    downTime = System.currentTimeMillis();
                } else {
                    new StatusesDestroy(status.getId(), mHandler)
                            .start();
                    setProgressBarIndeterminateVisibility(true);
                }

                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSingleWeibo(boolean withAnim) {
        String repostCount;
        String commentsCount;

        repostCount = status.getRepostsCount() + " ";
        commentsCount = status.getCommentsCount() + " ";

        if (Locale.getDefault().getLanguage().equals("en")) {
            if (!repostCount.equals("1"))
                titleRepost += "S";
            if (!commentsCount.equals("1"))
                titleComment += "S";
        }

        actionBar.getTabAt(0).setText(repostCount + titleRepost);
        actionBar.getTabAt(2).setText(commentsCount + titleComment);

        if(withAnim)
            mSectionsPagerAdapter.getSingleWeiboFrag().setSingleWeiboWithAnim(status);
        else
            mSectionsPagerAdapter.getSingleWeiboFrag().setSingleWeibo(status);

        titleRepost = null;
        titleComment = null;
        repostCount = null;
        commentsCount = null;

        invalidateOptionsMenu();
    }

    private ActionBar.TabListener tabListener = new ActionBar.TabListener() {

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

    public boolean isFreshing() {
        return isFreshing;
    }

    public void setFreshing(boolean isFreshing) {
        this.isFreshing = isFreshing;
    }

}