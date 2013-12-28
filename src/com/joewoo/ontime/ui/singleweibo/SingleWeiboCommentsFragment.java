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
import com.joewoo.ontime.action.comments.CommentsShow;
import com.joewoo.ontime.action.statuses.StatusesFriendsTimeLine;
import com.joewoo.ontime.support.adapter.listview.SingleWeiboCmtsListViewAdapter;
import com.joewoo.ontime.support.bean.CommentsBean;
import com.joewoo.ontime.support.info.AcquireCount;
import com.joewoo.ontime.ui.CommentRepost;
import com.joewoo.ontime.ui.SingleUser;

import java.util.List;

import static com.joewoo.ontime.support.info.Defines.COMMENT_ID;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMNETS_SHOW_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMNETS_SHOW_ADD_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMNETS_SHOW_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.IS_REPLY;
import static com.joewoo.ontime.support.info.Defines.SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;


public class SingleWeiboCommentsFragment extends Fragment {

    private SingleWeiboActivity act;
    private ListView lv;
    private ProgressBar pb;
    private String weiboID;
    private TextView tv;
    private List<CommentsBean> comments;
    private SingleWeiboCmtsListViewAdapter adapter;

    public void showComments(String weiboID) {
        new CommentsShow(weiboID, mHandler).start();
        pb.setVisibility(View.VISIBLE);
        this.weiboID = weiboID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_singleweibo_comments_reposts, container, false);

        lv = (ListView) v.findViewById(R.id.lv_single_weibo_comments);
        pb = (ProgressBar) v.findViewById(R.id.pb_single_weibo_comments);
        tv = (TextView) v.findViewById(R.id.tv_single_weibo_comments);

        return v;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        act = (SingleWeiboActivity) getActivity();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent i = new Intent(act, CommentRepost.class);
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
                Intent i = new Intent(act, SingleUser.class);
                i.putExtra(SCREEN_NAME, comments.get(arg2).getUser().getScreenName());
                startActivity(i);
                return false;
            }
        });

        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int arg1, int arg2, int arg3) {


                // 滚到到尾刷新
                if (view.getCount() > (Integer.valueOf(AcquireCount.COMMENTS_SHOW_COUNT) - 2) && !act.isFreshing() && comments != null && comments.size() > 6 && view.getLastVisiblePosition() > (view.getCount() - 6)) {
                    Log.e(TAG, "到底");
                    new CommentsShow(weiboID, comments.get(view.getCount() - 1).getId(), mHandler).start();
                    act.setFreshing(true);
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });

        adapter = new SingleWeiboCmtsListViewAdapter(act);

    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            pb.setVisibility(View.GONE);
            act.setFreshing(false);

            switch (msg.what) {
                case GOT_COMMNETS_SHOW_INFO: {

                    comments = (List<CommentsBean>) msg.obj;

                    if(comments != null) {
                        if (comments.isEmpty()) {
                            tv.setVisibility(View.VISIBLE);
                            tv.setText(R.string.frag_single_weibo_no_comments);
                        } else {
                            setListView(comments);
                        }
                    }

                    act.setCommentsCount(comments.size());

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
