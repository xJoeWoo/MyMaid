package com.joewoo.ontime.support.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joewoo.ontime.R;

/**
 * Created by JoeWoo on 14-1-12.
 */
public class MyMaidSettingView extends RelativeLayout{

    private Context context;

    private TextView tv_main;
    private TextView tv_name;
    private ImageView iv;

    public MyMaidSettingView(Context context) {
        super(context);

//        this.context = context;
//
//        View v = LayoutInflater.from(context).inflate(R.layout.mymaid_setting, null);
//
//        addView(v);
//
//        tv_main = (TextView) v.findViewById(R.id.setting_tv_main);
//        tv_name = (TextView) v.findViewById(R.id.setting_tv_name);
//        iv = (ImageView) v.findViewById(R.id.setting_iv);

    }

    public MyMaidSettingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.mymaid_setting, this);

//        View v = LayoutInflater.from(context).inflate(R.layout.mymaid_setting, null);

//        addView(v);

        tv_main = (TextView) findViewById(R.id.setting_tv_main);
        tv_name = (TextView) findViewById(R.id.setting_tv_name);
        iv = (ImageView) findViewById(R.id.setting_iv);
    }

    public MyMaidSettingView setMainText(String string) {
        tv_main.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-ThinItalic.ttf"));
        tv_main.setVisibility(VISIBLE);
        tv_main.setText(string);
        return this;
    }

    public MyMaidSettingView setMainImg(Bitmap bitmap) {
        iv.setVisibility(VISIBLE);
        iv.setImageBitmap(bitmap);
        return this;
    }

    public MyMaidSettingView setMainImg(int resID) {
        iv.setVisibility(VISIBLE);
        iv.setImageResource(resID);
        return this;
    }

    public MyMaidSettingView setName(String string) {
        tv_name.setText(string);
        return this;
    }

    public MyMaidSettingView setName(int resID) {
        tv_name.setText(resID);
        return this;
    }
}
