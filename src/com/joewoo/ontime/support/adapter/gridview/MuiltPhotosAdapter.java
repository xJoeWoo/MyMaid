package com.joewoo.ontime.support.adapter.gridview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.bean.PicURLsBean;
import com.joewoo.ontime.support.image.DownloadSinglePhotoInGridView;
import com.joewoo.ontime.support.info.Defines;

import java.util.HashSet;
import java.util.List;

/**
 * Created by JoeWoo on 13-12-26.
 */
public class MuiltPhotosAdapter extends BaseAdapter {

    private Context context;
    private List<PicURLsBean> pics;
    private HashSet<DownloadSinglePhotoInGridView> tasksHashSet;
    private HashSet<Integer> positionCompare;
    private ScrollView sv;

    public MuiltPhotosAdapter(Context context, List<PicURLsBean> pics, ScrollView sv) {
        this.context = context;
        this.pics = pics;
        tasksHashSet = new HashSet<>();
        positionCompare = new HashSet<>();
        this.sv = sv;
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

        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.gv_single_weibo_muilt_photos,
                    null);
        }

        ImageView iv = (ImageView) convertView.findViewById(R.id.frag_single_weibo_grid_view_img);
        loadBitmap(pics.get(position).getSquarePic(), iv, position);

        return convertView;
    }

    private void loadBitmap(String imageUrl, ImageView iv, int position) {
        if (!positionCompare.contains(position)) {
            DownloadSinglePhotoInGridView downloadSinglePhotoInGridView = new DownloadSinglePhotoInGridView(iv, tasksHashSet);
            tasksHashSet.add(downloadSinglePhotoInGridView);
            downloadSinglePhotoInGridView.execute(imageUrl);
            positionCompare.add(position);
        }
    }

    public void cancelAllTasks() {
        for (DownloadSinglePhotoInGridView d : tasksHashSet) {
            d.cancel(true);
        }
    }
}
