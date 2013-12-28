package com.joewoo.ontime.support.adapter.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.bean.CommentsBean;

import java.util.List;

public class CommentsToMeAdapter extends BaseAdapter {

    private List<CommentsBean> data;
    private Context context;

    public CommentsToMeAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<CommentsBean> data) {
        this.data = data;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Log.e(TAG, "getView");

        ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.comments_to_me_lv,
                    null);

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


        CommentsBean c = data.get(position);

        holder.tv_scr_name.setText(c.getUser().getScreenName());

        holder.tv_text.setText(c.getText());

        if (c.getReplyComment() != null) {
            holder.tv_st.setText(c.getReplyComment().getText());

            holder.tv_st_scr_name.setText(c.getReplyComment().getUser().getScreenName());
        } else {
            holder.tv_st.setText(c.getStatus().getText());

            holder.tv_st_scr_name.setText(c.getStatus().getUser().getScreenName());
        }

        String source = c.getSource();

        holder.tv_source.setText(" · " + source);

        if (source.equals(context.getString(R.string.app_name_cn))) {
            holder.tv_source.setTextColor(context.getResources().getColor(R.color.textGrey));
            holder.tv_source.setShadowLayer(20, 0, 0, context.getResources().getColor(R.color.sourcePink));
        } else {
            holder.tv_source.setShadowLayer(0, 0, 0, 0);
        }

        holder.tv_crt_at.setText(c.getCreatedAt());

//        }

        return convertView;
    }
}
