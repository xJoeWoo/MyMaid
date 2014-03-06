package com.joewoo.ontime.ui.maintimeline;


import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.joewoo.ontime.R;
import com.joewoo.ontime.action.update.CheckUpdate;
import com.joewoo.ontime.support.dialog.WeatherDialog;
import com.joewoo.ontime.support.info.Defines;
import com.joewoo.ontime.support.notification.MyMaidNotificationHelper;
import com.joewoo.ontime.support.setting.MyMaidSettingsHelper;
import com.joewoo.ontime.support.util.GlobalContext;
import com.joewoo.ontime.support.view.MyMaidSettingView;
import com.joewoo.ontime.ui.UpdataActivity;

/**
 * Created by JoeWoo on 14-1-12.
 */
public class AboutFragment extends Fragment {

    private MainTimelineActivity act;
    private MyMaidSettingView weatherView;
    private MyMaidSettingView aboutView;
    private TextView tv;
    private TextView tv_ver;
    private TextView tv_update_hint;
    private View v;
    private MyMaidNotificationHelper mNotification;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Defines.GOT_APP_VERSION_INFO && msg.obj != null) {
                String newVer = (String) msg.obj;
                if (compareVersion(newVer)) {
                    hasLatestVersion(newVer);
                    Toast.makeText(act, R.string.toast_pull_right_to_update, Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.frag_setting, null);

        weatherView = (MyMaidSettingView) v.findViewById(R.id.frag_setting_2);
        aboutView = (MyMaidSettingView) v.findViewById(R.id.frag_setting_3);

        tv = (TextView) v.findViewById(R.id.frag_setting_tv_app_name);
        tv_ver = (TextView) v.findViewById(R.id.frag_setting_tv_app_version);
        tv_update_hint = (TextView) v.findViewById(R.id.tv_frag_setting_update_hint);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        act = (MainTimelineActivity) getActivity();

        Typeface tf = Typeface.createFromAsset(act.getAssets(), "fonts/Roboto-ThinItalic.ttf");

        tv.setTypeface(tf);
        tv_ver.setTypeface(tf);
        try {
            tv_ver.setText(GlobalContext.getVersionName().substring(0, 4));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Typeface lightTf = Typeface.createFromAsset(act.getAssets(), "fonts/Roboto-Light.ttf");

        tv_update_hint.setTypeface(lightTf);

        weatherView.setMainImg(R.drawable.ic_weather).setName(R.string.title_weather, lightTf);
        aboutView.setMainImg(R.drawable.ic_about).setName(R.string.title_about, lightTf);

        weatherView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeatherDialog.show(false, act);
            }
        });
        aboutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(act);
                builder.setTitle(R.string.dialog_about_title);
                builder.setMessage(R.string.dialog_about_message);
                builder.setNegativeButton(R.string.dialog_about_cancle, null);
                builder.show();
            }
        });


        if (System.currentTimeMillis() - MyMaidSettingsHelper.getLong(MyMaidSettingsHelper.CHECK_UPDATE_TIME) > 60 * 60 * 24 * 1000) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    new CheckUpdate(handler).start();
                }
            }, Defines.CHECK_APP_VERSION_DELAY);
        } else {
            String newVer = MyMaidSettingsHelper.getString(MyMaidSettingsHelper.NEW_VERSION);
            if (newVer != null && !newVer.equals("") && compareVersion(newVer)) {
                hasLatestVersion(newVer);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNotification.setRemove();
    }

    private void hasLatestVersion(final String newVer) {

        Log.e(Defines.TAG, "Old Ver: " + GlobalContext.getVersionName());
        Log.e(Defines.TAG, "New Ver: " + newVer.substring(0, 11));

        if (!MyMaidSettingsHelper.getBoolean(MyMaidSettingsHelper.UPDATED))
            tv_update_hint.setVisibility(View.VISIBLE);


        v.findViewById(R.id.frag_setting_update_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                UpdataActivity.show(newVer, act);
                startActivity(new Intent(act, UpdataActivity.class));
            }
        });

        tv_ver.setText(newVer.substring(0, 4) + "\n" + "NEW!");
        tv_ver.setTextColor(act.getResources().getColor(R.color.pinkHighlightSpan));
        Spannable ssb = new SpannableString("MyMaid");
        // Holo Green Light
        ssb.setSpan(new ForegroundColorSpan(0xff99cc00), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // Holo Orange Light
        ssb.setSpan(new ForegroundColorSpan(0xffffbb33), 2, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(ssb);

        mNotification = new MyMaidNotificationHelper(MyMaidNotificationHelper.APP_UPDATE, null, act);
        mNotification.setUpdate();
    }

    private boolean compareVersion(String newVer) {
        return !newVer.substring(0, 11).equals(GlobalContext.getVersionName());
    }
}
