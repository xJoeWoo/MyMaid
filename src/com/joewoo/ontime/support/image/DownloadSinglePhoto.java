package com.joewoo.ontime.support.image;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joewoo.ontime.support.info.Defines;
import com.joewoo.ontime.support.listener.MyMaidListeners;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.ui.Photo;

import static com.joewoo.ontime.support.info.Defines.TAG;

public class DownloadSinglePhoto extends AsyncTask<String, Integer, byte[]> implements MyMaidListeners.DownloadProgressListener {

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
    protected byte[] doInBackground(String... params) {

        Log.e(TAG, "Download Pic AsyncTask START");
        Log.e(TAG, "Pic URL - " + params[0]);

        Bitmap image;

        if (!params[0].endsWith(".gif")) {
            try {

//                final byte[] imgBytes = new HttpUtility().executeDownloadImageTask(params[0], this);
//
//                BitmapSaveAsFile.save(BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length), BitmapSaveAsFile.SAVE_AS_PNG, Defines.TEMP_IMAGE_PATH, Defines.TEMP_IMAGE_NAME);

                return new HttpUtility().executeDownloadImageTask(params[0], this);


            } catch (Exception e) {
                Log.e(TAG, "Download Pic AsyncTask FAILED");
                e.printStackTrace();
                return null;
            }

        } else {
            Log.e(TAG, "GIF image...");
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        lp.width = (int) (width * progress[0]);
        tv.setLayoutParams(lp);
    }

    @Override
    protected void onPostExecute(byte[] imgBytes) {
        if (imgBytes != null) {
            if (!isRepost)
                tv.setVisibility(View.INVISIBLE);

            Intent i = new Intent(act, Photo.class);
            i.putExtra(Defines.PHOTO_BYTES, imgBytes);
            act.startActivity(i);
        }
    }

    @Override
    public void downloadProgress(int transferred, int contentLength) {
        publishProgress((int) (((double) transferred / (double) contentLength) * 100));
    }

}
