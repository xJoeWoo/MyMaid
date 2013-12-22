package com.joewoo.ontime.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.joewoo.ontime.R;
import com.joewoo.ontime.action.statuses.StatusesUserTimeLine;
import com.joewoo.ontime.action.user.UserShow;
import com.joewoo.ontime.support.adapter.listview.MainListViewAdapter;
import com.joewoo.ontime.support.bean.StatusesBean;
import com.joewoo.ontime.support.bean.WeiboBackBean;
import com.joewoo.ontime.support.info.AcquireCount;
import com.joewoo.ontime.support.net.ProfileImage;
import com.joewoo.ontime.support.view.UserTimelineHeaderView;
import com.joewoo.ontime.ui.singleweibo.SingleWeiboActivity;

import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

import static com.joewoo.ontime.support.info.Defines.GOT_PROFILEIMG_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_SHOW_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_SHOW_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.GOT_USER_TIMELINE_ADD_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_USER_TIMELINE_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_USER_TIMELINE_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.MENU_FOLLOWERS_COUNT;
import static com.joewoo.ontime.support.info.Defines.MENU_FRIENDS_COUNT;
import static com.joewoo.ontime.support.info.Defines.MENU_STATUSES_COUNT;
import static com.joewoo.ontime.support.info.Defines.RESULT_DESTROYED_WEIBO;
import static com.joewoo.ontime.support.info.Defines.SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.STATUS_BEAN;
import static com.joewoo.ontime.support.info.Defines.STATUS_BEAN_POSITION;
import static com.joewoo.ontime.support.info.Defines.TAG;

public class SingleUser extends Activity implements PullToRefreshAttacher.OnRefreshListener {

    private ListView lv;
    private List<StatusesBean> statuses;
    private String screenName = null;
    private String followersCount = null;
    private String friendsCount = null;
    private String statusesCount = null;
    private PullToRefreshAttacher mPullToRefreshAttacher;
    private MainListViewAdapter mAdapter;
    private UserTimelineHeaderView headerView;

    @Override
    public void onRefreshStarted(View view) {

        refreshTimeLine();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singleuser);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
//        getActionBar().hide();
        findViews();
        lv.setDivider(null);
        mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
        mPullToRefreshAttacher.setRefreshing(true);
        mPullToRefreshAttacher.addRefreshableView(lv, this);

        screenName = getIntent().getStringExtra(SCREEN_NAME);
        setTitle(screenName);

        refreshTimeLine();

        headerView = new UserTimelineHeaderView(this);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent ii = new Intent(SingleUser.this, SingleWeiboActivity.class);
                StatusesBean b = statuses.get(arg2 - lv.getHeaderViewsCount());
                ii.putExtra(STATUS_BEAN, b);
                ii.putExtra(STATUS_BEAN_POSITION, arg2 - lv.getHeaderViewsCount());
                startActivityForResult(ii, RESULT_DESTROYED_WEIBO);
                b = null;
            }
        });

        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 滚到到尾刷新
                if (view.getCount() > (Integer.valueOf(AcquireCount.USER_TIMELINE_COUNT) - 2) && view.getLastVisiblePosition() > (view.getCount() - 6) && !mPullToRefreshAttacher.isRefreshing() && statuses != null) {
                    Log.e(TAG, "到底");
                    new StatusesUserTimeLine(screenName, statuses.get(view.getCount() - 1 - ((ListView) view).getHeaderViewsCount()).getId(), mHandler).start();
                    mPullToRefreshAttacher.setRefreshing(true);
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });

        mAdapter = new MainListViewAdapter(this);
        lv.addHeaderView(headerView, null, false);
    }

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

                    headerView.setDescription(b.getLocation() + "\n\n" + b.getDescription());

                    new ProfileImage(b.getAvatarLarge(), mHandler)
                            .start();
                    break;
                }
                case GOT_PROFILEIMG_INFO: {
//                    byte[] img = (byte[]) msg.obj;
//                    getActionBar().setLogo(
//                            new BitmapDrawable(getResources(), BitmapFactory
//                                    .decodeByteArray(img, 0, img.length)));
                    headerView.setImageView((byte[]) msg.obj);
                    break;
                }
                case GOT_USER_TIMELINE_INFO: {
                    statuses = (List<StatusesBean>) msg.obj;
                    setListView(statuses);
                    mPullToRefreshAttacher.setRefreshing(false);
                    break;
                }
                case GOT_USER_TIMELINE_ADD_INFO: {
                    statuses.addAll((List<StatusesBean>) msg.obj);
                    setListView(statuses);
                    mPullToRefreshAttacher.setRefreshing(false);
                    break;
                }
                case GOT_USER_TIMELINE_INFO_FAIL: {
                    Toast.makeText(SingleUser.this, (String) msg.obj, Toast.LENGTH_SHORT)
                            .show();
                    mPullToRefreshAttacher.setRefreshing(false);
                    break;
                }
                case GOT_SHOW_INFO_FAIL: {
                    Toast.makeText(SingleUser.this, (String) msg.obj, Toast.LENGTH_SHORT)
                            .show();
                    mPullToRefreshAttacher.setRefreshing(false);
                    finish();
                    break;
                }
            }
            invalidateOptionsMenu();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_DESTROYED_WEIBO: {
                if (data != null) {
                    int pos;
                    if ((pos = data.getIntExtra(STATUS_BEAN_POSITION, -1)) != -1) {
                        clearListViewItem(statuses, pos);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();

        if (friendsCount == null)
            menu.add(0, MENU_FRIENDS_COUNT, 0, "").setShowAsAction(
                    MenuItem.SHOW_AS_ACTION_ALWAYS);
        else
            menu.add(0, MENU_FRIENDS_COUNT, 0, getResources().getString(R.string.menu_fan_xxx) + friendsCount)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        if (followersCount == null)
            menu.add(0, MENU_FOLLOWERS_COUNT, 0, "").setShowAsAction(
                    MenuItem.SHOW_AS_ACTION_ALWAYS);
        else
            menu.add(0, MENU_FOLLOWERS_COUNT, 0, followersCount + getResources().getString(R.string.menu_xxx_fans))
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        if (statusesCount == null)
            menu.add(0, MENU_STATUSES_COUNT, 0, "").setShowAsAction(
                    MenuItem.SHOW_AS_ACTION_ALWAYS);
        else
            menu.add(0, MENU_STATUSES_COUNT, 0, statusesCount + getResources().getString(R.string.menu_xxx_statuses))
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

    private void setListView(List<StatusesBean> statuses) {
        mAdapter.setData(statuses);
        if(lv.getAdapter() == null)
            lv.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void clearListViewItem(List<StatusesBean> statuses, int position) {
        statuses.remove(position);
        mAdapter.setData(statuses);
        mAdapter.notifyDataSetChanged();
    }

    private void refreshTimeLine() {
        Log.e(TAG, "Screen Name: " + screenName);
        new StatusesUserTimeLine(screenName, mHandler).start();
        new UserShow(screenName, mHandler).start();
    }
}
