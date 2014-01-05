package com.joewoo.ontime.ui.maintimeline;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.joewoo.ontime.R;
import com.joewoo.ontime.action.statuses.StatusesFriendsTimeLine;
import com.joewoo.ontime.support.adapter.listview.MainListViewAdapter;
import com.joewoo.ontime.support.bean.StatusesBean;
import com.joewoo.ontime.support.dialog.UserChooserDialog;
import com.joewoo.ontime.support.net.NetworkStatus;
import com.joewoo.ontime.support.util.GlobalContext;
import com.joewoo.ontime.support.util.IDtoMID;
import com.joewoo.ontime.support.view.header.MainTimelineHeaderView;
import com.joewoo.ontime.ui.Post;
import com.joewoo.ontime.ui.SingleUser;
import com.joewoo.ontime.ui.singleweibo.SingleWeiboActivity;

import java.util.List;
import java.util.Locale;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;

import static com.joewoo.ontime.support.info.Defines.GOT_FRIENDS_TIMELINE_ADD_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_FRIENDS_TIMELINE_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_FRIENDS_TIMELINE_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.MENU_POST;
import static com.joewoo.ontime.support.info.Defines.MENU_PROFILE_IMAGE;
import static com.joewoo.ontime.support.info.Defines.MENU_UNREAD_COUNT;
import static com.joewoo.ontime.support.info.Defines.RESULT_DESTROYED_WEIBO;
import static com.joewoo.ontime.support.info.Defines.SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.STATUS_BEAN;
import static com.joewoo.ontime.support.info.Defines.STATUS_BEAN_POSITION;
import static com.joewoo.ontime.support.info.Defines.TAG;


public class FriendsTimeLineFragment extends Fragment implements OnRefreshListener {

    private List<StatusesBean> statuses;
    private ListView lv;
    private MainListViewAdapter mAdapter;
    private PullToRefreshAttacher mPullToRefreshAttacher;
    private MainTimelineActivity act;

