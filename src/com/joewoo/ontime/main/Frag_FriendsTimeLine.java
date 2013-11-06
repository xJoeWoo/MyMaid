package com.joewoo.ontime.main;

import static com.joewoo.ontime.info.Constants.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;

import com.joewoo.ontime.Login;
import com.joewoo.ontime.Post;
import com.joewoo.ontime.R;
import com.joewoo.ontime.SingleUser;
import com.joewoo.ontime.info.Weibo_AcquireCount;
import com.joewoo.ontime.info.Weibo_Constants;
import com.joewoo.ontime.singleWeibo.SingleWeibo;
import com.joewoo.ontime.action.Weibo_FriendsTimeLine;
import com.joewoo.ontime.tools.Id2MidUtil;
import com.joewoo.ontime.tools.MyMaidAdapter;
import com.joewoo.ontime.tools.MyMaidSQLHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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

@SuppressLint("HandlerLeak")
public class Frag_FriendsTimeLine extends Fragment implements OnRefreshListener {

    ArrayList<HashMap<String, String>> text;
    ListView lv;
    MyMaidSQLHelper sqlHelper;
    SQLiteDatabase sql;
    boolean isRefreshing = true;
    MyMaidAdapter mAdapter;
    private PullToRefreshAttacher mPullToRefreshAttacher;
    byte[] profileImg;
    private Activity act;

