package com.joewoo.ontime.ui.singleweibo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.joewoo.ontime.action.MyMaidActionHelper;
import com.joewoo.ontime.support.adapter.gridview.MuiltPhotosAdapter;
import com.joewoo.ontime.support.bean.StatusesBean;
import com.joewoo.ontime.support.image.DownloadGIFPhoto;
import com.joewoo.ontime.support.image.DownloadPhoto;
import com.joewoo.ontime.support.image.DownloadSinglePhoto;
import com.joewoo.ontime.support.image.DownloadUserProfileImage;
import com.joewoo.ontime.support.info.Defines;
import com.joewoo.ontime.support.menu.CopyTextContextualMenu;
import com.joewoo.ontime.support.util.GlobalContext;
import com.joewoo.ontime.support.util.MyMaidUtilites;
import com.joewoo.ontime.support.view.gridview.MuiltPhotosGirdView;
import com.joewoo.ontime.ui.Photo;
import com.joewoo.ontime.ui.SingleUser;

import java.io.File;
import java.text.DecimalFormat;

import static com.joewoo.ontime.support.info.Defines.GOT_STATUSES_SHOW_INFO;
import static com.joewoo.ontime.support.info.Defines.STATUS_BEAN;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.USER_BEAN;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;

public class SingleWeiboFragment extends Fragment {

    private StatusesBean status;
    private String weiboID;
    private Intent i;
    private File imgFile;

    private SingleWeiboActivity act;

    private View v;

    private TextView tv_screen_name;
    private TextView tv_created_at;
    private TextView tv_source;
    private TextView tv_text;
    private TextView tv_rt_rl;
    private TextView tv_rt_screen_name;
    private TextView tv_rt_text;
    private TextView tv_rt_source;
    private TextView tv_rt_created_at;
    private TextView tv_load_gif;
    private ImageView iv_image;
    private ImageView iv_profile_image;
    private ProgressBar pb;
    private ScrollView sv;
    private MuiltPhotosGirdView gv;

