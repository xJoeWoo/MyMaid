package com.joewoo.ontime.support.image;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.image.BitmapSaveAsFile;
import com.joewoo.ontime.support.image.BitmapScale;
import com.joewoo.ontime.support.info.Defines;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.net.ImageNetworkListener;
import com.joewoo.ontime.support.util.GlobalContext;

import java.io.File;

import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.TEMP_IMAGE_NAME;
import static com.joewoo.ontime.support.info.Defines.TEMP_IMAGE_PATH;

public class DownloadPhoto extends AsyncTask<String, Integer, Bitmap> implements ImageNetworkListener.DownloadProgressListener {

    private ImageView iv;
    private TextView tv;
    private boolean isRepost = false;
    private Activity act;
    private Animation in;
    private Animation out;
    private float width;
    private ViewGroup.LayoutParams lp;

    public DownloadPhoto(ImageView iv, TextView tv, boolean isRepost, Activity act) {
        this.iv = iv;
        this.tv = tv;
        this.isRepost = isRepost;
        this.act = act;
    }

    @Override
    protected void onPreExecute() {
        // Toast.makeText(activity, "开始下载图片…", Toast.LENGTH_SHORT).show();
        in = AnimationUtils.loadAnimation(GlobalContext.getAppContext(), R.anim.in);
        out = AnimationUtils.loadAnimation(GlobalContext.getAppContext(), R.anim.out);

        if (tv != null) {
            DisplayMetrics dm = new DisplayMetrics();
            act.getWindowManager().getDefaultDisplay().getMetrics(dm);
            width = ((dm.widthPixels * dm.density) - 32) / 100;
            tv.setVisibility(View.VISIBLE);
            lp = tv.getLayoutParams();
        }

    }

    @Override
    protected Bitmap doInBackground(String... params) {

        Log.e(TAG, "Download Pic AsyncTask START");
        Log.e(TAG, "Pic URL - " + params[0]);

        Bitmap image;

        if (!params[0].endsWith(".gif")) {
            try {

                final byte[] imgBytes = new HttpUtility().executeDownloadImageTask(params[0], this);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "Save image Thread START");
                        BitmapSaveAsFile.save(BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length), BitmapSaveAsFile.SAVE_AS_PNG, Defines.TEMP_IMAGE_PATH, Defines.TEMP_IMAGE_NAME);
                    }
                }).start();
                image = BitmapScale.scaleBitmapFromArray(imgBytes, 400, 400);


            } catch (Exception e) {
                Log.e(TAG, "Download Pic AsyncTask FAILED");
                e.printStackTrace();
                return null;
            }

        } else {
            Log.e(TAG, "GIF image...");
            return null;
        }

        return image;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        lp.width = (int) (width * progress[0]);
        tv.setLayoutParams(lp);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (!isRepost) {
            tv.setVisibility(View.GONE);
            tv.startAnimation(out);
            lp.width = 0;
            tv.setLayoutParams(lp);
        }

        iv.setVisibility(View.VISIBLE);

        if (bitmap != null) {
            iv.setImageBitmap(bitmap);
        } else {
            lp.width = 10000;
            tv.setLayoutParams(lp);
            iv.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        }
        iv.startAnimation(in);
    }

    @Override
    public void downloadProgress(int transferred, int contentLength) {
        publishProgress((int) (((double) transferred / (double) contentLength) * 100));
    }

}
