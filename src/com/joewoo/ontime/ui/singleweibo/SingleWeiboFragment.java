package com.joewoo.ontime.ui.singleweibo;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.adapter.gridview.SingleWeiboGirdViewAdapter;
import com.joewoo.ontime.support.bean.StatusesBean;
import com.joewoo.ontime.support.menu.CopyTextContextualMenu;
import com.joewoo.ontime.support.net.DownloadPic;
import com.joewoo.ontime.support.net.DownloadUserProfileImage;
import com.joewoo.ontime.support.util.CheckMentionsURLTopic;
import com.joewoo.ontime.support.util.GlobalContext;
import com.joewoo.ontime.ui.SingleUser;

import java.io.File;

import static com.joewoo.ontime.support.info.Defines.SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.TEMP_IMAGE_NAME;
import static com.joewoo.ontime.support.info.Defines.TEMP_IMAGE_PATH;

public class SingleWeiboFragment extends Fragment {

    private StatusesBean status;

    private SingleWeiboActivity act;

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
    private ImageView iv_profile_image;
    private ProgressBar pb;
    private ScrollView sv;
    private GridView gv;

    private Animation in;
    private Animation out;

    private DownloadPic dp;

    public SingleWeiboFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_singleweibo, container, false);
        findViews(v);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        act = (SingleWeiboActivity) getActivity();
        act.setSingleWeiboFragment();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dp != null && dp.getStatus() != AsyncTask.Status.FINISHED) {
            Log.e(TAG, "Cancelled Thread");
            dp.cancel(true);
        }
    }

    public void setSingleWeibo(StatusesBean status) {
        this.status = status;
        setStatus();
    }

    public void setSingleWeiboWithAnim(StatusesBean status) {
        this.in = AnimationUtils.loadAnimation(GlobalContext.getAppContext(), R.anim.in);
        this.out = AnimationUtils.loadAnimation(GlobalContext.getAppContext(), R.anim.out);
        setViewShow();
        this.status = status;
        setStatus();
    }

    private void setStatus() {

        tv_screen_name.setText(status.getUser().getScreenName());
        tv_created_at.setText(" · " + status.getCreatedAt());

        tv_text.setText(CheckMentionsURLTopic.getSpannableString(status.getText(), act));
        tv_text.setMovementMethod(LinkMovementMethod.getInstance());

        tv_source.setText(" · " + status.getSource());
        if (status.getSource().equals(getString(R.string.app_name_cn))) {
            tv_source.setTextColor(getResources().getColor(R.color.textGrey));
            tv_source.setShadowLayer(20, 0, 0, getResources().getColor(R.color.sourcePink));
        }


        iv_profile_image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DownloadUserProfileImage(iv_profile_image).execute(status.getUser().getProfileImageUrl());
                iv_profile_image.setClickable(false);
            }
        });

        tv_screen_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToSingleUser(status.getUser().getScreenName());
            }
        });
        tv_created_at.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToSingleUser(status.getUser().getScreenName());
            }
        });
        tv_source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToSingleUser(status.getUser().getScreenName());
            }
        });

        setRetweetedStatus();
        setImage();
        setLongClickCopyText();

    }

    private void setRetweetedStatus() {
        if (status.getRetweetedStatus() == null) {
            tv_rt_rl.setVisibility(View.GONE);
            tv_rt_screen_name.setVisibility(View.GONE);
            tv_rt_created_at.setVisibility(View.GONE);
            tv_rt_text.setVisibility(View.GONE);
            tv_rt_source.setVisibility(View.GONE);
//            iv_rt_image.setVisibility(View.GONE);
        } else {

            tv_rt_screen_name.setText(status.getRetweetedStatus().getUser().getScreenName());
            tv_rt_created_at.setText(" · " + status.getRetweetedStatus().getCreatedAt());

            tv_rt_text.setText(CheckMentionsURLTopic.getSpannableString(status.getRetweetedStatus().getText(), act));
            tv_rt_text.setMovementMethod(LinkMovementMethod.getInstance());

            tv_rt_source.setText(" · " + status.getRetweetedStatus().getSource());
            if (status.getRetweetedStatus().getSource().equals(getString(R.string.app_name_cn))) {
                tv_rt_source.setTextColor(getResources().getColor(R.color.textGrey));
                tv_rt_source.setShadowLayer(20, 0, 0, getResources().getColor(R.color.sourcePink));
            }

            tv_rt_screen_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    jumpToRetweetedUser(status.getRetweetedStatus().getUser().getScreenName());
                }
            });
            tv_rt_source.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    jumpToRetweetedUser(status.getRetweetedStatus().getUser().getScreenName());
                }
            });
            tv_rt_created_at.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    jumpToRetweetedUser(status.getRetweetedStatus().getUser().getScreenName());
                }
            });

        }
    }

    private void setImage() {

        if (status.getPicURLs() != null && status.getPicURLs().size() > 1) {
            // 原创多图微博

            gv.setAdapter(new SingleWeiboGirdViewAdapter(act, status.getPicURLs()));

            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    new DownloadPic(null, tv_rt_rl, false, act).execute(status.getPicURLs().get(position).getBmiddlePic());
                }
            });

        } else if (status.getRetweetedStatus() != null && status.getRetweetedStatus().getPicURLs() != null && status.getRetweetedStatus().getPicURLs().size() > 1) {
            // 转发多图微博

            ViewGroup.LayoutParams lp = tv_rt_rl.getLayoutParams();
            lp.width = 10000;
            tv_rt_rl.setLayoutParams(lp);

            gv.setAdapter(new SingleWeiboGirdViewAdapter(act, status.getRetweetedStatus().getPicURLs()));

            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    new DownloadPic(null, tv_rt_rl, true, act).execute(status.getRetweetedStatus().getPicURLs().get(position).getBmiddlePic());
                }
            });

        } else if (status.getBmiddlePic() != null) {
            // 原创有图微博

            dp = new DownloadPic(iv_image, tv_rt_rl, false, act);
            dp.execute(status.getBmiddlePic());
            iv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpToGallery();
                }
            });

        } else if (status.getRetweetedStatus() != null && status.getRetweetedStatus().getBmiddlePic() != null) {
            // 转发有图微博

            dp = new DownloadPic(iv_image, tv_rt_rl, true, act);
            dp.execute(status.getRetweetedStatus().getBmiddlePic());
            iv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpToGallery();
                }
            });

        } else {
            // 什么都木有

            if (status.getRetweetedStatus() != null && status.getRetweetedStatus().getBmiddlePic() == null) {
                ViewGroup.LayoutParams lp = tv_rt_rl.getLayoutParams();
                lp.width = 10000;
                tv_rt_rl.setLayoutParams(lp);
            }
        }
    }

    private void setLongClickCopyText() {
        if (tv_text.getText() != null) {
            tv_text.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    act.startActionMode(new CopyTextContextualMenu(tv_text.getText().toString()));
                    return false;
                }
            });
        }
        if (tv_rt_text.getText() != null) {
            tv_rt_text.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    act.startActionMode(new CopyTextContextualMenu(tv_rt_text.getText().toString()));
                    return false;
                }
            });
        }

    }

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
        iv_profile_image = (ImageView) v.findViewById(R.id.frag_single_weibo_profile_image);
        pb = (ProgressBar) v.findViewById(R.id.frag_single_weibo_pb);
        sv = (ScrollView) v.findViewById(R.id.frag_single_weibo_sv);
        gv = (GridView) v.findViewById(R.id.frag_single_weibo_pics_grid);
    }

    private void jumpToSingleUser(String screenName) {
        Intent it = new Intent(act, SingleUser.class);
        it.putExtra(SCREEN_NAME, screenName);
        startActivity(it);
    }

    private void jumpToRetweetedUser(String retweetedScreenName) {
        Intent it = new Intent(act, SingleUser.class);
        it.putExtra(SCREEN_NAME, retweetedScreenName);
        startActivity(it);
    }

    private void jumpToGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(TEMP_IMAGE_PATH, TEMP_IMAGE_NAME)), "image/*");
        startActivity(intent);
    }

    public void setViewHide() {
        sv.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
    }

    public void setViewShow() {
        sv.setVisibility(View.VISIBLE);
        sv.startAnimation(in);
        pb.setVisibility(View.GONE);
        pb.startAnimation(out);
    }

}
