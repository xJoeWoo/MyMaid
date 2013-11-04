package com.joewoo.ontime.main;

import static com.joewoo.ontime.info.Defines.*;

import java.util.ArrayList;
import java.util.HashMap;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;

import com.joewoo.ontime.Comment_Repost;
import com.joewoo.ontime.Post;
import com.joewoo.ontime.R;
import com.joewoo.ontime.SingleUser;
import com.joewoo.ontime.action.Weibo_CommentsToMe;
import com.joewoo.ontime.action.Weibo_RemindSetCount;
import com.joewoo.ontime.action.Weibo_UnreadCount;
import com.joewoo.ontime.bean.UnreadCountBean;
import com.joewoo.ontime.info.WeiboConstant;
import com.joewoo.ontime.info.Weibo_AcquireCount;
import com.joewoo.ontime.tools.MySQLHelper;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

@SuppressLint({"HandlerLeak", "NewApi"})
public class Frag_Comments extends Fragment implements OnRefreshListener {

    ArrayList<HashMap<String, String>> text;
    ListView lv;
    boolean isRefreshing;
    MySQLHelper sqlHelper;
    SQLiteDatabase sql;
    String unreadCount;
    private PullToRefreshAttacher mPullToRefreshAttacher;
    byte[] profileImg;
    private Activity act;

    @Override
    public void onRefreshStarted(View view) {
        Log.e(TAG, "Refresh Comments");
        refreshComments();
        new Weibo_RemindSetCount(mHandler)
                .execute(Weibo_RemindSetCount.setCommentsCount);
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

        sqlHelper = new MySQLHelper(act, SQL_NAME, null, SQL_VERSION);
        sql = sqlHelper.getReadableDatabase();
        Cursor c = sql.query(sqlHelper.tableName, new String[]{
                sqlHelper.TO_ME_COMMENTS, sqlHelper.PROFILEIMG}, sqlHelper.UID
                + "=?", new String[]{WeiboConstant.UID}, null, null, null);

        if (c != null && c.moveToFirst()) {
            profileImg = c.getBlob(c.getColumnIndex(sqlHelper.PROFILEIMG));
            try {
                if (!c.getString(c.getColumnIndex(sqlHelper.TO_ME_COMMENTS)).equals(""))
                    new Weibo_CommentsToMe(c.getString(c
                            .getColumnIndex(sqlHelper.TO_ME_COMMENTS)), mHandler).start();

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
                Intent i = new Intent();
                i.setClass(act, Comment_Repost.class);
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

                Intent i = new Intent();
                i.setClass(act, SingleUser.class);
                i.putExtra(SCREEN_NAME, text.get(arg2).get(SCREEN_NAME));
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
                if (WeiboConstant.UID.equals("1665287983")) {
                    Intent i = new Intent();
                    i.setClass(act, SingleUser.class);
                    i.putExtra(UID, "1739275793");
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

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {

            mPullToRefreshAttacher.setRefreshing(false);
            isRefreshing = false;
            switch (msg.what) {
                case GOT_COMMENTS_TO_ME_INFO: {

                    text = (ArrayList<HashMap<String, String>>) msg.obj;

                    String[] from = {SCREEN_NAME, TEXT, CREATED_AT, SOURCE,
                            STATUS_USER_SCREEN_NAME, STATUS_TEXT};
                    int[] to = {R.id.comments_to_me_screen_name,
                            R.id.comments_to_me_text,
                            R.id.comments_to_me_created_at,
                            R.id.comments_to_me_source,
                            R.id.comments_to_me_status_screen_name,
                            R.id.comments_to_me_status};

                    SimpleAdapter data = new SimpleAdapter(act, text,
                            R.layout.comments_to_me_lv, from, to);

                    lv.setAdapter(data);

                    break;
                }
                case GOT_COMMENTS_TO_ME_INFO_FAIL: {
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
        new Weibo_UnreadCount(mHandler).start();
    }

    public void refreshComments() {
        new Weibo_CommentsToMe(Weibo_AcquireCount.COMMENTS_TO_ME_COUNT, sqlHelper, mHandler).start();
        isRefreshing = true;
        mPullToRefreshAttacher.setRefreshing(true);
    }

}
