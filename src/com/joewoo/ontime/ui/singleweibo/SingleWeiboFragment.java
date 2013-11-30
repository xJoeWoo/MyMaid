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
import android.widget.ImageView;
import android.widget.TextView;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.net.DownloadPic;
import com.joewoo.ontime.support.menu.CopyTextContextualMenu;
import com.joewoo.ontime.support.net.DownloadUserProfileImage;
import com.joewoo.ontime.support.util.CheckMentionsURLTopic;
import com.joewoo.ontime.ui.SingleUser;

import java.io.File;
import java.util.HashMap;

import static com.joewoo.ontime.support.info.Defines.BMIDDLE_PIC;
import static com.joewoo.ontime.support.info.Defines.CREATED_AT;
import static com.joewoo.ontime.support.info.Defines.IS_COMMENT;
import static com.joewoo.ontime.support.info.Defines.IS_REPOST;
import static com.joewoo.ontime.support.info.Defines.PROFILE_IMAGE_URL;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_BMIDDLE_PIC;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_CREATED_AT;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_SOURCE;
import static com.joewoo.ontime.support.info.Defines.SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.SOURCE;
import static com.joewoo.ontime.support.info.Defines.STATUS_BMIDDLE_PIC;
import static com.joewoo.ontime.support.info.Defines.STATUS_CREATED_AT;
import static com.joewoo.ontime.support.info.Defines.STATUS_PROFILE_IMAGE_URL;
import static com.joewoo.ontime.support.info.Defines.STATUS_SOURCE;
import static com.joewoo.ontime.support.info.Defines.STATUS_TEXT;
import static com.joewoo.ontime.support.info.Defines.STATUS_USER_SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.TEMP_IMAGE_NAME;
import static com.joewoo.ontime.support.info.Defines.TEMP_IMAGE_PATH;
import static com.joewoo.ontime.support.info.Defines.TEXT;

public class SingleWeiboFragment extends Fragment {

    private HashMap<String, String> map;

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
    private ImageView iv_rt_image;
    private ImageView iv_profile_image;

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
    public void onDestroy() {
        super.onDestroy();
        if (dp != null && dp.getStatus() == AsyncTask.Status.RUNNING) {
            dp.cancel(true);
        }


//        if(iv_image.getDrawable() != null)
//            ((BitmapDrawable)iv_image.getDrawable()).getBitmap().recycle();
//
//        if(iv_rt_image.getDrawable() != null)
//            ((BitmapDrawable)iv_rt_image.getDrawable()).getBitmap().recycle();

        Log.e(TAG, "OnDestroy");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        act = (SingleWeiboActivity) getActivity();

        map = act.getSingleWeiboMap();

        if (map.get(IS_COMMENT) == null) {
            Log.e(TAG, "Normal Single Weibo");
            setNormalSingleWeibo();
        } else {
            Log.e(TAG, "Comment Single Weibo");
            setCommentsToMeSingleWeibo();
        }

        setRetweetedStatus();

        setImage();

        setLongClickCopyText();
    }


    private void setCommentsToMeSingleWeibo() {

        tv_screen_name.setText(map.get(STATUS_USER_SCREEN_NAME));
        tv_created_at.setText(" · " + map.get(STATUS_CREATED_AT));

        tv_text.setText(CheckMentionsURLTopic.getSpannableString(map.get(STATUS_TEXT), act));
        tv_text.setMovementMethod(LinkMovementMethod.getInstance());

        tv_source.setText(map.get(STATUS_SOURCE));

        if (map.get(STATUS_BMIDDLE_PIC) == null) {
            if (map.get(RETWEETED_STATUS_BMIDDLE_PIC) == null) {
                ViewGroup.LayoutParams lp = tv_rt_rl.getLayoutParams();
                lp.width = 10000;
                tv_rt_rl.setLayoutParams(lp);
            }
            iv_image.setVisibility(View.GONE);
        } else {

            dp = new DownloadPic(iv_image, tv_rt_rl, false, act);
            dp.execute(map.get(STATUS_BMIDDLE_PIC));

        }

        iv_profile_image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DownloadPic(iv_profile_image).execute(map
                        .get(STATUS_PROFILE_IMAGE_URL));
                iv_profile_image.setClickable(false);
            }
        });

        tv_screen_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToSingleUser(map.get(STATUS_USER_SCREEN_NAME));
            }
        });
        tv_created_at.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToSingleUser(map.get(STATUS_USER_SCREEN_NAME));
            }
        });
        tv_source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToSingleUser(map.get(STATUS_USER_SCREEN_NAME));
            }
        });

    }

    private void setNormalSingleWeibo() {

        tv_screen_name.setText(map.get(SCREEN_NAME));
        tv_created_at.setText(" · " + map.get(CREATED_AT));

        tv_text.setText(CheckMentionsURLTopic.getSpannableString(map.get(TEXT), act));
        tv_text.setMovementMethod(LinkMovementMethod.getInstance());

        tv_source.setText(map.get(SOURCE));

        if (map.get(BMIDDLE_PIC) == null) {
            if (map.get(RETWEETED_STATUS_BMIDDLE_PIC) == null) {
                ViewGroup.LayoutParams lp = tv_rt_rl.getLayoutParams();
                lp.width = 10000;
                tv_rt_rl.setLayoutParams(lp);
            }
            iv_image.setVisibility(View.GONE);
        } else {

            dp = new DownloadPic(iv_image, tv_rt_rl, false, act);
            dp.execute(map.get(BMIDDLE_PIC));

        }

        iv_profile_image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DownloadUserProfileImage(iv_profile_image).execute(map
                        .get(PROFILE_IMAGE_URL));
                iv_profile_image.setClickable(false);
            }
        });

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

    private void setRetweetedStatus() {
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
                new DownloadPic(iv_rt_image, tv_rt_rl, true, act).execute(map
                        .get(RETWEETED_STATUS_BMIDDLE_PIC));

            }
            tv_rt_screen_name.setText(map
                    .get(RETWEETED_STATUS_SCREEN_NAME));
            tv_rt_created_at.setText(" · " + map
                    .get(RETWEETED_STATUS_CREATED_AT));

            tv_rt_text.setText(CheckMentionsURLTopic.getSpannableString(map.get(RETWEETED_STATUS), act));
            tv_rt_text.setMovementMethod(LinkMovementMethod.getInstance());

            tv_rt_source.setText(map.get(RETWEETED_STATUS_SOURCE));


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

    private void setImage() {
        if (iv_rt_image.getVisibility() == View.VISIBLE) {
            iv_rt_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpToGallery();
                }
            });
        }
        if (iv_image.getVisibility() == View.VISIBLE) {
            iv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpToGallery();
                }
            });
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

    void jumpToGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(TEMP_IMAGE_PATH, TEMP_IMAGE_NAME)), "image/*");
        startActivity(intent);
    }
    //    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//
//        menu.clear();
//
//    }

}
