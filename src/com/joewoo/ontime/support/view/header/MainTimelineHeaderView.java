package com.joewoo.ontime.support.view.header;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joewoo.ontime.R;

/**
 * Created by JoeWoo on 13-12-15.
 */
public class MainTimelineHeaderView extends RelativeLayout {

    private RelativeLayout rl;

    public MainTimelineHeaderView(Context context) {
        super(context);

        View v = LayoutInflater.from(context).inflate(R.layout.main_timeline_header, null);

        addView(v);

        rl = (RelativeLayout) v.findViewById(R.id.main_timeline_header_rl);
    }

    public void hide() {
        rl.setVisibility(GONE);
    }

    public void show() {
        rl.setVisibility(VISIBLE);
    }
}
