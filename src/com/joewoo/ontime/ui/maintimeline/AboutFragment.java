package com.joewoo.ontime.ui.maintimeline;


import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.view.MyMaidSettingView;

/**
 * Created by JoeWoo on 14-1-12.
 */
public class AboutFragment extends Fragment {

    private MainTimelineActivity act;
    private MyMaidSettingView settingsView;
    private MyMaidSettingView aboutView;
    private TextView tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frag_settings, null);

        settingsView = (MyMaidSettingView) v.findViewById(R.id.frag_setting_2);
        aboutView = (MyMaidSettingView) v.findViewById(R.id.frag_setting_3);

        tv = (TextView) v.findViewById(R.id.frag_setting_tv_app_name);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        act = (MainTimelineActivity) getActivity();

        tv.setTypeface(Typeface.createFromAsset(act.getAssets(), "fonts/Roboto-ThinItalic.ttf"));

        settingsView.setMainImg(R.drawable.ic_settings).setName(R.string.title_settings);

        aboutView.setMainImg(R.drawable.ic_about).setName(R.string.title_about);

        settingsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(act, SettingsActivity.class));
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

    }
}
