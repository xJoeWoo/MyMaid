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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.joewoo.ontime.R;
import com.joewoo.ontime.action.comments.CommentsMentions;
import com.joewoo.ontime.action.remind.RemindUnreadCount;
import com.joewoo.ontime.action.statuses.StatusesMentions;
import com.joewoo.ontime.support.adapter.listview.CommentsMentionsAdapter;
import com.joewoo.ontime.support.adapter.listview.MainListViewAdapter;
import com.joewoo.ontime.support.bean.CommentsBean;
import com.joewoo.ontime.support.bean.StatusesBean;
import com.joewoo.ontime.support.bean.UnreadCountBean;
import com.joewoo.ontime.support.info.AcquireCount;
import com.joewoo.ontime.support.net.NetworkStatus;
import com.joewoo.ontime.support.util.GlobalContext;
import com.joewoo.ontime.support.view.header.MainTimelineHeaderView;
import com.joewoo.ontime.ui.CommentRepost;
import com.joewoo.ontime.ui.Post;
import com.joewoo.ontime.ui.SingleUser;
import com.joewoo.ontime.ui.singleweibo.SingleWeiboActivity;

import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;

import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_MENTIONS_ADD_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_MENTIONS_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_MENTIONS_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.GOT_MENTIONS_ADD_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_MENTIONS_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_MENTIONS_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.GOT_SET_REMIND_COUNT_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.GOT_UNREAD_COUNT_INFO;
import static com.joewoo.ontime.support.info.Defines.IS_COMMENT;
import static com.joewoo.ontime.support.info.Defines.MENU_POST;
import static com.joewoo.ontime.support.info.Defines.MENU_PROFILE_IMAGE;
import static com.joewoo.ontime.support.info.Defines.MENU_UNREAD_COUNT;
import static com.joewoo.ontime.support.info.Defines.SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.STATUS_BEAN;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;

public class MentionsFragment extends Fragment implements OnRefreshListener {

    private List<StatusesBean> statuses;
    private List<CommentsBean> comments;
    ListView lv;
    String unreadCount;
    String unreadCommentMentionsCount;
    private PullToRefreshAttacher mPullToRefreshAttacher;
    private MainTimelineActivity act;
    private boolean isNormalMention = true;
    private MainListViewAdapter mainAdapter;
    private CommentsMentionsAdapter commentsAdapter;

    @Override
    public void onRefreshStarted(View view) {
        if (NetworkStatus.check(true)) {
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

        new StatusesMentions(true, mHandler).start();

        lv.setFastScrollAlwaysVisible(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent i = new Intent();
                i.setClass(act, CommentRepost.class);
                i.putExtra(IS_COMMENT, true);
                if (isNormalMention)
                    i.putExtra(WEIBO_ID, statuses.get(arg2 - lv.getHeaderViewsCount()).getId());
                else
                    i.putExtra(WEIBO_ID, comments.get(arg2 - lv.getHeaderViewsCount()).getId());
                startActivity(i);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {

                StatusesBean b = statuses.get(arg2 - lv.getHeaderViewsCount());

                if (isNormalMention)
                    if (b.getRetweetedStatus() != null && b.getRetweetedStatus().getUser() == null)
                        return false; // 微博已被删除不继续进行

                Intent i = new Intent(act, SingleWeiboActivity.class);
                if (isNormalMention) {
                    i.putExtra(STATUS_BEAN, statuses.get(arg2 - lv.getHeaderViewsCount()));
                } else {
                    i.putExtra(WEIBO_ID, comments.get(arg2 - lv.getHeaderViewsCount()).getStatus().getId());
                }
                startActivity(i);
                return false;
            }
        });

        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 滚到到尾刷新
                if (view.getCount() > (Integer.valueOf(AcquireCount.MENTIONS_COUNT) - 2) && view.getLastVisiblePosition() > (view.getCount() - 6) && !mPullToRefreshAttacher.isRefreshing() && statuses != null) {
                    Log.e(TAG, "到底");
                    if (isNormalMention)
                        new StatusesMentions(statuses.get(view.getCount() - 1 - lv.getHeaderViewsCount()).getId(), mHandler).start();
                    else
                        new CommentsMentions(comments.get(view.getCount() - 1 - lv.getHeaderViewsCount()).getId(), mHandler).start();
                    mPullToRefreshAttacher.setRefreshing(true);
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
        });

