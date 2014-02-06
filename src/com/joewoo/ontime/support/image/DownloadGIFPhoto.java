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

public class DownloadGIFPhoto extends AsyncTask<String, Integer, byte[]> implements MyMaidListeners.DownloadProgressListener {

    private SingleWeiboFragment frag;
    private TextView tv;
    private float width;
    private ViewGroup.LayoutParams lp;
    private boolean isSetSize;
    private double size;

    public DownloadGIFPhoto(TextView tv, SingleWeiboFragment frag) {
        this.frag = frag;
        this.tv = tv;
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

        try {

            return new HttpUtility().executeDownloadImageTask(params[0], this);

        } catch (Exception e) {
            Log.e(TAG, "Download Pic AsyncTask FAILED");
            e.printStackTrace();
            return null;
        }

    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        if (!isSetSize) {
            frag.setGIFSize(size);
            isSetSize = true;
        }
        lp.width = (int) (width * progress[0]);
        tv.setLayoutParams(lp);
    }

    @Override
    protected void onPostExecute(byte[] bytes) {
        if (bytes != null) {
            frag.jumpToPhoto(BitmapSaveAsFile.saveToData(bytes), true);
        }
    }

    @Override
    public void downloadProgress(int transferred, int contentLength) {
        if (!isSetSize)
            size = contentLength;
        publishProgress((int) (((double) transferred / (double) contentLength) * 100));
    }

}
