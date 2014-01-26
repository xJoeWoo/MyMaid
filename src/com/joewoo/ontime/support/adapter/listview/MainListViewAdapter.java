package com.joewoo.ontime.support.adapter.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.bean.StatusesBean;

import java.util.List;

public class MainListViewAdapter extends BaseAdapter {

    private List<StatusesBean> statuses;
    private Context context;

    public MainListViewAdapter(Context context) {
        this.context = context;

    }

    public void setData(List<StatusesBean> statuses) {
        this.statuses = statuses;
    }

    @Override
    public Object getItem(int position) {
        return statuses.get(position);
    }

    @Override
    public int getCount() {
        if(statuses != null)
            return statuses.size();
        else
            return 0;
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

            convertView = LayoutInflater.from(context).inflate(R.layout.lv_main_timeline, null);

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

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }



        StatusesBean s = statuses.get(position);

        if (s.getPicURLs().size() == 1) {
            holder.tv_img.setVisibility(View.VISIBLE);
            holder.tv_img.setBackgroundResource(R.drawable.image_dark);
        } else if (s.getPicURLs().size() > 1) {
            holder.tv_img.setVisibility(View.VISIBLE);
            holder.tv_img.setBackgroundResource(R.drawable.muilt_image);
        } else
            holder.tv_img.setVisibility(View.GONE);

        holder.tv_scr_name.setText(s.getUser().getScreenName());

//        holder.tv_text.setText(CheckMentionsURLTopic.getSpannableString(s.getText(), context));
        holder.tv_text.setText(s.getText());

        if (s.getRetweetedStatus() == null) {

            holder.tv_rt_rl.setVisibility(View.GONE);
            holder.tv_rt_scr_name.setVisibility(View.GONE);
            holder.tv_rt.setVisibility(View.GONE);
        } else {
            holder.tv_rt_scr_name.setVisibility(View.VISIBLE);
            holder.tv_rt.setVisibility(View.VISIBLE);
            holder.tv_rt_rl.setVisibility(View.VISIBLE);
//            holder.tv_rt.setText(CheckMentionsURLTopic.getSpannableString(s.getRetweetedStatus().getText(), context));
            holder.tv_rt.setText(s.getRetweetedStatus().getText());
            if (s.getRetweetedStatus().getUser() != null) { // 微博已被删除
                holder.tv_rt_scr_name.setText(s.getRetweetedStatus().getUser().getScreenName());
                if (s.getRetweetedStatus().getPicURLs().size() == 1) {
                    holder.tv_img.setVisibility(View.VISIBLE);
                    holder.tv_img.setBackgroundResource(R.drawable.image_dark);
                } else if (s.getRetweetedStatus().getPicURLs().size() > 1) {
                    holder.tv_img.setVisibility(View.VISIBLE);
                    holder.tv_img.setBackgroundResource(R.drawable.muilt_image);
                } else
                    holder.tv_img.setVisibility(View.GONE);
            } else {
                holder.tv_rt_scr_name.setText("……");
            }

        }

        String source = s.getSource();
        holder.tv_source.setText(" · " + source);
        if (source.equals(context.getString(R.string.app_name_cn))) {
            holder.tv_source.setTextColor(context.getResources().getColor(R.color.greyText));
            holder.tv_source.setShadowLayer(20, 0, 0, context.getResources().getColor(R.color.pinkSource));
        } else {
            holder.tv_source.setShadowLayer(0, 0, 0, 0);
        }

        holder.tv_crt_at.setText(s.getCreatedAt());

        holder.tv_cmt_cnt.setText(String.valueOf(s.getCommentsCount()));

        holder.tv_rpos_cnt.setText(String.valueOf(s.getRepostsCount()));

        return convertView;
    }
}
