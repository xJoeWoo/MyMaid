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

/**
 * Created by JoeWoo on 13-12-18.
 */
public class CommentsMentionsListViewAdapter extends BaseAdapter {

    private List<CommentsBean> comments;
    private Context context;

    public CommentsMentionsListViewAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<CommentsBean> comments) {
        this.comments = comments;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
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
//        public TextView tv_blank;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {

                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.friendstimeline_lv,
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

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            CommentsBean c = comments.get(position);

            if (c.getStatus().getBmiddlePic() != null
                    || c.getStatus().getRetweetedStatus().getThumbnailPic() != null) {
                if (c.getStatus().getPicURLs() == null)
                    holder.tv_img.setBackgroundResource(R.drawable.image_dark);
                else
                    holder.tv_img.setBackgroundResource(R.drawable.muilt_image);
            } else
                holder.tv_img.setVisibility(View.GONE);

            holder.tv_scr_name.setText(c.getUser().getScreenName());

            holder.tv_text.setText(c.getText());

            if (c.getStatus().getRetweetedStatus() == null) {
                holder.tv_rt_rl.setVisibility(View.GONE);
                holder.tv_rt_scr_name.setVisibility(View.GONE);
                holder.tv_rt.setVisibility(View.GONE);
            } else {
                holder.tv_rt_scr_name.setText(c.getStatus().getRetweetedStatus().getUser().getScreenName());
                holder.tv_rt.setText(c.getStatus().getRetweetedStatus().getText());
            }

            String source = c.getSource();
            holder.tv_source.setText(" · " + source);
            if (source.equals(context.getString(R.string.app_name_cn))) {
                holder.tv_source.setTextColor(context.getResources().getColor(R.color.textGrey));
                holder.tv_source.setShadowLayer(20, 0, 0, context.getResources().getColor(R.color.sourcePink));
            } else {
                holder.tv_source.setShadowLayer(0, 0, 0, 0);
            }

            holder.tv_crt_at.setText(c.getStatus().getCreatedAt());

            holder.tv_cmt_cnt.setText(String.valueOf(c.getStatus().getCommentsCount()));

            holder.tv_rpos_cnt.setText(String.valueOf(c.getStatus().getRepostsCount()));

            holder.tv_crt_at.setText(c.getCreatedAt());

            return convertView;

    }
}
