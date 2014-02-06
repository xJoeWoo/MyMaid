package com.joewoo.ontime.support.image;

import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joewoo.ontime.support.listener.MyMaidListeners;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.ui.singleweibo.SingleWeiboFragment;

import static com.joewoo.ontime.support.info.Defines.TAG;

public class DownloadSinglePhoto extends AsyncTask<String, Integer, byte[]> implements MyMaidListeners.DownloadProgressListener {

    private TextView tv;
    private SingleWeiboFragment frag;
    private float width;
    private ViewGroup.LayoutParams lp;

    public DownloadSinglePhoto(TextView tv, SingleWeiboFragment frag) {
        this.tv = tv;
        this.frag = frag;
    }

    @Override
    protected void onPreExecute() {

        DisplayMetrics dm = new DisplayMetrics();
        frag.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = ((dm.widthPixels * dm.density) - 32) / 100;
        tv.setVisibility(View.VISIBLE);
        lp = tv.getLayoutParams();

    }

    @Override
    protected byte[] doInBackground(String... params) {

        Log.e(TAG, "Download Pic AsyncTask START");
        Log.e(TAG, "Pic URL - " + params[0]);

        if (!params[0].endsWith(".gif")) {
            try {

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
        Log.e(TAG, String.valueOf((int) (width * progress[0])));
        lp.width = (int) (width * progress[0]);
        tv.setLayoutParams(lp);
    }

    @Override
    protected void onPostExecute(byte[] imgBytes) {
        if (imgBytes != null) {
            frag.jumpToPhoto(BitmapSaveAsFile.saveToData(imgBytes), false);
        }
    }

    @Override
    public void downloadProgress(int transferred, int contentLength) {
        publishProgress((int) (((double) transferred / (double) contentLength) * 100));
    }

}
