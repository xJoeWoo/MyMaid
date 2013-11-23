package com.joewoo.ontime.support.adapter.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joewoo.ontime.R;

import java.util.ArrayList;
import java.util.HashMap;

import static com.joewoo.ontime.support.info.Defines.BLANK;
import static com.joewoo.ontime.support.info.Defines.COMMENTS_COUNT;
import static com.joewoo.ontime.support.info.Defines.CREATED_AT;
import static com.joewoo.ontime.support.info.Defines.IS_REPOST;
import static com.joewoo.ontime.support.info.Defines.REPOSTS_COUNT;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.RETWEETED_STATUS_THUMBNAIL_PIC;
import static com.joewoo.ontime.support.info.Defines.SCREEN_NAME;
import static com.joewoo.ontime.support.info.Defines.SOURCE;
import static com.joewoo.ontime.support.info.Defines.TEXT;
import static com.joewoo.ontime.support.info.Defines.THUMBNAIL_PIC;

public class MyMaidAdapter extends BaseAdapter {

    private ArrayList<HashMap<String, String>> data;
    private LayoutInflater mInflater;
    private Context context;

    public MyMaidAdapter(ArrayList<HashMap<String, String>> data, Context context) {
        this.context = context;
        this.data = data;
        this.mInflater = LayoutInflater.from(context);
    }

    public void addItem(ArrayList<HashMap<String, String>> toAdd) {
        this.data = toAdd;
    }

    public void changeItem(ArrayList<HashMap<String, String>> toChange) {
        this.data = toChange;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        public TextView tv_scr_name;
        public TextView tv_text;
        public TextView tv_rt_rl;
        public TextView tv_rt_scr_name;
        public TextView tv_rt;
        public TextView tv_source;
        public TextView tv_crt_at;
        public TextView tv_cmt_cnt;
        public TextView tv_rpos_cnt;
        public TextView tv_img;
        public TextView tv_blank;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Log.e(TAG, "getView");

        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.friendstimeline_lv,
                    null);

            // Find views
            holder.tv_scr_name = (TextView) convertView
                    .findViewById(R.id.friendstimeline_screen_name);

            holder.tv_text = (TextView) convertView
                    .findViewById(R.id.friendstimeline_text);

            holder.tv_rt_rl = (TextView) convertView
                    .findViewById(R.id.friendstimeline_retweeted_status_rl);

            holder.tv_rt_scr_name = (TextView) convertView
                    .findViewById(R.id.friendstimeline_retweeted_status_screen_name);

            holder.tv_rt = (TextView) convertView
                    .findViewById(R.id.friendstimeline_retweeted_status);

            holder.tv_source = (TextView) convertView
                    .findViewById(R.id.friendstimeline_source);

            holder.tv_crt_at = (TextView) convertView
                    .findViewById(R.id.friendstimeline_created_at);

            holder.tv_cmt_cnt = (TextView) convertView
                    .findViewById(R.id.friendstimeline_comments_count);

            holder.tv_rpos_cnt = (TextView) convertView
                    .findViewById(R.id.friendstimeline_reposts_count);

            holder.tv_img = (TextView) convertView
                    .findViewById(R.id.friendstimeline_have_image);

            holder.tv_blank = (TextView) convertView.findViewById(R.id.friendstimeline_blank);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Settings
        if(data.get(position).get(BLANK) != null)
        {
            holder.tv_blank.setVisibility(View.VISIBLE);
            holder.tv_scr_name.setVisibility(View.GONE);
            holder.tv_text.setVisibility(View.GONE);
            holder.tv_rt_rl.setVisibility(View.GONE);
            holder.tv_rt_scr_name.setVisibility(View.GONE);
            holder.tv_rt.setVisibility(View.GONE);
            holder.tv_source.setVisibility(View.GONE);
            holder.tv_crt_at.setVisibility(View.GONE);
            holder.tv_cmt_cnt.setVisibility(View.GONE);
            holder.tv_rpos_cnt.setVisibility(View.GONE);
            holder.tv_img.setVisibility(View.GONE);
        } else {
            holder.tv_blank.setVisibility(View.GONE);
            holder.tv_scr_name.setVisibility(View.VISIBLE);
            holder.tv_text.setVisibility(View.VISIBLE);
//            holder.tv_rt_rl.setVisibility(View.VISIBLE);
//            holder.tv_rt_scr_name.setVisibility(View.VISIBLE);
//            holder.tv_rt.setVisibility(View.VISIBLE);
            holder.tv_source.setVisibility(View.VISIBLE);
            holder.tv_crt_at.setVisibility(View.VISIBLE);
            holder.tv_cmt_cnt.setVisibility(View.VISIBLE);
            holder.tv_rpos_cnt.setVisibility(View.VISIBLE);
            holder.tv_img.setVisibility(View.VISIBLE);

            if (data.get(position).get(THUMBNAIL_PIC) != null
                    || data.get(position).get(RETWEETED_STATUS_THUMBNAIL_PIC) != null)
                holder.tv_img.setVisibility(View.VISIBLE);
            else
                holder.tv_img.setVisibility(View.GONE);

            holder.tv_scr_name.setText(data.get(position).get(SCREEN_NAME));

            holder.tv_text.setText(data.get(position).get(TEXT));

            if (data.get(position).get(IS_REPOST) != null)
                holder.tv_rt_rl.setVisibility(View.VISIBLE);
            else
                holder.tv_rt_rl.setVisibility(View.GONE);

            if (data.get(position).get(RETWEETED_STATUS_SCREEN_NAME) != null) {
                holder.tv_rt_scr_name.setVisibility(View.VISIBLE);
                holder.tv_rt_scr_name.setText(data.get(position).get(
                        RETWEETED_STATUS_SCREEN_NAME));
            } else {
                holder.tv_rt_scr_name.setVisibility(View.GONE);
            }

            if (data.get(position).get(RETWEETED_STATUS) != null) {
                holder.tv_rt.setVisibility(View.VISIBLE);

                holder.tv_rt.setText(data.get(position).get(RETWEETED_STATUS));
            } else
                holder.tv_rt.setVisibility(View.GONE);

            holder.tv_source.setText(data.get(position).get(SOURCE));

            //......Remember there's a " · " front of source
            if (data.get(position).get(SOURCE).equals(" · " + context.getString(R.string.app_name_cn))) {
                holder.tv_source.setTextColor(context.getResources().getColor(R.color.textGrey));
                holder.tv_source.setShadowLayer(20, 0, 0, context.getResources().getColor(R.color.sourcePink));
            } else {
                holder.tv_source.setShadowLayer(0, 0, 0, 0);
            }

            holder.tv_crt_at.setText(data.get(position).get(CREATED_AT));

            holder.tv_cmt_cnt.setText(data.get(position).get(COMMENTS_COUNT));

            holder.tv_rpos_cnt.setText(data.get(position).get(REPOSTS_COUNT));


        }

        return convertView;
    }
}
