package com.joewoo.ontime.singleWeibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joewoo.ontime.R;
import com.joewoo.ontime.SingleUser;
import com.joewoo.ontime.action.Weibo_DownloadPic;

import static com.joewoo.ontime.info.Defines.BMIDDLE_PIC;
import static com.joewoo.ontime.info.Defines.CREATED_AT;
import static com.joewoo.ontime.info.Defines.IS_REPOST;
import static com.joewoo.ontime.info.Defines.PROFILE_IMAGE_URL;
import static com.joewoo.ontime.info.Defines.RETWEETED_STATUS;
import static com.joewoo.ontime.info.Defines.RETWEETED_STATUS_BMIDDLE_PIC;
import static com.joewoo.ontime.info.Defines.RETWEETED_STATUS_CREATED_AT;
import static com.joewoo.ontime.info.Defines.RETWEETED_STATUS_SCREEN_NAME;
import static com.joewoo.ontime.info.Defines.RETWEETED_STATUS_SOURCE;
import static com.joewoo.ontime.info.Defines.SCREEN_NAME;
import static com.joewoo.ontime.info.Defines.SOURCE;
import static com.joewoo.ontime.info.Defines.TEXT;
import static com.joewoo.ontime.info.Defines.USER_WEIBO;

public class Frag_SingleWeibo extends Fragment {

    private Intent i;

    private Activity act;

    TextView tv_screen_name;
    TextView tv_created_at;
    TextView tv_source;
    TextView tv_text;
    TextView tv_rt_rl;
    TextView tv_rt_screen_name;
    TextView tv_rt_text;
    TextView tv_rt_source;
    TextView tv_rt_created_at;
    ImageView iv_image;
    ImageView iv_rt_image;
    ImageView iv_profile_image;
    Weibo_DownloadPic dp;

    public Frag_SingleWeibo() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//		setHasOptionsMenu(true);

        View v = inflater.inflate(R.layout.fragment_singleweibo, container, false);

        findViews(v);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        act = getActivity();

        i = ((SingleWeibo) act).getSingleWeiboIntent();

        tv_screen_name.setText(i.getStringExtra(SCREEN_NAME));
        tv_created_at.setText(" · " + i.getStringExtra(CREATED_AT));

        tv_text.setText(i.getStringExtra(TEXT));

//        new Weibo_CommentsShow(i.getStringExtra(WEIBO_ID), mHandler).start();

        tv_source.setText(i.getStringExtra(SOURCE));


        if (i.getStringExtra(IS_REPOST) == null) {
            tv_rt_rl.setVisibility(View.GONE);
            tv_rt_screen_name.setVisibility(View.GONE);
            tv_rt_created_at.setVisibility(View.GONE);
            tv_rt_text.setVisibility(View.GONE);
            tv_rt_source.setVisibility(View.GONE);
            iv_rt_image.setVisibility(View.GONE);
        } else {
            if (i.getStringExtra(RETWEETED_STATUS_BMIDDLE_PIC) == null)
                iv_rt_image.setVisibility(View.GONE);
            else {
                new Weibo_DownloadPic(iv_rt_image, tv_rt_rl, true, act).execute(i
                        .getStringExtra(RETWEETED_STATUS_BMIDDLE_PIC));

            }
            tv_rt_screen_name.setText(i
                    .getStringExtra(RETWEETED_STATUS_SCREEN_NAME));
            tv_rt_created_at.setText(" · " + i
                    .getStringExtra(RETWEETED_STATUS_CREATED_AT));
            tv_rt_text.setText(i.getStringExtra(RETWEETED_STATUS));
            tv_rt_source.setText(i.getStringExtra(RETWEETED_STATUS_SOURCE));


            if (i.getStringExtra(USER_WEIBO) == null) {
                tv_rt_screen_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        jumpToRetweetedUser(i);
                    }
                });
                tv_rt_source.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        jumpToRetweetedUser(i);
                    }
                });
                tv_rt_created_at.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        jumpToRetweetedUser(i);
                    }
                });
            }

        }

        if (i.getStringExtra(BMIDDLE_PIC) == null) {
            if (i.getStringExtra(RETWEETED_STATUS_BMIDDLE_PIC) == null) {
                ViewGroup.LayoutParams lp = tv_rt_rl.getLayoutParams();
                lp.width = 10000;
                tv_rt_rl.setLayoutParams(lp);
            }
            iv_image.setVisibility(View.GONE);
        } else {

            dp = new Weibo_DownloadPic(iv_image, tv_rt_rl, false, act);
            dp.execute(i.getStringExtra(BMIDDLE_PIC));

        }

        iv_profile_image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new Weibo_DownloadPic(iv_profile_image).execute(i
                        .getStringExtra(PROFILE_IMAGE_URL));
                iv_profile_image.setClickable(false);
            }
        });

        if (i.getStringExtra(USER_WEIBO) == null) {
            tv_screen_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpToSingleUser(i);
                }
            });
            tv_created_at.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpToSingleUser(i);
                }
            });
            tv_source.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    jumpToSingleUser(i);
                }
            });
        }


    }

//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//
//        menu.clear();
//
//    }

    private void findViews(View v) {
        tv_screen_name = (TextView) v.findViewById(R.id.frag_single_weibo_screen_name);
        tv_created_at = (TextView) v.findViewById(R.id.frag_single_weibo_created_at);
        tv_text = (TextView) v.findViewById(R.id.frag_single_weibo_text);
        tv_rt_rl = (TextView) v.findViewById(R.id.frag_single_weibo_retweeted_status_rl);
        tv_rt_screen_name = (TextView) v.findViewById(R.id.frag_single_weibo_retweeted_status_screen_name);
        tv_rt_created_at = (TextView) v.findViewById(R.id.frag_single_weibo_retweeted_status_created_at);
        tv_rt_source = (TextView) v.findViewById(R.id.frag_single_weibo_retweeted_status_source);
        tv_rt_text = (TextView) v.findViewById(R.id.frag_single_weibo_retweeted_status);
        tv_source = (TextView) v.findViewById(R.id.frag_single_weibo_source);
        iv_image = (ImageView) v.findViewById(R.id.frag_single_weibo_image);
        iv_rt_image = (ImageView) v.findViewById(R.id.frag_single_weibo_retweeted_status_weibo_image);
        iv_profile_image = (ImageView) v.findViewById(R.id.frag_single_weibo_profile_image);
    }

    void jumpToSingleUser(Intent i) {
        Intent it = new Intent();
        it.setClass(act, SingleUser.class);
        it.putExtra(SCREEN_NAME, i.getStringExtra(SCREEN_NAME));
        startActivity(it);
    }

    void jumpToRetweetedUser(Intent i) {
        Intent it = new Intent();
        it.setClass(act, SingleUser.class);
        // it.putExtra(UID, i.getStringExtra(UID));
        it.putExtra(SCREEN_NAME, i.getStringExtra(RETWEETED_STATUS_SCREEN_NAME));
        startActivity(it);
    }


}
