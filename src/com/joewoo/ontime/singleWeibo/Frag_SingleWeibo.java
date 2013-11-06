package com.joewoo.ontime.singleWeibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joewoo.ontime.R;
import com.joewoo.ontime.SingleUser;
import com.joewoo.ontime.action.Weibo_DownloadPic;
import com.joewoo.ontime.tools.NoUnderlineURLSpan;
import com.joewoo.ontime.tools.UserSpan;

import java.util.HashMap;

import static com.joewoo.ontime.info.Constants.BMIDDLE_PIC;
import static com.joewoo.ontime.info.Constants.CREATED_AT;
import static com.joewoo.ontime.info.Constants.IS_REPOST;
import static com.joewoo.ontime.info.Constants.PROFILE_IMAGE_URL;
import static com.joewoo.ontime.info.Constants.RETWEETED_STATUS;
import static com.joewoo.ontime.info.Constants.RETWEETED_STATUS_BMIDDLE_PIC;
import static com.joewoo.ontime.info.Constants.RETWEETED_STATUS_CREATED_AT;
import static com.joewoo.ontime.info.Constants.RETWEETED_STATUS_SCREEN_NAME;
import static com.joewoo.ontime.info.Constants.RETWEETED_STATUS_SOURCE;
import static com.joewoo.ontime.info.Constants.SCREEN_NAME;
import static com.joewoo.ontime.info.Constants.SOURCE;
import static com.joewoo.ontime.info.Constants.TAG;
import static com.joewoo.ontime.info.Constants.TEXT;
import static com.joewoo.ontime.info.Constants.USER_WEIBO;

public class Frag_SingleWeibo extends Fragment {

    private HashMap<String, String> map;
    
    private Activity act;

    private TextView tv_screen_name;
    private TextView tv_created_at;
    private TextView tv_source;
    private TextView tv_text;
    private TextView tv_rt_rl;
    private TextView tv_rt_screen_name;
    private TextView tv_rt_text;
    private TextView tv_rt_source;
    private TextView tv_rt_created_at;
    private ImageView iv_image;
    private ImageView iv_rt_image;
    private ImageView iv_profile_image;

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

        map = ((SingleWeibo) act).getSingleWeiboMap();

        tv_screen_name.setText(map.get(SCREEN_NAME));
        tv_created_at.setText(" · " + map.get(CREATED_AT));

        tv_text.setText(checkMentionsURL(map.get(TEXT)));
        tv_text.setMovementMethod(LinkMovementMethod.getInstance());

//        new Weibo_CommentsShow(map.get(WEIBO_ID), mHandler).start();

        tv_source.setText(map.get(SOURCE));


