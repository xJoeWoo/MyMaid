package com.joewoo.ontime.support.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.util.GlobalContext;

import java.io.ByteArrayInputStream;
import java.util.HashSet;

import static com.joewoo.ontime.support.info.Defines.TAG;

/**
 * Created by JoeWoo on 13-12-26.
 */
public class DownloadSinglePhotoInGridView extends AsyncTask<String, Integer, Bitmap> {

    private ImageView iv;
    private Animation in;
    private HashSet<DownloadSinglePhotoInGridView> tasksHashSet;

    public DownloadSinglePhotoInGridView(ImageView iv, HashSet<DownloadSinglePhotoInGridView> tasksHashSet) {
        this.iv = iv;
        this.tasksHashSet = tasksHashSet;
    }

    @Override
    protected void onPreExecute() {
        in = AnimationUtils.loadAnimation(GlobalContext.getAppContext(), R.anim.in);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String imageUrl = params[0];

        Log.e(TAG, "Download Single Photo AsyncTask START");
        Log.e(TAG, "Pic URL - " + imageUrl);

        if (!imageUrl.endsWith(".gif")) {
            try {
                return BitmapFactory.decodeStream(new ByteArrayInputStream(new HttpUtility().executeDownloadImageTask(params[0], null)));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            iv.setImageBitmap(bitmap);
            iv.startAnimation(in);
        } else {
            iv.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        }
        tasksHashSet.remove(this);
    }
}
