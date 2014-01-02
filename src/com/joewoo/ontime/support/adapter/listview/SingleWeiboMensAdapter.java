package com.joewoo.ontime.support.adapter.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.bean.CommentsBean;
import com.joewoo.ontime.support.bean.StatusesBean;

import java.util.List;

/**
 * Created by JoeWoo on 13-12-21.
 */
public class SingleWeiboMensAdapter extends BaseAdapter {

    private Context context;
    private List<StatusesBean> statuses;

    public SingleWeiboMensAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<StatusesBean> statuses) {
        this.statuses = statuses;
    }

    @Override
    public int getCount() {
        return statuses.size();
    }

    @Override
    public Object getItem(int position) {
        return statuses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.comments_repsots_show_lv,
                    null);

            holder.tv_scr_name = (TextView) convertView
                    .findViewById(R.id.comments_show_screen_name);

            holder.tv_text = (TextView) convertView
                    .findViewById(R.id.comments_show_text);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.tv_scr_name.setText(statuses.get(position).getUser().getScreenName());

        holder.tv_text.setText(statuses.get(position).getText());

        return convertView;
    }
}