        mainAdapter = new MainListViewAdapter(act);
        commentsAdapter = new CommentsMentionsAdapter(act);
        lv.addHeaderView(new MainTimelineHeaderView(act), null, false);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        menu.clear();

        menu.add(0, MENU_PROFILE_IMAGE, 0, R.string.menu_coming)
                .setIcon(GlobalContext.getSmallProfileImg())
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

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
                startActivity(new Intent(act, Post.class));
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
                                new StatusesMentions(true, mHandler).start();
                                break;
                            }
                            case 1: {
                                isNormalMention = false;
                                new CommentsMentions(true, mHandler).start();
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

    private Handler mHandler = new Handler() {

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            // act.setProgressBarIndeterminateVisibility(false);
            mPullToRefreshAttacher.setRefreshComplete();
            switch (msg.what) {
                case GOT_MENTIONS_INFO: {
                    statuses = (List<StatusesBean>) msg.obj;
                    setListView(statuses);
                    break;
                }
                case GOT_COMMENTS_MENTIONS_INFO_FAIL:
                case GOT_MENTIONS_INFO_FAIL: {
                    Toast.makeText(act, (String) msg.obj,
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
                case GOT_MENTIONS_ADD_INFO: {
                    statuses.addAll((List<StatusesBean>) msg.obj);
                    setListView(statuses);
                    break;
                }
                case GOT_COMMENTS_MENTIONS_ADD_INFO: {
                    comments.addAll((List<CommentsBean>) msg.obj);
                    setCommentsListView(comments);
                    break;
                }
                case GOT_COMMENTS_MENTIONS_INFO: {
                    comments = (List<CommentsBean>) msg.obj;
                    setCommentsListView(comments);
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

    private void setListView(List<StatusesBean> statuses) {
        mainAdapter.setData(statuses);
        setAdapter(isNormalMention);
        mainAdapter.notifyDataSetChanged();
    }

    private void setCommentsListView(List<CommentsBean> comments) {
        commentsAdapter.setData(comments);
        setAdapter(isNormalMention);
        commentsAdapter.notifyDataSetChanged();
    }

    private void setAdapter(boolean isNormalMention) {
        if (lv.getAdapter() == null) {
            if (isNormalMention)
                lv.setAdapter(mainAdapter);
            else
                lv.setAdapter(commentsAdapter);
        } else {
            Log.e(TAG, "isNor: " + String.valueOf(isNormalMention));
            Log.e(TAG, "Adapter: " + String.valueOf(lv.getAdapter() == mainAdapter ? "1" : "0"));
            if (!isNormalMention && lv.getAdapter() != mainAdapter)
                lv.setAdapter(commentsAdapter);
            else if (isNormalMention && lv.getAdapter() != commentsAdapter)
                lv.setAdapter(mainAdapter);
            Log.e(TAG, "isNor: " + String.valueOf(isNormalMention));
            Log.e(TAG, "Adapter: " + String.valueOf(lv.getAdapter() == mainAdapter ? "1" : "0"));
        }

    }

    public void getUnreadMentionsCount() {
        new RemindUnreadCount(mHandler).start();
    }

    public void refreshMentions() {
        new StatusesMentions(false, mHandler).start();
        mPullToRefreshAttacher.setRefreshing(true);
    }

    public void refreshCommentsMentions() {
        new CommentsMentions(false, mHandler).start();
        mPullToRefreshAttacher.setRefreshing(true);
    }

    public void scrollListViewToTop() {
        lv.smoothScrollToPosition(0);
    }

}
