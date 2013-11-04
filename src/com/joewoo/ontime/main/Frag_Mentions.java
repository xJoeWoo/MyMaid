package com.joewoo.ontime.main;

import java.util.ArrayList;
import java.util.HashMap;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;

import com.joewoo.ontime.Comment_Repost;
import com.joewoo.ontime.Post;
import com.joewoo.ontime.R;
import com.joewoo.ontime.SingleUser;
import com.joewoo.ontime.action.Weibo_CommentsMentions;
import com.joewoo.ontime.singleWeibo.SingleWeibo;
import com.joewoo.ontime.action.Weibo_Mentions;
import com.joewoo.ontime.action.Weibo_RemindSetCount;
import com.joewoo.ontime.action.Weibo_UnreadCount;
import com.joewoo.ontime.bean.UnreadCountBean;
import com.joewoo.ontime.info.WeiboConstant;

import static com.joewoo.ontime.info.Defines.*;

import com.joewoo.ontime.info.Weibo_AcquireCount;
import com.joewoo.ontime.tools.MySQLHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class Frag_Mentions extends Fragment implements OnRefreshListener {

    ArrayList<HashMap<String, String>> text;
    ListView lv;
    MySQLHelper sqlHelper;
    SQLiteDatabase sql;
    boolean isRefreshing;
    String unreadCount;
    String unreadCommentMentionsCount;
    private PullToRefreshAttacher mPullToRefreshAttacher;
    byte[] profileImg;
    private Activity act;
    private boolean isNormalMention = true;
    private Cursor c;

    @Override
    public void onRefreshStarted(View view) {
        if (isNormalMention) {
            Log.e(TAG, "Refresh Mentions");
            refreshMentions();
            new Weibo_RemindSetCount(mHandler)
                    .execute(Weibo_RemindSetCount.setMentionsCount);
        } else {
            Log.e(TAG, "Refresh Comments Mentions");
            refreshCommentsMentions();
            new Weibo_RemindSetCount(mHandler)
                    .execute(Weibo_RemindSetCount.setCommentMentionsCount);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.friendstimeline, null);

        lv = (ListView) v.findViewById(R.id.lv_friends_timeline);
        lv.setDivider(null);

        return v;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        act = getActivity();

        mPullToRefreshAttacher = ((Main) act)
                .getPullToRefreshAttacher();
        mPullToRefreshAttacher.addRefreshableView(lv, this);

        sqlHelper = new MySQLHelper(act, SQL_NAME, null, SQL_VERSION);
        sql = sqlHelper.getReadableDatabase();
        c = sql.query(sqlHelper.tableName, new String[]{
                sqlHelper.MENTIONS, sqlHelper.PROFILEIMG, MySQLHelper.COMMENTS_MENTIONS}, sqlHelper.UID
                + "=?", new String[]{WeiboConstant.UID}, null, null, null);

        setLv(sqlHelper.MENTIONS);

        profileImg = c.getBlob(c.getColumnIndex(sqlHelper.PROFILEIMG));

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent i = new Intent();
                i.setClass(act, Comment_Repost.class);
                i.putExtra(IS_COMMENT, true);
                i.putExtra(WEIBO_ID, text.get(arg2).get(WEIBO_ID));
                startActivity(i);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                if (isNormalMention) {
                    Intent i = new Intent();
                    i.setClass(act, SingleWeibo.class);

                    HashMap<String, String> map = text.get(arg2);

                    i.putExtra(SCREEN_NAME, map.get(SCREEN_NAME));
                    i.putExtra(CREATED_AT, map.get(CREATED_AT));
                    i.putExtra(TEXT, map.get(TEXT));
                    i.putExtra(PROFILE_IMAGE_URL, map.get(PROFILE_IMAGE_URL));
                    i.putExtra(IS_REPOST, map.get(IS_REPOST));
                    i.putExtra(RETWEETED_STATUS_SCREEN_NAME,
                            map.get(RETWEETED_STATUS_SCREEN_NAME));
                    i.putExtra(RETWEETED_STATUS, map.get(RETWEETED_STATUS));
                    i.putExtra(RETWEETED_STATUS_COMMENTS_COUNT,
                            map.get(RETWEETED_STATUS_COMMENTS_COUNT));
                    i.putExtra(RETWEETED_STATUS_REPOSTS_COUNT,
                            map.get(RETWEETED_STATUS_REPOSTS_COUNT));
                    i.putExtra(RETWEETED_STATUS_SOURCE,
                            map.get(RETWEETED_STATUS_SOURCE));
                    i.putExtra(RETWEETED_STATUS_CREATED_AT,
                            map.get(RETWEETED_STATUS_CREATED_AT));
                    i.putExtra(RETWEETED_STATUS_THUMBNAIL_PIC,
                            map.get(RETWEETED_STATUS_THUMBNAIL_PIC));
                    i.putExtra(COMMENTS_COUNT, map.get(COMMENTS_COUNT));
                    i.putExtra(REPOSTS_COUNT, map.get(REPOSTS_COUNT));
                    i.putExtra(SOURCE, map.get(SOURCE));
                    i.putExtra(WEIBO_ID, map.get(WEIBO_ID));
                    i.putExtra(RETWEETED_STATUS_BMIDDLE_PIC,
                            map.get(RETWEETED_STATUS_BMIDDLE_PIC));
                    i.putExtra(BMIDDLE_PIC, map.get(BMIDDLE_PIC));
                    i.putExtra(UID, map.get(UID));
                    i.putExtra(RETWEETED_STATUS_UID, map.get(RETWEETED_STATUS_UID));

                    startActivity(i);
                } else {

                }
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
            menu.add(0, MENU_UNREAD_COUNT, 0, act.getString(R.string.menu_unread_post_mentions) + unreadCount + act.getString(R.string.menu_unread_comments_mentions) + unreadCommentMentionsCount).setShowAsAction(
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
                getUnreadMentionsCount();
                String[] items = {act.getString(R.string.frag_mentions_dialog_normal_mention), act.getString(R.string.frag_mentions_dialog_comment_mention)};
                new AlertDialog.Builder(act).setTitle(R.string.frag_mentions_dialog_title).setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mPullToRefreshAttacher.setRefreshing(true);
                        switch (i) {
                            case 0: {
                                isNormalMention = true;
                                setLv(sqlHelper.MENTIONS);
//                            refreshMentions();
                                break;
                            }
                            case 1: {
                                isNormalMention = false;
                                setLv(sqlHelper.COMMENTS_MENTIONS);
//                            refreshCommentsMentions();
                                break;
                            }
                        }
                    }
                }).setNegativeButton(android.R.string.cancel, null).show();
                break;
            }
            case MENU_PROFILE_IMAGE: {
                if (WeiboConstant.UID.equals("1665287983")) {
                    Intent i = new Intent();
                    i.setClass(act, SingleUser.class);
                    i.putExtra(UID, "1893689251");
                    i.putExtra(SCREEN_NAME, "Selley__LauChingYee");
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
            // act.setProgressBarIndeterminateVisibility(false);
            isRefreshing = false;
            mPullToRefreshAttacher.setRefreshComplete();
            switch (msg.what) {
                case GOT_MENTIONS_INFO: {

                    text = (ArrayList<HashMap<String, String>>) msg.obj;
                    setListView(text);

                    break;
                }
                case GOT_MENTIONS_INFO_FAIL: {
                    Toast.makeText(act, R.string.toast_mentions_fail,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                case GOT_UNREAD_COUNT_INFO: {
                    UnreadCountBean b = (UnreadCountBean) msg.obj;
                    if (b.getMentionStatusCount() != null) {
                        unreadCount = b.getMentionStatusCount();
                        unreadCommentMentionsCount = b.getMentionCmtCount();
                    } else
                        Toast.makeText(act,
                                R.string.toast_unread_count_fail,
                                Toast.LENGTH_SHORT).show();
                    break;
                }
                case GOT_COMMENTS_MENTIONS_INFO: {
                    text = (ArrayList<HashMap<String, String>>) msg.obj;
                    setListView(text);
                    break;
                }
                case GOT_COMMENTS_MENTIONS_INFO_FAIL: {

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

    private void setListView(ArrayList<HashMap<String, String>> arrayList) {

        String[] from = {SOURCE, CREATED_AT, SCREEN_NAME, TEXT,
                COMMENTS_COUNT, REPOSTS_COUNT, RETWEETED_STATUS_SCREEN_NAME,
                RETWEETED_STATUS, HAVE_PIC, IS_REPOST};
        int[] to = {R.id.mentions_source, R.id.mentions_created_at,
                R.id.mentions_screen_name, R.id.mentions_text,
                R.id.mentions_comments_count, R.id.mentions_reposts_count,
                R.id.mentions_retweeted_status_screen_name,
                R.id.mentions_retweeted_status, R.id.mentions_have_image,
                R.id.mentions_retweeted_status_rl};

        SimpleAdapter data = new SimpleAdapter(act, arrayList,
                R.layout.mentions_lv, from, to);

        SimpleAdapter.ViewBinder binder = new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {

                if (view.equals((TextView) view
                        .findViewById(R.id.mentions_retweeted_status_rl))) {
                    if (" ".equals(textRepresentation)) {
                        view.setVisibility(View.VISIBLE);
                    } else {
                        view.setVisibility(View.GONE);
                    }
                }

                if (view.equals((TextView) view
                        .findViewById(R.id.mentions_retweeted_status_screen_name))) {
                    if (!"".equals(textRepresentation)) {
                        view.setVisibility(View.VISIBLE);
                    } else {
                        view.setVisibility(View.GONE);
                    }
                }

                if (view.equals((TextView) view
                        .findViewById(R.id.mentions_retweeted_status))) {
                    if (!"".equals(textRepresentation)) {
                        view.setVisibility(View.VISIBLE);
                    } else {
                        view.setVisibility(View.GONE);
                    }
                }
                if (view.equals((TextView) view
                        .findViewById(R.id.mentions_have_image))) {
                    if (" ".equals(textRepresentation)) {
                        view.setVisibility(View.VISIBLE);

                    } else {
                        view.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        };

        data.setViewBinder(binder);

        lv.setAdapter(data);
    }

    public void getUnreadMentionsCount() {
        new Weibo_UnreadCount(mHandler).start();
    }

    public void refreshMentions() {
        new Weibo_Mentions(Weibo_AcquireCount.MENTIONS_COUNT, sqlHelper, mHandler).start();
        isRefreshing = true;
        mPullToRefreshAttacher.setRefreshing(true);
    }

    public void refreshCommentsMentions() {
        new Weibo_CommentsMentions(Weibo_AcquireCount.COMMENTS_MENTIONS_COUNT, sqlHelper, mHandler).start();
        isRefreshing = true;
        mPullToRefreshAttacher.setRefreshing(true);
    }

    private void setLv(String column) {

        if (c != null && c.moveToFirst()) {

            try {
                if (!c.getString(c.getColumnIndex(column)).equals("")) {
                    if (column.equals(sqlHelper.MENTIONS)) {
                        new Weibo_Mentions(
                                c.getString(c.getColumnIndex(MySQLHelper.MENTIONS)), mHandler).start();
                    } else if (column.equals(sqlHelper.COMMENTS_MENTIONS)) {
                        new Weibo_CommentsMentions(
                                c.getString(c.getColumnIndex(MySQLHelper.COMMENTS_MENTIONS)), mHandler).start();
                    }
                } else {
                    if (column.equals(sqlHelper.MENTIONS)) {
                        refreshMentions();
                    } else if (column.equals(sqlHelper.COMMENTS_MENTIONS)) {
                        refreshCommentsMentions();
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

        }

    }

}
