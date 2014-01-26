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
import com.joewoo.ontime.support.info.Defines;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.net.ImageNetworkListener;
import com.joewoo.ontime.support.util.GlobalContext;

import java.io.File;

import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.TEMP_IMAGE_NAME;
import static com.joewoo.ontime.support.info.Defines.TEMP_IMAGE_PATH;

public class DownloadSinglePhoto extends AsyncTask<String, Integer, Bitmap> implements ImageNetworkListener.DownloadProgressListener {

    private TextView tv;
    private Activity act;
    private boolean isRepost;
    private float width;
    private ViewGroup.LayoutParams lp;

    public DownloadSinglePhoto(TextView tv, boolean isRepost, Activity act) {
        this.tv = tv;
        this.act = act;
        this.isRepost = isRepost;
    }

    @Override
    protected void onPreExecute() {

        DisplayMetrics dm = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = ((dm.widthPixels * dm.density) - 32) / 100;
        tv.setVisibility(View.VISIBLE);
        lp = tv.getLayoutParams();

    }

    @Override
    protected Bitmap doInBackground(String... params) {

        Log.e(TAG, "Download Pic AsyncTask START");
        Log.e(TAG, "Pic URL - " + params[0]);

        if (!params[0].endsWith(".gif")) {
            try {

                final byte[] imgBytes = new HttpUtility().executeDownloadImageTask(params[0], this);

                BitmapSaveAsFile.save(BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length), BitmapSaveAsFile.SAVE_AS_PNG, Defines.TEMP_IMAGE_PATH, Defines.TEMP_IMAGE_NAME);

            } catch (Exception e) {
                Log.e(TAG, "Download Pic AsyncTask FAILED");
                e.printStackTrace();
            }

        } else {
            Log.e(TAG, "GIF image...");
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {

        lp.width = (int) (width * progress[0]);
        tv.setLayoutParams(lp);

    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (!isRepost)
            tv.setVisibility(View.GONE);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(TEMP_IMAGE_PATH, TEMP_IMAGE_NAME)), "image/*");
        act.startActivity(intent);

    }

    @Override
    public void downloadProgress(int transferred, int contentLength) {
        publishProgress((int) (((double) transferred / (double) contentLength) * 100));
    }

}
