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
import com.joewoo.ontime.action.MyMaidActionHelper;
import com.joewoo.ontime.support.adapter.listview.CommentsToMeAdapter;
import com.joewoo.ontime.support.bean.CommentsBean;
import com.joewoo.ontime.support.bean.UnreadCountBean;
import com.joewoo.ontime.support.info.AcquireCount;
import com.joewoo.ontime.support.net.NetworkStatus;
import com.joewoo.ontime.support.util.GlobalContext;
import com.joewoo.ontime.ui.CommentRepost;
import com.joewoo.ontime.ui.Post;
import com.joewoo.ontime.ui.SingleUser;
import com.joewoo.ontime.ui.singleweibo.SingleWeiboActivity;

import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;

import static com.joewoo.ontime.support.info.Defines.COMMENT_ID;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_BY_ME_ADD_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_BY_ME_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_BY_ME_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_DESTROY_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_DESTROY_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_TO_ME_ADD_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_TO_ME_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENTS_TO_ME_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.GOT_SET_REMIND_COUNT_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.GOT_UNREAD_COUNT_INFO;
import static com.joewoo.ontime.support.info.Defines.IS_REPLY;
import static com.joewoo.ontime.support.info.Defines.MENU_POST;
import static com.joewoo.ontime.support.info.Defines.MENU_PROFILE_IMAGE;
import static com.joewoo.ontime.support.info.Defines.MENU_UNREAD_COUNT;
import static com.joewoo.ontime.support.info.Defines.SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;

public class CommentsToMeFragment extends Fragment implements OnRefreshListener {

