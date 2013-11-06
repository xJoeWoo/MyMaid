package com.joewoo.ontime.singleWeibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.joewoo.ontime.Comment_Repost;
import com.joewoo.ontime.R;
import com.joewoo.ontime.action.Weibo_RepostTimeline;
import com.joewoo.ontime.info.Weibo_AcquireCount;

import java.util.ArrayList;
import java.util.HashMap;

import static com.joewoo.ontime.info.Constants.GOT_REPOST_TIMELINE_INFO;
import static com.joewoo.ontime.info.Constants.GOT_REPOST_TIMELINE_INFO_FAIL;
import static com.joewoo.ontime.info.Constants.IS_COMMENT;
import static com.joewoo.ontime.info.Constants.SCREEN_NAME;
import static com.joewoo.ontime.info.Constants.TEXT;
import static com.joewoo.ontime.info.Constants.WEIBO_ID;


public class Frag_SingleWeibo_Reposts extends Fragment {

    private Activity act;
    private ListView lv;
    private ProgressBar pb;
    private String weibo_id;
    private TextView tv;

    public void showReposts(String weibo_id){
        this.weibo_id = weibo_id;
        new Weibo_RepostTimeline(Weibo_AcquireCount.REPOSTS_TIMELINE_COUNT, weibo_id, mHandler).start();
    }

    Handler mHandler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {

            pb.setVisibility(View.GONE);

            switch (msg.what) {
                case GOT_REPOST_TIMELINE_INFO: {

                    final ArrayList<HashMap<String, String>> text = (ArrayList<HashMap<String, String>>) msg.obj;

                    String[] from = { SCREEN_NAME, TEXT };
                    int[] to = { R.id.comments_show_screen_name,
                            R.id.comments_show_text };

                    SimpleAdapter data = new SimpleAdapter(act , text,
                            R.layout.comments_show_lv, from, to);

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0,
                                                View arg1, int arg2, long arg3) {

                            Intent it = new Intent();
                            it.setClass(act, Comment_Repost.class);
                            it.putExtra(IS_COMMENT, true);
                            it.putExtra(WEIBO_ID, weibo_id);
                            startActivity(it);

                        }
                    });

                    lv.setAdapter(data);

                    if(lv.getCount() == 0)
                    {
                        tv.setVisibility(View.VISIBLE);
                        tv.setText(R.string.frag_single_weibo_no_reposts);
                    }

                    break;
                }
                case GOT_REPOST_TIMELINE_INFO_FAIL:{
                    Toast.makeText(act, R.string.toast_repost_timeline_fail, Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    };


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_singleweibo_comments_reposts, container, false);

        lv = (ListView)v.findViewById(R.id.frag_single_weibo_comments_lv);
        pb = (ProgressBar)v.findViewById(R.id.frag_single_weibo_comments_pb);
        tv = (TextView)v.findViewById(R.id.frag_single_weibo_comments_tv);

        return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        act = getActivity();
	}

//	@Override
//	public void onPrepareOptionsMenu(Menu menu) {
//		menu.clear();
//
//
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//
//		}
//
//		return super.onOptionsItemSelected(item);
//	}


}
