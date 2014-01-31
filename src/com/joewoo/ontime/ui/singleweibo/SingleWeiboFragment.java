package com.joewoo.ontime.ui.singleweibo;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.adapter.gridview.MuiltPhotosAdapter;
import com.joewoo.ontime.support.bean.StatusesBean;
import com.joewoo.ontime.support.image.DownloadPhoto;
import com.joewoo.ontime.support.image.DownloadSinglePhoto;
import com.joewoo.ontime.support.image.DownloadUserProfileImage;
import com.joewoo.ontime.support.info.Defines;
import com.joewoo.ontime.support.listener.MyMaidListeners;
import com.joewoo.ontime.support.menu.CopyTextContextualMenu;
import com.joewoo.ontime.support.util.GlobalContext;
import com.joewoo.ontime.support.util.MyMaidUtilites;
import com.joewoo.ontime.support.view.gridview.MuiltPhotosGirdView;
import com.joewoo.ontime.ui.Photo;
import com.joewoo.ontime.ui.SingleUser;

import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.USER_BEAN;

public class SingleWeiboFragment extends Fragment {

    private StatusesBean status;

    private SingleWeiboActivity act;
    private MyMaidListeners.FragmentReadyListener fragmentReadyListener;

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
    private MuiltPhotosGirdView gv;

    private Animation in;
    private Animation out;

    private DownloadPhoto dp;
    private DownloadSinglePhoto dsp;
    private MuiltPhotosAdapter muiltPhotosAdapter;

    public SingleWeiboFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_single_weibo, container, false);
        findViews(v);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        act = (SingleWeiboActivity) getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (fragmentReadyListener != null)
            fragmentReadyListener.fragmentReady();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dp != null) {
            Log.e(TAG, "Cancel Thread");
            dp.cancel(true);
        }
        if (muiltPhotosAdapter != null) {
            Log.e(TAG, "Cancel All Thread");
            muiltPhotosAdapter.cancelAllTasks();
        }
        if (dsp != null) {
            Log.e(TAG, "Cancel Single Photo Thread");
            dsp.cancel(true);
        }
    }

    public void setSingleWeibo(StatusesBean status) {
        if (pb.getVisibility() == View.VISIBLE) {
            in = AnimationUtils.loadAnimation(GlobalContext.getAppContext(), R.anim.in);
            out = AnimationUtils.loadAnimation(GlobalContext.getAppContext(), R.anim.out);
            setViewShow();
        }
        this.status = status;
        setStatus();
    }

    private void setStatus() {

        tv_screen_name.setText(status.getUser().getScreenName());
        tv_created_at.setText(" · " + status.getCreatedAt());

        tv_text.setText(MyMaidUtilites.CheckMentionsURLTopic.getSpannableString(status.getText(), act));
        tv_text.setMovementMethod(LinkMovementMethod.getInstance());

        tv_source.setText(" · " + status.getSource());
        if (status.getSource().equals(getString(R.string.app_name_cn))) {
            tv_source.setTextColor(getResources().getColor(R.color.greyText));
            tv_source.setShadowLayer(20, 0, 0, getResources().getColor(R.color.pinkSource));
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
                jumpToSingleUser();
            }
        });
        tv_created_at.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToSingleUser();
            }
        });
        tv_source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToSingleUser();
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

            tv_rt_text.setText(MyMaidUtilites.CheckMentionsURLTopic.getSpannableString(status.getRetweetedStatus().getText(), act));
            tv_rt_text.setMovementMethod(LinkMovementMethod.getInstance());

            tv_rt_source.setText(" · " + status.getRetweetedStatus().getSource());
            if (status.getRetweetedStatus().getSource().equals(getString(R.string.app_name_cn))) {
                tv_rt_source.setTextColor(getResources().getColor(R.color.greyText));
                tv_rt_source.setShadowLayer(20, 0, 0, getResources().getColor(R.color.pinkSource));
            }

            tv_rt_screen_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    jumpToRetweetedUser();
                }
            });
            tv_rt_source.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    jumpToRetweetedUser();
                }
            });
            tv_rt_created_at.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    jumpToRetweetedUser();
                }
            });

        }
    }

    private void setImage() {

        if (status.getPicURLs() != null && status.getPicURLs().size() > 1) {
            // 原创多图微博

            muiltPhotosAdapter = new MuiltPhotosAdapter(act, status.getPicURLs(), sv);
            gv.setAdapter(muiltPhotosAdapter);

            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dsp = new DownloadSinglePhoto(tv_rt_rl, false, act);
                    dsp.execute(status.getPicURLs().get(position).getBmiddlePic());
                }
            });

        } else if (status.getRetweetedStatus() != null && status.getRetweetedStatus().getPicURLs() != null && status.getRetweetedStatus().getPicURLs().size() > 1) {
            // 转发多图微博

            ViewGroup.LayoutParams lp = tv_rt_rl.getLayoutParams();
            lp.width = 10000;
            tv_rt_rl.setLayoutParams(lp);

            muiltPhotosAdapter = new MuiltPhotosAdapter(act, status.getRetweetedStatus().getPicURLs(), sv);
            gv.setAdapter(muiltPhotosAdapter);

            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dsp = new DownloadSinglePhoto(tv_rt_rl, true, act);
                    dsp.execute(status.getRetweetedStatus().getPicURLs().get(position).getBmiddlePic());
                }
            });


        } else if (status.getBmiddlePic() != null) {
            // 原创有图微博

            dp = new DownloadPhoto(iv_image, tv_rt_rl, false, act);
            dp.execute(status.getBmiddlePic());

            iv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(act, Photo.class);
                    i.putExtra(Defines.PHOTO_BYTES, act.getImageBytes());
                    act.startActivity(i);
                }
            });

        } else if (status.getRetweetedStatus() != null && status.getRetweetedStatus().getBmiddlePic() != null) {
            // 转发有图微博

            dp = new DownloadPhoto(iv_image, tv_rt_rl, true, act);
            dp.execute(status.getRetweetedStatus().getBmiddlePic());

            iv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(act, Photo.class);
                    i.putExtra(Defines.PHOTO_BYTES, act.getImageBytes());
                    act.startActivity(i);
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
        gv = (MuiltPhotosGirdView) v.findViewById(R.id.frag_single_weibo_pics_grid);
    }

    private void jumpToSingleUser() {
        Intent ii = new Intent(act, SingleUser.class);
        ii.putExtra(USER_BEAN, status.getUser());
        startActivity(ii);
    }

    private void jumpToRetweetedUser() {
        Intent ii = new Intent(act, SingleUser.class);
        ii.putExtra(USER_BEAN, status.getRetweetedStatus().getUser());
        startActivity(ii);
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

    public void setFragmentReadyListener(MyMaidListeners.FragmentReadyListener listener) {
        this.fragmentReadyListener = listener;
    }
}
