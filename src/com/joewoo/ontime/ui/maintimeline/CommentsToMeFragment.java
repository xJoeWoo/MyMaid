package com.joewoo.ontime.ui.maintimeline;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.joewoo.ontime.R;
import com.joewoo.ontime.action.comments.CommentsToMe;
import com.joewoo.ontime.action.remind.RemindUnreadCount;
import com.joewoo.ontime.support.adapter.listview.MyMaidCommentsToMeAdapter;
import com.joewoo.ontime.support.bean.UnreadCountBean;
import com.joewoo.ontime.support.info.AcquireCount;
import com.joewoo.ontime.support.info.Constants;
import com.joewoo.ontime.support.sql.MyMaidSQLHelper;
import com.joewoo.ontime.ui.CommentRepost;
import com.joewoo.ontime.ui.Post;
import com.joewoo.ontime.ui.SingleUser;
import com.joewoo.ontime.ui.singleweibo.SingleWeiboActivity;

import java.util.ArrayList;
import java.util.HashMap;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;

import static com.joewoo.ontime.support.info.Defines.COMMENT_ID;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_TO_ME_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_TO_ME_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.GOT_SET_REMIND_COUNT_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.GOT_UNREAD_COUNT_INFO;
import static com.joewoo.ontime.support.info.Defines.IS_FRAG_POST;
import static com.joewoo.ontime.support.info.Defines.IS_REPLY;
import static com.joewoo.ontime.support.info.Defines.MENU_POST;
import static com.joewoo.ontime.support.info.Defines.MENU_PROFILE_IMAGE;
import static com.joewoo.ontime.support.info.Defines.MENU_UNREAD_COUNT;
import static com.joewoo.ontime.support.info.Defines.PROFILE_IMAGE;
import static com.joewoo.ontime.support.info.Defines.SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.SINGLE_WEIBO_MAP;
import static com.joewoo.ontime.support.info.Defines.STATUS_TEXT;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;

public class CommentsToMeFragment extends Fragment implements OnRefreshListener {

    ArrayList<HashMap<String, String>> text;
    ListView lv;
    SQLiteDatabase sql;
    String unreadCount;
    private PullToRefreshAttacher mPullToRefreshAttacher;
    byte[] profileImg;
    private MainTimelineActivity act;

    @Override
    public void onRefreshStarted(View view) {
        Log.e(TAG, "Refresh Comments");
        if ((act).checkNetwork())
            refreshComments();
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

        sql = (act).getSQL();
        Cursor c = sql.query(MyMaidSQLHelper.tableName, new String[]{
                MyMaidSQLHelper.TO_ME_COMMENTS, MyMaidSQLHelper.PROFILEIMG}, MyMaidSQLHelper.UID
                + "=?", new String[]{Constants.UID}, null, null, null);

        if (c != null && c.moveToFirst()) {
            profileImg = c.getBlob(c.getColumnIndex(MyMaidSQLHelper.PROFILEIMG));
            try {
                    new CommentsToMe(c.getString(c
                            .getColumnIndex(MyMaidSQLHelper.TO_ME_COMMENTS)), mHandler).start();
            } catch (Exception e) {
                e.printStackTrace();
                refreshComments();
            }
        } else {
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent i = new Intent(act, CommentRepost.class);
                i.putExtra(IS_REPLY, true);
                i.putExtra(WEIBO_ID, text.get(arg2).get(WEIBO_ID));
                i.putExtra(COMMENT_ID, text.get(arg2).get(COMMENT_ID));
                startActivity(i);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                Intent i = new Intent(act, SingleWeiboActivity.class);
                i.putExtra(SINGLE_WEIBO_MAP, text.get(arg2));
                Log.e(TAG, "+++++++++++" + text.get(arg2).get(STATUS_TEXT));
                startActivity(i);
                return false;
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();

        try {
            menu.add(0, MENU_PROFILE_IMAGE, 0, R.string.menu_coming)
                    .setIcon(
                            new BitmapDrawable(getResources(), BitmapFactory
                                    .decodeByteArray(profileImg, 0,
                                            profileImg.length)))
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (unreadCount == null)
            menu.add(0, MENU_UNREAD_COUNT, 0, R.string.menu_unread)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        else
            menu.add(0, MENU_UNREAD_COUNT, 0, act.getString(R.string.menu_unread_comments) + unreadCount).setShowAsAction(
                    MenuItem.SHOW_AS_ACTION_ALWAYS);

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
            case MENU_UNREAD_COUNT: {
                getUnreadCommentsCount();
                break;
            }
            case MENU_PROFILE_IMAGE: {
                if (Constants.UID.equals("1665287983")) {
                    Intent i = new Intent();
                    i.setClass(act, SingleUser.class);
                    i.putExtra(SCREEN_NAME, "VongCamCam");
                    startActivity(i);
                } else {
                    Toast.makeText(act, R.string.toast_coming_soon, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
        act.invalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mPullToRefreshAttacher.setRefreshing(false);
            act.setRefreshing(false);
            switch (msg.what) {
                case GOT_COMMENTS_TO_ME_INFO: {
                    text = (ArrayList<HashMap<String, String>>) msg.obj;
                    setListView(text);
                    break;
                }
                case GOT_COMMENTS_TO_ME_INFO_FAIL: {
                    if(msg.obj != null)
                        Toast.makeText(act, (String) msg.obj,
                                Toast.LENGTH_SHORT).show();
                    else
                    Toast.makeText(act, R.string.toast_comments_fail,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                case GOT_UNREAD_COUNT_INFO: {
                    UnreadCountBean b = (UnreadCountBean) msg.obj;
                    if (b.getMentionCmtCount() != null)
                        unreadCount = b.getCmtCount();
                    else
                        Toast.makeText(act,
                                R.string.toast_unread_count_fail,
                                Toast.LENGTH_SHORT).show();
                    break;
                }
                case GOT_SET_REMIND_COUNT_INFO_FAIL: {
                    Toast.makeText(act,
                            R.string.toast_clear_unread_count_fail,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            act.invalidateOptionsMenu();
        }

    };

    public void getUnreadCommentsCount() {
        new RemindUnreadCount(mHandler).start();
    }

    public void refreshComments() {
        new CommentsToMe(AcquireCount.COMMENTS_TO_ME_COUNT, sql, mHandler).start();
        act.setRefreshing(true);
        mPullToRefreshAttacher.setRefreshing(true);
    }

    private void setListView(ArrayList<HashMap<String, String>> arrayList) {
        MyMaidCommentsToMeAdapter adapter = new MyMaidCommentsToMeAdapter(act, arrayList);
        lv.setAdapter(adapter);
    }

}
