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
import com.joewoo.ontime.bean.ErrorBean;
import com.joewoo.ontime.info.Weibo_Constants;
import com.joewoo.ontime.singleWeibo.SingleWeibo;
import com.joewoo.ontime.action.Weibo_Mentions;
import com.joewoo.ontime.action.Weibo_RemindSetCount;
import com.joewoo.ontime.action.Weibo_UnreadCount;
import com.joewoo.ontime.bean.UnreadCountBean;

import static com.joewoo.ontime.info.Constants.*;

import com.joewoo.ontime.info.Weibo_AcquireCount;
import com.joewoo.ontime.tools.MyMaidAdapter;
import com.joewoo.ontime.tools.MyMaidSQLHelper;
import com.joewoo.ontime.tools.MyMaidUtilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
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

public class Frag_Mentions extends Fragment implements OnRefreshListener {

    ArrayList<HashMap<String, String>> text;
    ListView lv;
    MyMaidSQLHelper sqlHelper;
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
        if (((Main) act).checkNetwork()) {
            if (isNormalMention) {
                Log.e(TAG, "Refresh Mentions");
                refreshMentions();
            } else {
                Log.e(TAG, "Refresh Comments Mentions");
                refreshCommentsMentions();
            }
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

        sql = ((Main) act).getSQL();
        c = sql.query(MyMaidSQLHelper.tableName, new String[]{
                MyMaidSQLHelper.MENTIONS, MyMaidSQLHelper.PROFILEIMG, MyMaidSQLHelper.COMMENTS_MENTIONS}, MyMaidSQLHelper.UID
                + "=?", new String[]{Weibo_Constants.UID}, null, null, null);

        setLv(MyMaidSQLHelper.MENTIONS);

        profileImg = c.getBlob(c.getColumnIndex(MyMaidSQLHelper.PROFILEIMG));

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
                    Intent i = new Intent(act, SingleWeibo.class);
                    i.putExtra(SINGLE_WEIBO_MAP, text.get(arg2));
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
                                setLv(MyMaidSQLHelper.MENTIONS);
//                            refreshMentions();
                                break;
                            }
                            case 1: {
                                isNormalMention = false;
                                setLv(MyMaidSQLHelper.COMMENTS_MENTIONS);
//                            refreshCommentsMentions();
                                break;
                            }
                        }
                    }
                }).setNegativeButton(android.R.string.cancel, null).show();
                break;
            }
            case MENU_PROFILE_IMAGE: {
                if (Weibo_Constants.UID.equals("1665287983")) {
                    Intent i = new Intent();
                    i.setClass(act, SingleUser.class);
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
                    new Weibo_RemindSetCount(mHandler)
                            .execute(Weibo_RemindSetCount.setMentionsCount);
                    break;
                }
                case GOT_MENTIONS_INFO_FAIL: {
                    if (msg.obj != null)
                        Toast.makeText(act, ((ErrorBean) msg.obj).getError(),
                                Toast.LENGTH_SHORT).show();
                    else
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
                    new Weibo_RemindSetCount(mHandler)
                            .execute(Weibo_RemindSetCount.setCommentMentionsCount);
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
        MyMaidAdapter adapter = new MyMaidAdapter(act, arrayList);
        lv.setAdapter(adapter);
    }

    public void getUnreadMentionsCount() {
        new Weibo_UnreadCount(mHandler).start();
    }

    public void refreshMentions() {
        new Weibo_Mentions(Weibo_AcquireCount.MENTIONS_COUNT, sql, mHandler).start();
        isRefreshing = true;
        mPullToRefreshAttacher.setRefreshing(true);
    }

    public void refreshCommentsMentions() {
        new Weibo_CommentsMentions(Weibo_AcquireCount.COMMENTS_MENTIONS_COUNT, sql, mHandler).start();
        isRefreshing = true;
        mPullToRefreshAttacher.setRefreshing(true);
    }

    private void setLv(String column) {
        if (c != null && c.moveToFirst()) {
            try {
                if (!c.getString(c.getColumnIndex(column)).equals("")) {
                    if (column.equals(MyMaidSQLHelper.MENTIONS)) {
                        new Weibo_Mentions(
                                c.getString(c.getColumnIndex(MyMaidSQLHelper.MENTIONS)), mHandler).start();
                    } else if (column.equals(MyMaidSQLHelper.COMMENTS_MENTIONS)) {
                        new Weibo_CommentsMentions(
                                c.getString(c.getColumnIndex(MyMaidSQLHelper.COMMENTS_MENTIONS)), mHandler).start();
                    }
                } else {
                    if (column.equals(MyMaidSQLHelper.MENTIONS)) {
                        refreshMentions();
                    } else if (column.equals(MyMaidSQLHelper.COMMENTS_MENTIONS)) {
                        refreshCommentsMentions();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                mPullToRefreshAttacher.setRefreshing(false);
            }
        } else {

        }

    }

}
