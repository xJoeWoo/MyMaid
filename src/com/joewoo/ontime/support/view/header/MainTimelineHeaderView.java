package com.joewoo.ontime.support.view.header;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.joewoo.ontime.R;

/**
 * Created by Joe on 14-1-22.
 */
public class MainTimelineHeaderView extends RelativeLayout{

    private RelativeLayout rl;

    public MainTimelineHeaderView(Context context) {
        super(context);

        View v = LayoutInflater.from(context).inflate(R.layout.lv_header_main_timeline, null);

        addView(v);

        rl = (RelativeLayout) v.findViewById(R.id.main_timeline_header_rl);
    }


    public void hide() {
        rl.setVisibility(GONE);
    }

    public void show() {
        rl.setVisibility(VISIBLE);
    }

    public void setAlpha(float alpha) {
        rl.setAlpha(alpha);
    }
}