    private Animation in;
    private Animation out;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == GOT_STATUSES_SHOW_INFO) {
                status = (StatusesBean) msg.obj;
                weiboID = status.getId();
                in = AnimationUtils.loadAnimation(GlobalContext.getAppContext(), R.anim.in);
                out = AnimationUtils.loadAnimation(GlobalContext.getAppContext(), R.anim.out);
                setViewShow();
                setStatus();
            }
        }
    };
    private DownloadPhoto dp;
    private DownloadSinglePhoto dsp;
    private DownloadGIFPhoto dGifp;
    private MuiltPhotosAdapter muiltPhotosAdapter;

    public SingleWeiboFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "FRAGMENT ON CREATE VIEW");
        v = inflater.inflate(R.layout.frag_single_weibo, container, false);
        findViews();
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(Defines.PHOTO_FILE, imgFile);
        outState.putParcelable(Defines.STATUS_BEAN, status);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            Log.e(TAG, "load buddle");
            imgFile = (File) savedInstanceState.getSerializable(Defines.PHOTO_FILE);
            status = savedInstanceState.getParcelable(Defines.STATUS_BEAN);
            if (status != null) {
                weiboID = status.getId();
                setStatus();
                Log.e(TAG, "status not null: " + status.getText());
            }
        }

        act.invalidateOptionsMenu();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.e(TAG, "FRAGMENT ON ACTIVITY CREATED");
        super.onActivityCreated(savedInstanceState);
        act = (SingleWeiboActivity) getActivity();
        i = act.getActIntent();

        if (i.getStringExtra(WEIBO_ID) != null) {
            weiboID = i.getStringExtra(WEIBO_ID);
            MyMaidActionHelper.statusesShow(weiboID, mHandler);
            setViewHide();
        } else if (i.getParcelableExtra(STATUS_BEAN) != null) {
            Log.e(TAG, "get status from intent");
            status = i.getParcelableExtra(STATUS_BEAN);
            weiboID = status.getId();
            setStatus();
        }

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
        if (dGifp != null) {
            dGifp.cancel(true);
        }
    }

    private void setStatus() {
        act.setCommentsCount(status.getCommentsCount());
        act.setRepostsCount(status.getRepostsCount());

        act.invalidateOptionsMenu();

        setBasics();
        setRetweetedStatus();
        setImage();
        setLongClickCopyText();
    }

    private void setBasics() {
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

        if (status.getBmiddlePic() != null && status.getBmiddlePic().endsWith(".gif")) {
            // 原创动图

            tv_load_gif.setVisibility(View.VISIBLE);
            tv_load_gif.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dGifp != null) {
                        dGifp.cancel(true);
                    }
                    dGifp = new DownloadGIFPhoto(tv_rt_rl, SingleWeiboFragment.this);
                    dGifp.execute(status.getBmiddlePic());
                }
            });

        } else if (status.getRetweetedStatus() != null && status.getRetweetedStatus().getBmiddlePic() != null && status.getRetweetedStatus().getBmiddlePic().endsWith(".gif")) {
            // 转发动图

            setFullProgress();

            tv_load_gif.setVisibility(View.VISIBLE);
            tv_load_gif.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dGifp != null) {
                        dGifp.cancel(true);
                    }
                    dGifp = new DownloadGIFPhoto(tv_rt_rl, SingleWeiboFragment.this);
                    dGifp.execute(status.getRetweetedStatus().getBmiddlePic());
                }
            });
        } else if (status.getPicURLs() != null && status.getPicURLs().size() > 1) {
            // 原创多图微博

            muiltPhotosAdapter = new MuiltPhotosAdapter(act, status.getPicURLs(), sv);
            gv.setAdapter(muiltPhotosAdapter);

            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (dsp != null) {
                        dsp.cancel(true);
                    }
                    dsp = new DownloadSinglePhoto(tv_rt_rl, SingleWeiboFragment.this);
                    dsp.execute(status.getPicURLs().get(position).getBmiddlePic());
                }
            });

        } else if (status.getRetweetedStatus() != null && status.getRetweetedStatus().getPicURLs() != null && status.getRetweetedStatus().getPicURLs().size() > 1) {
            // 转发多图微博

            setFullProgress();

            muiltPhotosAdapter = new MuiltPhotosAdapter(act, status.getRetweetedStatus().getPicURLs(), sv);
            gv.setAdapter(muiltPhotosAdapter);

            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (dsp != null) {
                        dsp.cancel(true);
                    }
                    dsp = new DownloadSinglePhoto(tv_rt_rl, SingleWeiboFragment.this);
                    dsp.execute(status.getRetweetedStatus().getPicURLs().get(position).getBmiddlePic());
                }
            });


        } else if (status.getBmiddlePic() != null) {
            // 原创有图微博

            dp = new DownloadPhoto(iv_image, tv_rt_rl, false, SingleWeiboFragment.this);
            dp.execute(status.getBmiddlePic());

            iv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpToPhoto(imgFile, false);
                }
            });

        } else if (status.getRetweetedStatus() != null && status.getRetweetedStatus().getBmiddlePic() != null) {
            // 转发有图微博

            dp = new DownloadPhoto(iv_image, tv_rt_rl, true, SingleWeiboFragment.this);
            dp.execute(status.getRetweetedStatus().getBmiddlePic());

            iv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpToPhoto(imgFile, false);
                }
            });

        } else {
            // 什么都木有

            if (status.getRetweetedStatus() != null && status.getRetweetedStatus().getBmiddlePic() == null) {
                setFullProgress();
            }
        }
    }

    private void setFullProgress() {
        ViewGroup.LayoutParams lp = tv_rt_rl.getLayoutParams();
        lp.width = 10000;
        tv_rt_rl.setLayoutParams(lp);
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

    private void findViews() {
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
        tv_load_gif = (TextView) v.findViewById(R.id.tv_frag_single_weibo_load_gif);
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

    public StatusesBean getStatus() {
        return status;
    }

    public String getWeiboID() {
        return weiboID;
    }

    public File getImageFile() {
        return imgFile;
    }

    public void setImageFile(File file) {
        imgFile = file;
    }

    public void jumpToPhoto(File file, boolean isGIF) {
        Intent ii = new Intent(act, Photo.class);
        ii.putExtra(Defines.PHOTO_FILE, file);
        if (isGIF)
            ii.putExtra(Defines.IS_GIF, true);
        act.startActivity(ii);
        act.overridePendingTransition(R.anim.in, R.anim.alpha_out);
    }

    public void setGIFSize(double size) {

        if (tv_load_gif.getText() != null && tv_load_gif.getText().toString().indexOf("(") > 0)
            return;

        StringBuilder sb = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#.0");

        if (size < 1024)
            sb.append(size).append(" B");
        else if (size < 1024 * 1024)
            sb.append(df.format(size / 1024)).append(" KB");
        else
            sb.append(df.format(size / (1024 * 1024))).append(" MB");

        tv_load_gif.setText(tv_load_gif.getText() + "\n(" + sb.toString() + ")");
    }

}
