package com.joewoo.ontime.support.adapter.gridview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.adapter.listview.ViewHolder;
import com.joewoo.ontime.support.bean.PicURLsBean;
import com.joewoo.ontime.support.net.DownloadMuiltPic;

import static com.joewoo.ontime.support.info.Defines.TAG;

import java.util.List;

/**
 * Created by JoeWoo on 13-12-26.
 */
public class SingleWeiboGirdViewAdapter extends BaseAdapter {

    private Context context;
    private List<PicURLsBean> pics;

    int count;

    public SingleWeiboGirdViewAdapter(Context context, List<PicURLsBean> pics) {
        this.context = context;
        this.pics = pics;
    }

    @Override
    public int getCount() {
        return pics.size();
    }

    @Override
    public Object getItem(int position) {
        return pics.get(position);
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

            convertView = LayoutInflater.from(context).inflate(R.layout.single_weibo_grid_view,
                    null);
            holder.iv = (ImageView) convertView.findViewById(R.id.frag_single_weibo_grid_view_img);

            String url = pics.get(position).getSquarePic();

            new DownloadMuiltPic(holder.iv).execute(url);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        count++;
        Log.e(TAG, String.valueOf(count));

        return convertView;
    }
}