    @Override
    public void onRefreshStarted(View view) {
        Log.e(TAG, "Refresh FriendsTimeLine");
        refreshFriendsTimeLine();
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

        act = getActivity();

        mPullToRefreshAttacher = ((Main) act)
                .getPullToRefreshAttacher();
        mPullToRefreshAttacher.addRefreshableView(lv, this);

        sqlHelper = new MyMaidSQLHelper(act, MyMaidSQLHelper.SQL_NAME, null, MyMaidSQLHelper.SQL_VERSION);
        sql = sqlHelper.getReadableDatabase();
        Cursor c = sql.query(MyMaidSQLHelper.tableName, new String[]{
                MyMaidSQLHelper.FRIENDS_TIMELINE, MyMaidSQLHelper.PROFILEIMG},
                MyMaidSQLHelper.UID + "=?", new String[]{Weibo_Constants.UID}, null,
                null, null);

        if (c != null && c.moveToFirst()) {
            profileImg = c.getBlob(c.getColumnIndex(MyMaidSQLHelper.PROFILEIMG));
            try {
                if (!c.getString(c
                        .getColumnIndex(MyMaidSQLHelper.FRIENDS_TIMELINE)).equals(""))
                    new Weibo_FriendsTimeLine(c.getString(c
                            .getColumnIndex(MyMaidSQLHelper.FRIENDS_TIMELINE)), mHandler).start();

            } catch (Exception e) {
                refreshFriendsTimeLine();
            }
        } else {

        }


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent i = new Intent(act, SingleWeibo.class);
                i.putExtra(SINGLE_WEIBO_MAP, text.get(arg2));
                startActivity(i);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri
                        .parse("http://weibo.com/"
                                + text.get(arg2).get(UID)
                                + "/"
                                + Id2MidUtil.Id2Mid(text.get(arg2)
                                .get(WEIBO_ID)))));
                return false;
            }
        });

        lv.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case OnScrollListener.SCROLL_STATE_IDLE: { // 已经停止
                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                            if (!isRefreshing) {
                                Log.e(TAG, "到底");
                                new Weibo_FriendsTimeLine(text.get(
                                        view.getLastVisiblePosition())
                                        .get(WEIBO_ID), Weibo_AcquireCount.FRIENDS_TIMELINE_ADD_COUNT, mHandler).start();
                                isRefreshing = true;
                                mPullToRefreshAttacher.setRefreshing(true);
                            }
                        }
                        break;
                    }
                    case OnScrollListener.SCROLL_STATE_FLING: { // 开始滚动

                        break;
                    }
                    case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: { // 正在滚动

                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        menu.clear();

        // menu.add(0, MENU_PROFILE_IMAGE, 0, R.string.menu_user_statuses)
        // .setIcon(
        // new BitmapDrawable(getResources(), BitmapFactory
        // .decodeByteArray(profileImg, 0,
        // profileImg.length)))
        // .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        try {
            menu.add(0, MENU_PROFILE_IMAGE, 0, R.string.menu_user_statuses)
                    .setIcon(
                            new BitmapDrawable(getResources(), BitmapFactory
                                    .decodeByteArray(profileImg, 0,
                                            profileImg.length)))
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        } catch (Exception e) {
            Log.e(TAG, "Profile image length: (Timeline) ERROR!");
            e.printStackTrace();
        }

        menu.add(0, MENU_UNREAD_COUNT, 0,
                Weibo_Constants.SCREEN_NAME.toUpperCase(Locale.US))
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
                Intent i = new Intent();
                i.setClass(act, Post.class);
                i.putExtra(IS_FRAG_POST, true);
                i.putExtra(PROFILE_IMAGE, profileImg);
                startActivity(i);
                break;
            }
            case MENU_PROFILE_IMAGE: {

                Cursor cursor = sql.query(MyMaidSQLHelper.tableName, new String[]{
                        MyMaidSQLHelper.UID, MyMaidSQLHelper.SCREEN_NAME}, null, null, null,
                        null, null);
                Log.e(MyMaidSQLHelper.TAG_SQL, "Queried users");

                if (cursor.getCount() > 0) {
                    final String[] singleUid = new String[cursor.getCount() + 2];
                    final String[] singleUser = new String[cursor.getCount() + 2];

                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                            .moveToNext()) {

                        singleUid[cursor.getPosition()] = cursor.getString(0);
                        singleUser[cursor.getPosition()] = cursor.getString(1);
                        Log.e(TAG, "Cursor position - " + cursor.getPosition());
                        Log.e(TAG, "Single Uid - "
                                + singleUid[cursor.getPosition()]);
                        Log.e(TAG,
                                "Single User - " + singleUser[cursor.getPosition()]);
                        Log.e(TAG, LOG_DEVIDER);
                    }

                    singleUser[cursor.getCount()] = act
                            .getResources()
                            .getString(
                                    R.string.frag_ftl_dialog_choose_account_add_account);
                    singleUid[cursor.getCount()] = "0";

                    singleUser[cursor.getCount() + 1] = act
                            .getResources().getString(
                                    R.string.frag_ftl_dialog_choose_account_logout);
                    singleUid[cursor.getCount() + 1] = "1";

                    new AlertDialog.Builder(act)
                            .setTitle(R.string.frag_ftl_dialog_choose_account_title)
                            .setItems(singleUser, new OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    SharedPreferences uids = act
                                            .getSharedPreferences(PREFERENCES,
                                                    Context.MODE_PRIVATE);
                                    SharedPreferences.Editor uidsE = uids.edit();

                                    Log.e(TAG, "Chose UID: " + singleUid[which]);
                                    Log.e(TAG, "Chose Screen Name: "
                                            + singleUid[which]);

                                    if (!singleUid[which].equals("0")
                                            && !singleUid[which].equals("1")) {
                                        uidsE.putString(LASTUID, singleUid[which]);
                                        uidsE.commit();
                                        act.finish();
                                        startActivity(new Intent(act,
                                                Main.class));
                                    } else if (singleUid[which].equals("0")) {
                                        startActivity(new Intent(act,
                                                Login.class));
                                        act.finish();
                                    } else if (singleUid[which].equals("1")) {

                                        new AlertDialog.Builder(act,
                                                AlertDialog.THEME_HOLO_LIGHT)
                                                .setTitle(
                                                        R.string.frag_ftl_dialog_confirm_logout_title)
                                                .setPositiveButton(
                                                        R.string.frag_ftl_dialog_confirm_logout_btn_ok,
                                                        new OnClickListener() {

                                                            @Override
                                                            public void onClick(
                                                                    DialogInterface dialog,
                                                                    int which) {
                                                                SharedPreferences.Editor editor = ((Main) act)
                                                                        .getEditor();

                                                                editor.putString(
                                                                        LASTUID, "");
                                                                editor.commit();

                                                                if (sql.delete(
                                                                        MyMaidSQLHelper.tableName,
                                                                        MyMaidSQLHelper.UID
                                                                                + "=?",
                                                                        new String[]{Weibo_Constants.UID}) > 0) {
                                                                    Log.e(MyMaidSQLHelper.TAG_SQL,
                                                                            "LOGOUT - Cleared user info");

                                                                    Toast.makeText(
                                                                            act,
                                                                            "<(￣︶￣)>",
                                                                            Toast.LENGTH_SHORT)
                                                                            .show();
                                                                    startActivity(new Intent(
                                                                            act,
                                                                            Login.class));
                                                                    act
                                                                            .finish();
                                                                }
                                                            }
                                                        })
                                                .setNegativeButton(
                                                        R.string.frag_ftl_dialog_confirm_logout_btn_cancle,
                                                        null).show();
                                    }
                                }
                            }).setNegativeButton(android.R.string.cancel, null).show();
                } else {

                }

                break;
            }
            case MENU_UNREAD_COUNT: {
                String[] groups = {act.getString(R.string.frag_ftl_dialog_groups_my_posts), act.getString(R.string.frag_ftl_dialog_groups_coming_soon)};
                new AlertDialog.Builder(act).setTitle(R.string.frag_ftl_dialog_groups_title).setItems(groups, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.e(TAG, String.valueOf(i));
                        switch (i) {
                            case 0: {
                                Intent it = new Intent();
                                it.setClass(act, SingleUser.class);
                                it.putExtra(SCREEN_NAME, Weibo_Constants.SCREEN_NAME);
                                startActivity(it);
                                break;
                            }
                            case 1: {
                                Toast.makeText(act, "TAT", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).setNegativeButton(android.R.string.cancel, null).show();


                break;
            }
        }
        act.invalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }

    Handler mHandler = new Handler() {

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            // act.setProgressBarIndeterminateVisibility(false);
            mPullToRefreshAttacher.setRefreshComplete();
            isRefreshing = false;
            switch (msg.what) {
                case GOT_FRIENDS_TIMELINE_INFO: {
                    text = (ArrayList<HashMap<String, String>>) msg.obj;
                    setListView(text);

                    break;
                }
                case GOT_FRIENDS_TIMELINE_ADD_INFO: {
                    text.addAll((ArrayList<HashMap<String, String>>) msg.obj);
                    addListView(text);
                    break;
                }
                case GOT_FRIENDS_TIMELINE_INFO_FAIL: {
                    Toast.makeText(act,
                            R.string.toast_user_timeline_fail, Toast.LENGTH_SHORT)
                            .show();
                    break;
                }
                case GOT_FRIENDS_TIMELINE_EXTRA_INFO: {

                    break;
                }
            }
            act.invalidateOptionsMenu();
        }

    };

    private void setListView(ArrayList<HashMap<String, String>> arrayList) {
        mAdapter = new MyMaidAdapter(act, arrayList);
        lv.setAdapter(mAdapter);
    }

    private void addListView(ArrayList<HashMap<String, String>> arrayList) {
        mAdapter.addItem(arrayList);
        mAdapter.notifyDataSetChanged();
    }

    public void refreshFriendsTimeLine() {
        new Weibo_FriendsTimeLine(Weibo_AcquireCount.FRIENDS_TIMELINE_COUNT, sqlHelper, mHandler).start();
        isRefreshing = true;
        mPullToRefreshAttacher.setRefreshing(true);
    }

    public void setListViewToTop()
    {
        if(lv != null)
            lv.smoothScrollToPosition(0);
    }
}
