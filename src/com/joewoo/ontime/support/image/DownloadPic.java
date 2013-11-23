package com.joewoo.ontime.support.image;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.image.BitmapRoundCorner;
import com.joewoo.ontime.support.image.BitmapSaveAsFile;
import com.joewoo.ontime.support.image.BitmapScale;
import com.joewoo.ontime.support.info.Defines;
import com.joewoo.ontime.support.net.HttpUtility;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static com.joewoo.ontime.support.info.Defines.TAG;

public class DownloadPic extends AsyncTask<String, Integer, Bitmap> implements ImageDownloadHelper.ProgressListener{

    private ImageView iv;
    private ProgressBar pb;
    private TextView tv;
    private boolean isRepost = false;
    private Activity act;
    private Animation fromBottom;

    public DownloadPic(ImageView iv, ProgressBar pb) {
        this.iv = iv;
        this.pb = pb;

    }

    public DownloadPic(ImageView iv) {
        this.iv = iv;
    }

    public DownloadPic(ImageView iv, TextView tv, boolean isRepost, Activity act) {
        this.iv = iv;
        this.tv = tv;
        this.isRepost = isRepost;
        this.act = act;
    }

    @Override
    protected void onPreExecute() {
        // Toast.makeText(activity, "开始下载图片…", Toast.LENGTH_SHORT).show();
        fromBottom = AnimationUtils.loadAnimation(act, R.anim.up_from_bottom);
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        Log.e(TAG, "Download Pic AsyncTask START");
        Log.e(TAG, "Pic URL - " + params[0]);

        Bitmap image = null;

        if (!params[0].endsWith(".gif")) {
            try {

                byte[] imgBytes  = new HttpUtility().executeDownloadImageTask(params[0], this);

                if (!isCancelled()) {
//                    byte[] imgBytes = baos.toByteArray();
                    image = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                    if (imgBytes.length > 4000) {

                        BitmapSaveAsFile.save(image, BitmapSaveAsFile.SAVE_AS_PNG, Defines.TEMP_IMAGE_PATH, Defines.TEMP_IMAGE_NAME);

                        image.recycle();
                        image = BitmapScale.scaleBitmapFromArray(imgBytes, 256, 256);
                        Log.e(TAG, "Hegiht: " + String.valueOf(image.getHeight()) + " Width: " + String.valueOf(image.getWidth()));
                    } else {
                        image = BitmapRoundCorner.toRoundCorner(image, 25);
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "Download Pic AsyncTask FAILED");
                e.printStackTrace();
            }

        } else {
            Log.e(TAG, "GIF image...");
            image = null;
        }

        return image;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
//        Log.e(TAG, "Progress: "+String.valueOf(progress[0]));
        if (!isCancelled()) {
            if (pb != null) {
                if (progress[0] == 0)
                    pb.setVisibility(View.VISIBLE);
                // Log.e(TAG, "Process - " + String.valueOf(progress[0]));
                pb.setProgress(progress[0]);
            } else if (tv != null) {

                DisplayMetrics dm = new DisplayMetrics();
                act.getWindowManager().getDefaultDisplay().getMetrics(dm);
                float width = ((dm.widthPixels * dm.density) - 32) / 100;

                tv.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams lp = tv.getLayoutParams();
                lp.width = (int) (width * progress[0]);
                tv.setLayoutParams(lp);
            }
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (!isCancelled()) {
            if (bitmap != null) {
                iv.setImageBitmap(bitmap);
            } else {
                if (tv != null) {
                    ViewGroup.LayoutParams lp = tv.getLayoutParams();
                    lp.width = 10000;
                    tv.setLayoutParams(lp);
                }
                iv.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            }

            iv.startAnimation(fromBottom);

            if (pb != null)
                pb.setVisibility(View.INVISIBLE);
            if (tv != null && !isRepost)
                tv.setVisibility(View.GONE);
        }
        bitmap = null;
    }


    @Override
    public void downloadProgress(int transferred, int contentLength) {
        publishProgress(((int)((float)transferred/(float)contentLength) * 100) + 1);
    }

}