        if (map.get(IS_REPOST) == null) {
            tv_rt_rl.setVisibility(View.GONE);
            tv_rt_screen_name.setVisibility(View.GONE);
            tv_rt_created_at.setVisibility(View.GONE);
            tv_rt_text.setVisibility(View.GONE);
            tv_rt_source.setVisibility(View.GONE);
            iv_rt_image.setVisibility(View.GONE);
        } else {
            if (map.get(RETWEETED_STATUS_BMIDDLE_PIC) == null)
                iv_rt_image.setVisibility(View.GONE);
            else {
                new Weibo_DownloadPic(iv_rt_image, tv_rt_rl, true, act).execute(map
                        .get(RETWEETED_STATUS_BMIDDLE_PIC));

            }
            tv_rt_screen_name.setText(map
                    .get(RETWEETED_STATUS_SCREEN_NAME));
            tv_rt_created_at.setText(" · " + map
                    .get(RETWEETED_STATUS_CREATED_AT));

            tv_rt_text.setText(checkMentionsURL(map.get(RETWEETED_STATUS)));
            tv_rt_text.setMovementMethod(LinkMovementMethod.getInstance());

            tv_rt_source.setText(map.get(RETWEETED_STATUS_SOURCE));


            if (map.get(USER_WEIBO) == null) {
                tv_rt_screen_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        jumpToRetweetedUser(map.get(RETWEETED_STATUS_SCREEN_NAME));
                    }
                });
                tv_rt_source.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        jumpToRetweetedUser(map.get(RETWEETED_STATUS_SCREEN_NAME));
                    }
                });
                tv_rt_created_at.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        jumpToRetweetedUser(map.get(RETWEETED_STATUS_SCREEN_NAME));
                    }
                });
            }

        }

        if (map.get(BMIDDLE_PIC) == null) {
            if (map.get(RETWEETED_STATUS_BMIDDLE_PIC) == null) {
                ViewGroup.LayoutParams lp = tv_rt_rl.getLayoutParams();
                lp.width = 10000;
                tv_rt_rl.setLayoutParams(lp);
            }
            iv_image.setVisibility(View.GONE);
        } else {

            dp = new Weibo_DownloadPic(iv_image, tv_rt_rl, false, act);
            dp.execute(map.get(BMIDDLE_PIC));

        }

        iv_profile_image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new Weibo_DownloadPic(iv_profile_image).execute(map
                        .get(PROFILE_IMAGE_URL));
                iv_profile_image.setClickable(false);
            }
        });

        if (map.get(USER_WEIBO) == null) {
            tv_screen_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpToSingleUser(map.get(SCREEN_NAME));
                }
            });
            tv_created_at.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpToSingleUser(map.get(SCREEN_NAME));
                }
            });
            tv_source.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    jumpToSingleUser(map.get(SCREEN_NAME));
                }
            });
        }
    }

    private SpannableString checkMentionsURL(String str) {

        char strarray[] = str.toCharArray();
        SpannableString ss = new SpannableString(str);

        try {
            for (int i = 0; i < str.length(); i++) {

                if (strarray[i] == '@') {
                    StringBuilder sb = new StringBuilder();
                    for (int j = i + 1; j < str.length() + 1; j++) {
                        if (strarray[j] != ' ' && strarray[j] != ':'
                                && strarray[j] != '/' && strarray[j] != '…'
                                && strarray[j] != '。' && strarray[j] != '，'
                                && strarray[j] != '@' && strarray[j] != '：'
                                && strarray[j] != '\0' && strarray[j] != '（'
                                && strarray[j] != '！' && strarray[j] != '.'
                                && strarray[j] != ',' && strarray[j] != '）') {
                            sb.append(strarray[j]);
                        } else {
                            Log.e(TAG, "@" + sb.toString());

                            ss.setSpan(new UserSpan(sb.toString(), act), i, j,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            i = j;
                            break;
                        }
                    }
                }


                if (strarray[i] == '#') {
                    StringBuilder sb = new StringBuilder("http://s.weibo.com/weibo/");
                    for (int j = i + 1; j < str.length(); j++) {
                        if (strarray[j] != '#') {
                            sb.append(strarray[j]);
                        } else {
                            Log.e(TAG, sb.toString());

                            ss.setSpan(new NoUnderlineURLSpan(sb.toString(), act), i, j + 1,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            i = j + 1;
                            break;
                        }
                    }
                }


                if (strarray[i] == 'h' && strarray[i + 4] == ':'
                        && strarray[i + 5] == '/') {
                    StringBuilder sb = new StringBuilder("http://t.cn/");

                    int j;
                    for (j = i + 12; j < i + 19; j++) {
                        sb.append(strarray[j]);
                    }

                    ss.setSpan(new NoUnderlineURLSpan(sb.toString(), act), i, j,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    Log.e(TAG, "HTTP URL - " + sb.toString());
                    i = j;

                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Check mentions FAILED");
        }

        strarray = null;
        return ss;
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

    void jumpToSingleUser(String screenName) {
        Intent it = new Intent(act, SingleUser.class);
        it.putExtra(SCREEN_NAME, screenName);
        startActivity(it);
    }

    void jumpToRetweetedUser(String retweetedScreenName) {
        Intent it = new Intent(act, SingleUser.class);
        it.putExtra(SCREEN_NAME, retweetedScreenName);
        startActivity(it);
    }


}
