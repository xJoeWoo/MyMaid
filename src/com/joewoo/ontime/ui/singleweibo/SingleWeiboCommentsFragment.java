package com.joewoo.ontime.ui.singleweibo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.joewoo.ontime.R;
import com.joewoo.ontime.action.MyMaidActionHelper;
import com.joewoo.ontime.support.adapter.listview.SingleWeiboCmtsAdapter;
import com.joewoo.ontime.support.bean.CommentsBean;
import com.joewoo.ontime.support.bean.CommentsToMeBean;
import com.joewoo.ontime.support.info.AcquireCount;
import com.joewoo.ontime.ui.CommentRepostActivity;
import com.joewoo.ontime.ui.SingleUserActivity;

import java.util.List;

import static com.joewoo.ontime.support.info.Defines.COMMENT_ID;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMNETS_SHOW_ADD_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMNETS_SHOW_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMNETS_SHOW_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.IS_REPLY;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.USER_BEAN;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;


public class SingleWeiboCommentsFragment extends Fragment {

    private SingleWeiboActivity act;
    private ListView lv;
    private ProgressBar pb;
    private String weiboID;
    private TextView tv;
    private List<CommentsBean> comments;
    private SingleWeiboCmtsAdapter adapter;
    private boolean isFreshing;
    private int totalNumber = -1;

    public void showComments() {
        weiboID = act.getSingleWeiboFragment().getWeiboID();
        MyMaidActionHelper.commentsShow(weiboID, mHandler);
        pb.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frag_single_weibo_comments_reposts, container, false);

        lv = (ListView) v.findViewById(R.id.lv_single_weibo_comments);
        pb = (ProgressBar) v.findViewById(R.id.pb_single_weibo_comments);
        tv = (TextView) v.findViewById(R.id.tv_single_weibo_comments);

        return v;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        act = (SingleWeiboActivity) getActivity();

        lv.setFastScrollAlwaysVisible(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent i = new Intent(act, CommentRepostActivity.class);
                i.putExtra(IS_REPLY, true);
                i.putExtra(WEIBO_ID, comments.get(arg2).getStatus().getId());
                i.putExtra(COMMENT_ID, comments.get(arg2).getId());
                startActivity(i);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                Intent i = new Intent(act, SingleUserActivity.class);
                i.putExtra(USER_BEAN, comments.get(arg2).getUser());
                startActivity(i);
                return false;
            }
        });

        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int arg1, int arg2, int arg3) {


                // 滚到到尾刷新
                if (view.getCount() > (Integer.valueOf(AcquireCount.COMMENTS_SHOW_COUNT) - 2) && !isFreshing && comments != null && comments.size() > 6 && view.getLastVisiblePosition() > (view.getCount() - 6)) {
                    if (totalNumber == -1 | view.getCount() < totalNumber) {
                        Log.e(TAG, "到底");
                        MyMaidActionHelper.commentsShow(weiboID, comments.get(view.getCount() - 1).getId(), mHandler);
                        isFreshing = true;
                    }
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });

        adapter = new SingleWeiboCmtsAdapter(act);

    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            pb.setVisibility(View.GONE);
            isFreshing = false;

            switch (msg.what) {
                case GOT_COMMNETS_SHOW_INFO: {

                    CommentsToMeBean b = (CommentsToMeBean) msg.obj;

                    comments = b.getComments();

                    if (comments != null) {
                        if (comments.isEmpty()) {
                            tv.setVisibility(View.VISIBLE);
                            tv.setText(R.string.frag_single_weibo_no_comments);
                        } else {
                            tv.setVisibility(View.GONE);
                            setListView(comments);
                        }
                    }

                    totalNumber = b.getTotalNumber();
                    act.setCommentsCount(totalNumber);

                    break;
                }
                case GOT_COMMNETS_SHOW_ADD_INFO: {
                    comments.addAll((List<CommentsBean>) msg.obj);
                    setListView(comments);
                    break;
                }
                case GOT_COMMNETS_SHOW_INFO_FAIL: {
                    Toast.makeText(act, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }

    };

    private void setListView(List<CommentsBean> comments) {
        adapter.setData(comments);
        if (lv.getAdapter() == null)
            lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


}