    @Override
    public void onRefreshStarted(View view) {
        Log.e(TAG, "Refresh StatusesFriendsTimeLine");
        if (NetworkStatus.check(true)) {
            refreshFriendsTimeLine();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.friendstimeline, container, false);

        lv = (ListView) v.findViewById(R.id.lv_friends_timeline);
        lv.setDivider(null);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        act = (MainTimelineActivity) getActivity();

        mPullToRefreshAttacher = (act)
                .getPullToRefreshAttacher();
        mPullToRefreshAttacher.addRefreshableView(lv, this);

        new StatusesFriendsTimeLine(true, mHandler).start();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent i = new Intent(act, SingleWeiboActivity.class);
                i.putExtra(STATUS_BEAN, statuses.get(arg2 - lv.getHeaderViewsCount()));
                i.putExtra(STATUS_BEAN_POSITION, arg2 - lv.getHeaderViewsCount());
                startActivityForResult(i, RESULT_DESTROYED_WEIBO);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri
                        .parse("http://weibo.com/"
                                + statuses.get(arg2 - lv.getHeaderViewsCount()).getUser().getId()
                                + "/"
                                + IDtoMID.Id2Mid(statuses.get(arg2 - lv.getHeaderViewsCount())
                                .getId()))));
                return false;
            }
        });

        lv.setOnScrollListener(new OnScrollListener() {
            int mLastFirstVisibleItem = 0;

            @Override
            public void onScroll(AbsListView view, int arg1, int arg2, int arg3) {

                // 检查上下滚动
                final int currentFirstVisibleItem = view.getFirstVisiblePosition();

                if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                    act.setActionBarLowProfile();
                } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                    act.setActionBarVisible();
                }
                mLastFirstVisibleItem = currentFirstVisibleItem;

                // 滚到到尾刷新
                if (view.getLastVisiblePosition() > (view.getCount() - 6) && !mPullToRefreshAttacher.isRefreshing() && statuses != null) {
                    Log.e(TAG, "到底");
                    // 获取后会删除第一项，所以获取数+1
                    new StatusesFriendsTimeLine(statuses.get(view.getCount() - 1 - lv.getHeaderViewsCount()).getId(), mHandler).start();
                    mPullToRefreshAttacher.setRefreshing(true);
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });

        mAdapter = new MainListViewAdapter(act);
        lv.addHeaderView(new MainTimelineHeaderView(act), null, false);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        menu.clear();

        try {
            menu.add(0, MENU_PROFILE_IMAGE, 0, R.string.menu_user_statuses)
                    .setIcon(GlobalContext.getProfileImg())
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        } catch (Exception e) {
            Log.e(TAG, "Profile image length: (Timeline) ERROR!");
            e.printStackTrace();
        }

        menu.add(0, MENU_UNREAD_COUNT, 0,
                GlobalContext.getScreenName().toUpperCase(Locale.US))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(0, MENU_POST, 0, R.string.menu_post)
                .setIcon(R.drawable.social_send_now)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {

                break;
            }
            case MENU_POST: {
                startActivity(new Intent(act, Post.class));
                break;
            }
            case MENU_PROFILE_IMAGE: {

                UserChooserDialog.show(act);

//                Cursor cursor = GlobalContext.getSQL().query(MyMaidSQLHelper.USER_TABLE, new String[]{
//                        MyMaidSQLHelper.UID, MyMaidSQLHelper.SCREEN_NAME}, null, null, null,
//                        null, null);
//                Log.e(MyMaidSQLHelper.TAG_SQL, "Queried users");
//
//                if (cursor.getCount() > 0) {
//                    final String[] singleUid = new String[cursor.getCount() + 2];
//                    final String[] singleUser = new String[cursor.getCount() + 2];
//
//                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
//                            .moveToNext()) {
//
//                        singleUid[cursor.getPosition()] = cursor.getString(0);
//                        singleUser[cursor.getPosition()] = cursor.getString(1);
//                        Log.e(TAG, "Cursor position - " + cursor.getPosition());
//                        Log.e(TAG, "Single Uid - "
//                                + singleUid[cursor.getPosition()]);
//                        Log.e(TAG,
//                                "Single User - " + singleUser[cursor.getPosition()]);
//                        Log.e(TAG, LOG_DEVIDER);
//                    }
//
//                    singleUser[cursor.getCount()] = act
//                            .getResources()
//                            .getString(
//                                    R.string.frag_ftl_dialog_choose_account_add_account);
//                    singleUid[cursor.getCount()] = "0";
//
//                    singleUser[cursor.getCount() + 1] = act
//                            .getResources().getString(
//                                    R.string.frag_ftl_dialog_choose_account_logout);
//                    singleUid[cursor.getCount() + 1] = "1";
//
//                    new AlertDialog.Builder(act)
//                            .setTitle(R.string.frag_ftl_dialog_choose_account_title)
//                            .setItems(singleUser, new OnClickListener() {
//
//                                @Override
//                                public void onClick(DialogInterface dialog,
//                                                    int which) {
//
//                                    Log.e(TAG, "Chose UID: " + singleUid[which]);
//                                    Log.e(TAG, "Chose Screen Name: "
//                                            + singleUid[which]);
//
//                                    if (!singleUid[which].equals("0")
//                                            && !singleUid[which].equals("1")) {
//
//                                        act.finish();
//                                        startActivity(new Intent(act,
//                                                MainTimelineActivity.class));
//                                    } else if (singleUid[which].equals("0")) {
//                                        startActivity(new Intent(act,
//                                                Login.class));
//                                        act.finish();
//                                    } else if (singleUid[which].equals("1")) {
//
//                                        new AlertDialog.Builder(act,
//                                                AlertDialog.THEME_HOLO_LIGHT)
//                                                .setTitle(
//                                                        R.string.frag_ftl_dialog_confirm_logout_title)
//                                                .setPositiveButton(
//                                                        R.string.frag_ftl_dialog_confirm_logout_btn_ok,
//                                                        new OnClickListener() {
//
//                                                            @Override
//                                                            public void onClick(
//                                                                    DialogInterface dialog,
//                                                                    int which) {
//
//                                                                if (GlobalContext.getSQL().delete(
//                                                                        MyMaidSQLHelper.USER_TABLE,
//                                                                        MyMaidSQLHelper.UID
//                                                                                + "=?",
//                                                                        new String[]{GlobalContext.getUID()}) > 0) {
//                                                                    Log.e(MyMaidSQLHelper.TAG_SQL,
//                                                                            "LOGOUT - Cleared user info");
//
//                                                                    Toast.makeText(
//                                                                            act,
//                                                                            "<(￣︶￣)>",
//                                                                            Toast.LENGTH_SHORT)
//                                                                            .show();
//                                                                    startActivity(new Intent(
//                                                                            act,
//                                                                            Login.class));
//                                                                    act
//                                                                            .finish();
//                                                                }
//                                                            }
//                                                        })
//                                                .setNegativeButton(
//                                                        R.string.frag_ftl_dialog_confirm_logout_btn_cancle,
//                                                        null).show();
//                                    }
//                                }
//                            }).setNegativeButton(android.R.string.cancel, null).show();
//                } else {
//
//                }

                break;
            }
            case MENU_UNREAD_COUNT: {
                if (NetworkStatus.check(true)) {
                    Intent ii = new Intent(act, SingleUser.class);
                    ii.putExtra(SCREEN_NAME, GlobalContext.getScreenName());
                    startActivity(ii);
                }
                break;
            }
        }
        act.invalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // act.setProgressBarIndeterminateVisibility(false);
            mPullToRefreshAttacher.setRefreshComplete();
            switch (msg.what) {
//                case GOT_FRIENDS_IDS_INFO: {
//                    new StatusesFriendsTimeLine(true, GlobalContext.getSQL(), mHandler).start();
//                    break;
//                }
                case GOT_FRIENDS_TIMELINE_INFO: {
                    statuses = (List<StatusesBean>) msg.obj;
                    setListView(statuses);
                    break;
                }
                case GOT_FRIENDS_TIMELINE_ADD_INFO: {
                    statuses.addAll((List<StatusesBean>) msg.obj);
                    setListView(statuses);
                    break;
                }
                case GOT_FRIENDS_TIMELINE_INFO_FAIL: {
                    Toast.makeText(act,
                            (String) msg.obj, Toast.LENGTH_SHORT)
                            .show();
                    break;
                }
            }
            act.invalidateOptionsMenu();
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

    private void setListView(List<StatusesBean> statuses) {
        mAdapter.setData(statuses);
        if(lv.getAdapter() == null)
            lv.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void clearListViewItem(List<StatusesBean> statuses, int position) {
        statuses.remove(position);
        setListView(statuses);
    }

    public void refreshFriendsTimeLine() {
        new StatusesFriendsTimeLine(false, mHandler).start();
        mPullToRefreshAttacher.setRefreshing(true);
    }

    public void scrollListViewToTop() { lv.smoothScrollToPosition(0); }
}