    private List<CommentsBean> comments;
    private ListView lv;
    private String unreadCount;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mPullToRefreshAttacher.setRefreshing(false);
            switch (msg.what) {
                case GOT_COMMENTS_BY_ME_INFO:
                case GOT_COMMENTS_TO_ME_INFO: {
                    comments = (List<CommentsBean>) msg.obj;
                    setListView(comments);
                    break;
                }
                case GOT_COMMENTS_BY_ME_ADD_INFO:
                case GOT_COMMENTS_TO_ME_ADD_INFO: {
                    comments.addAll((List<CommentsBean>) msg.obj);
                    updateListView(comments);
                    break;
                }
                case GOT_COMMENTS_DESTROY_INFO: {
                    Toast.makeText(act, R.string.toast_delete_success, Toast.LENGTH_SHORT).show();
                    if (destroyCommentPos != -1)
                        comments.remove(destroyCommentPos);
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
                case GOT_COMMENTS_DESTROY_INFO_FAIL:
                case GOT_COMMENTS_BY_ME_INFO_FAIL:
                case GOT_COMMENTS_TO_ME_INFO_FAIL: {
                    if (msg.obj != null)
                        Toast.makeText(act, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                }
                case GOT_SET_REMIND_COUNT_INFO_FAIL: {
//                    Toast.makeText(act,
//                            R.string.toast_clear_unread_count_fail,
//                            Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            act.invalidateOptionsMenu();
        }

    };

    private boolean isCommentsToMe = true;

    private PullToRefreshAttacher mPullToRefreshAttacher;
    private MainTimelineActivity act;
    private CommentsToMeAdapter mAdapter;

    private int destroyCommentPos = -1;

    @Override
    public void onRefreshStarted(View view) {
        Log.e(TAG, "Refresh Comments");
        if (NetworkStatus.check(true)) {
            if (isCommentsToMe)
                refreshCommentsToMe();
            else
                refreshCommentsByMe();
        } else
            mHandler.sendEmptyMessage(GOT_COMMENTS_TO_ME_INFO_FAIL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.frag_main, container, false);

        lv = (ListView) v.findViewById(R.id.lv_friends_timeline);
        lv.setDivider(null);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        act = (MainTimelineActivity) getActivity();

        mPullToRefreshAttacher = act.getPullToRefreshAttacher();
        mPullToRefreshAttacher.addRefreshableView(lv, this);

        MyMaidActionHelper.commentsToMe(true, mHandler);

//        lv.setFastScrollAlwaysVisible(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
                                    long arg3) {
                if (isCommentsToMe) {
                    Intent i = new Intent(act, CommentRepost.class);
                    i.putExtra(IS_REPLY, true);
                    i.putExtra(WEIBO_ID, comments.get(arg2 - lv.getHeaderViewsCount()).getStatus().getId());
                    i.putExtra(COMMENT_ID, comments.get(arg2 - lv.getHeaderViewsCount()).getId());
                    startActivity(i);
                } else {
                    new AlertDialog.Builder(act).setTitle(R.string.frag_comments_dialog_comment_destroy_title)
                            .setPositiveButton(R.string.frag_ftl_dialog_confirm_logout_btn_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MyMaidActionHelper.commentsDestroy(comments.get(arg2 - lv.getHeaderViewsCount()).getId(), mHandler);
                                    destroyCommentPos = arg2 - lv.getHeaderViewsCount();
                                }
                            }).setNegativeButton(R.string.frag_ftl_dialog_confirm_logout_btn_cancle, null).show();
                }
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {

                CommentsBean c = comments.get(arg2 - lv.getHeaderViewsCount());

                if (c.getStatus().getRetweetedStatus() != null && c.getStatus().getRetweetedStatus().getUser() == null)
                    return false; // 微博已被删除不继续进行

                Intent i = new Intent(act, SingleWeiboActivity.class);
                i.putExtra(WEIBO_ID, comments.get(arg2 - lv.getHeaderViewsCount()).getStatus().getId());
                startActivity(i);

                return false;
            }
        });

        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (view.getCount() > (Integer.valueOf(AcquireCount.COMMENTS_TO_ME_COUNT) - 5) && view.getLastVisiblePosition() > (view.getCount() - 6) && !mPullToRefreshAttacher.isRefreshing() && comments != null) {
                    Log.e(TAG, "到底");

                    if (isCommentsToMe)
                        MyMaidActionHelper.commentsToMe(comments.get(view.getCount() - 1 - lv.getHeaderViewsCount()).getStatus().getId(), mHandler);
                    else
                        MyMaidActionHelper.commentsByMe(comments.get(view.getCount() - 1 - lv.getHeaderViewsCount()).getStatus().getId(), mHandler);

                    mPullToRefreshAttacher.setRefreshing(true);
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
        });

        mAdapter = new CommentsToMeAdapter(act);
        lv.addHeaderView(LayoutInflater.from(act).inflate(R.layout.lv_header_main_timeline, null), null, false);
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
                startActivity(new Intent(act, Post.class));
                break;
            }
            case MENU_UNREAD_COUNT: {
                getUnreadCommentsCount();
                String[] items = {act.getString(R.string.frag_comments_dialog_comments_to_me), act.getString(R.string.frag_comments_dialog_comments_by_me)};
                new AlertDialog.Builder(act).setTitle(R.string.frag_comments_dialog_title).setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mPullToRefreshAttacher.setRefreshing(true);
                        switch (i) {
                            case 0: {
                                isCommentsToMe = true;
                                MyMaidActionHelper.commentsToMe(true, mHandler);
                                break;
                            }
                            case 1: {
                                isCommentsToMe = false;
                                MyMaidActionHelper.commentsByMe(true, mHandler);
                                break;
                            }
                        }
                    }
                }).setNegativeButton(android.R.string.cancel, null).show();
                break;
            }
            case MENU_PROFILE_IMAGE: {
                if (GlobalContext.getUID().equals("1665287983")) {
                    Intent i = new Intent(act, SingleUser.class);
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

    public void getUnreadCommentsCount() {
        MyMaidActionHelper.remindUnreadCount(mHandler);
    }

    public void refreshCommentsToMe() {
        MyMaidActionHelper.commentsToMe(false, mHandler);
        mPullToRefreshAttacher.setRefreshing(true);
    }

    public void refreshCommentsByMe() {
        MyMaidActionHelper.commentsByMe(false, mHandler);
        mPullToRefreshAttacher.setRefreshing(true);
    }

    private void setListView(List<CommentsBean> comments) {
        mAdapter.setData(comments);
        lv.setAdapter(mAdapter);
    }

    private void updateListView(List<CommentsBean> comments) {
        mAdapter.setData(comments);
        mAdapter.notifyDataSetChanged();
    }

    public void scrollListViewToTop() {
        lv.smoothScrollToPosition(0);
    }
}
