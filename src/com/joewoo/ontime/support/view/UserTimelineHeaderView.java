package com.joewoo.ontime.support.view;

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
public class UserTimelineHeaderView extends RelativeLayout {

    private ImageView iv;
    private RelativeLayout rl;
    private TextView tv_description;

    public UserTimelineHeaderView(Context context) {
        super(context);

        View v = LayoutInflater.from(context).inflate(R.layout.user_timeline_header, null);

        addView(v);

        iv = (ImageView) v.findViewById(R.id.user_timeline_header_iv);
        rl = (RelativeLayout) v.findViewById(R.id.user_timeline_header_container);
        tv_description = (TextView) v.findViewById(R.id.user_timeline_header_description);
    }

    public void setImageView(byte[] bytes) {
        iv.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
    }

    public void setDescription(String str) { tv_description.setText(str); }

    public void hide() {
        rl.setVisibility(GONE);
    }

    public void show() {
        rl.setVisibility(VISIBLE);
    }
}
