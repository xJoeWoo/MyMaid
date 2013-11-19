package com.joewoo.ontime.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joewoo.ontime.R;

import java.util.ArrayList;
import java.util.HashMap;

import static com.joewoo.ontime.info.Constants.BLANK;
import static com.joewoo.ontime.info.Constants.CREATED_AT;
import static com.joewoo.ontime.info.Constants.REPLY_COMMNET_TEXT;
import static com.joewoo.ontime.info.Constants.REPLY_COMMNET_USER_SCREEN_NAME;
import static com.joewoo.ontime.info.Constants.SCREEN_NAME;
import static com.joewoo.ontime.info.Constants.SOURCE;
import static com.joewoo.ontime.info.Constants.TEXT;

public class MyMaidCommentsToMeAdapter extends BaseAdapter {

    private ArrayList<HashMap<String, String>> data;
    private LayoutInflater mInflater;
    private Context context;

    public MyMaidCommentsToMeAdapter(Context context,
                                     ArrayList<HashMap<String, String>> data) {
        this.data = data;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
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
        public TextView tv_st_rl;
        public TextView tv_st_scr_name;
        public TextView tv_st;
        public TextView tv_source;
        public TextView tv_crt_at;
        public TextView tv_blank;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Log.e(TAG, "getView");

        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.comments_to_me_lv,
                    null);

            // Find views
            holder.tv_scr_name = (TextView) convertView
                    .findViewById(R.id.comments_to_me_screen_name);

            holder.tv_text = (TextView) convertView
                    .findViewById(R.id.comments_to_me_text);

            holder.tv_st_scr_name = (TextView) convertView
                    .findViewById(R.id.comments_to_me_status_screen_name);

            holder.tv_st = (TextView) convertView
                    .findViewById(R.id.comments_to_me_status);

            holder.tv_st_rl = (TextView) convertView.findViewById(R.id.comments_to_me_status_rl);

            holder.tv_source = (TextView) convertView
                    .findViewById(R.id.comments_to_me_source);

            holder.tv_crt_at = (TextView) convertView
                    .findViewById(R.id.comments_to_me_created_at);

            holder.tv_blank = (TextView) convertView.findViewById(R.id.comments_to_me_blank);

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
            holder.tv_source.setVisibility(View.GONE);
            holder.tv_crt_at.setVisibility(View.GONE);
            holder.tv_st.setVisibility(View.GONE);
            holder.tv_st_scr_name.setVisibility(View.GONE);
            holder.tv_st_rl.setVisibility(View.GONE);
        } else {
            holder.tv_blank.setVisibility(View.GONE);
            holder.tv_scr_name.setVisibility(View.VISIBLE);
            holder.tv_text.setVisibility(View.VISIBLE);
            holder.tv_source.setVisibility(View.VISIBLE);
            holder.tv_crt_at.setVisibility(View.VISIBLE);
            holder.tv_st.setVisibility(View.VISIBLE);
            holder.tv_st_scr_name.setVisibility(View.VISIBLE);
            holder.tv_st_rl.setVisibility(View.VISIBLE);

            holder.tv_scr_name.setText(data.get(position).get(SCREEN_NAME));

            holder.tv_text.setText(data.get(position).get(TEXT));

            holder.tv_st.setText(data.get(position).get(REPLY_COMMNET_TEXT));

            holder.tv_st_scr_name.setText(data.get(position).get(REPLY_COMMNET_USER_SCREEN_NAME));

            holder.tv_source.setText(data.get(position).get(SOURCE));

            //......Remember there's a " · " front of source
            if (data.get(position).get(SOURCE).equals(" · " + context.getString(R.string.app_name_cn))) {
                holder.tv_source.setTextColor(context.getResources().getColor(R.color.textGrey));
                holder.tv_source.setShadowLayer(20, 0, 0, context.getResources().getColor(R.color.sourcePink));
            } else {
                holder.tv_source.setShadowLayer(0, 0, 0, 0);
            }

            holder.tv_crt_at.setText(data.get(position).get(CREATED_AT));

        }



        return convertView;
    }
}
