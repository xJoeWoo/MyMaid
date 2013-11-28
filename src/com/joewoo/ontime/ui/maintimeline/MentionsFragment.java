package com.joewoo.ontime.ui.maintimeline;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.joewoo.ontime.action.comments.CommentsMentions;
import com.joewoo.ontime.action.remind.RemindUnreadCount;
import com.joewoo.ontime.action.statuses.StatusesMentions;
import com.joewoo.ontime.support.adapter.listview.MyMaidAdapter;
import com.joewoo.ontime.support.bean.UnreadCountBean;
import com.joewoo.ontime.support.util.GlobalContext;
import com.joewoo.ontime.ui.CommentRepost;
import com.joewoo.ontime.ui.Post;
import com.joewoo.ontime.ui.SingleUser;
import com.joewoo.ontime.ui.singleweibo.SingleWeiboActivity;

import java.util.ArrayList;
import java.util.HashMap;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;

import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_MENTIONS_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_MENTIONS_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.GOT_MENTIONS_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_MENTIONS_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.GOT_SET_REMIND_COUNT_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.GOT_UNREAD_COUNT_INFO;
import static com.joewoo.ontime.support.info.Defines.IS_COMMENT;
import static com.joewoo.ontime.support.info.Defines.IS_FRAG_POST;
import static com.joewoo.ontime.support.info.Defines.MENU_POST;
import static com.joewoo.ontime.support.info.Defines.MENU_PROFILE_IMAGE;
import static com.joewoo.ontime.support.info.Defines.MENU_UNREAD_COUNT;
import static com.joewoo.ontime.support.info.Defines.PROFILE_IMAGE;
import static com.joewoo.ontime.support.info.Defines.SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.SINGLE_WEIBO_MAP;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;

public class MentionsFragment extends Fragment implements OnRefreshListener {

    ArrayList<HashMap<String, String>> text;
    ListView lv;
    String unreadCount;
    String unreadCommentMentionsCount;
    private PullToRefreshAttacher mPullToRefreshAttacher;
    private MainTimelineActivity act;
    private boolean isNormalMention = true;

    @Override
    public void onRefreshStarted(View view) {
        if ((act).checkNetwork()) {
            if (isNormalMention) {
                Log.e(TAG, "Refresh StatusesMentions");
                refreshMentions();
            } else {
                Log.e(TAG, "Refresh Comments StatusesMentions");
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

        act = (MainTimelineActivity) getActivity();

        mPullToRefreshAttacher = act
                .getPullToRefreshAttacher();
        mPullToRefreshAttacher.addRefreshableView(lv, this);

        new StatusesMentions(true, act.getSQL(), mHandler).start();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent i = new Intent();
                i.setClass(act, CommentRepost.class);
                i.putExtra(IS_COMMENT, true);
                i.putExtra(WEIBO_ID, text.get(arg2).get(WEIBO_ID));
                startActivity(i);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
//                if (isNormalMention) {
                Intent i = new Intent(act, SingleWeiboActivity.class);
                i.putExtra(SINGLE_WEIBO_MAP, text.get(arg2));
                startActivity(i);
//                } else {
//
//                }
                return false;
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        menu.clear();

        try {
            menu.add(0, MENU_PROFILE_IMAGE, 0, R.string.menu_coming)
                    .setIcon(act.getProfileImage())
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
                i.putExtra(PROFILE_IMAGE, act.getProfileImgBytes());
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
                                new StatusesMentions(true, act.getSQL(), mHandler).start();
                                break;
                            }
                            case 1: {
                                isNormalMention = false;
                                new CommentsMentions(true, act.getSQL(), mHandler).start();
                                break;
                            }
                        }
                    }
                }).setNegativeButton(android.R.string.cancel, null).show();
                break;
            }
            case MENU_PROFILE_IMAGE: {
                if (GlobalContext.getUID().equals("1665287983")) {
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
            act.setRefreshing(false);
            mPullToRefreshAttacher.setRefreshComplete();
            switch (msg.what) {
                case GOT_MENTIONS_INFO: {
                    text = (ArrayList<HashMap<String, String>>) msg.obj;
                    setListView(text);
                    break;
                }
                case GOT_MENTIONS_INFO_FAIL: {
                    if (msg.obj != null)
                        Toast.makeText(act, (String) msg.obj,
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
        MyMaidAdapter adapter = new MyMaidAdapter(arrayList, act);
        lv.setAdapter(adapter);
    }

    public void getUnreadMentionsCount() {
        new RemindUnreadCount(mHandler).start();
    }

    public void refreshMentions() {
        new StatusesMentions(false, act.getSQL(), mHandler).start();
        act.setRefreshing(true);
        mPullToRefreshAttacher.setRefreshing(true);
    }

    public void refreshCommentsMentions() {
        new CommentsMentions(false, act.getSQL(), mHandler).start();
        act.setRefreshing(true);
        mPullToRefreshAttacher.setRefreshing(true);
    }

}
