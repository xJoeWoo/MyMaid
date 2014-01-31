package com.joewoo.ontime.support.image;

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
import com.joewoo.ontime.support.listener.MyMaidListeners;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.util.GlobalContext;
import com.joewoo.ontime.support.util.MyMaidUtilites;
import com.joewoo.ontime.ui.singleweibo.SingleWeiboActivity;

import static com.joewoo.ontime.support.info.Defines.TAG;

public class DownloadPhoto extends AsyncTask<String, Integer, byte[]> implements MyMaidListeners.DownloadProgressListener {

    private ImageView iv;
    private TextView tv;
    private boolean isRepost = false;
    private SingleWeiboActivity act;
    private Animation in;
    private Animation out;
    private float width;
    private ViewGroup.LayoutParams lp;

    public DownloadPhoto(ImageView iv, TextView tv, boolean isRepost, SingleWeiboActivity act) {
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
        lp.width = (int) (width * progress[0]);
        tv.setLayoutParams(lp);
    }

    @Override
    protected void onPostExecute(byte[] bytes) {

        iv.setVisibility(View.VISIBLE);

        if (bytes != null) {
            MyMaidUtilites.setBitmapToSingleWeibo(bytes, iv);
            act.setImageBytes(bytes);


        } else {
            lp.width = 10000;
            tv.setLayoutParams(lp);
            iv.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        }
        iv.startAnimation(in);

        if (!isRepost) {
            tv.setVisibility(View.GONE);
            tv.startAnimation(out);
            lp.width = 0;
            tv.setLayoutParams(lp);
        }

    }

    @Override
    public void downloadProgress(int transferred, int contentLength) {
        publishProgress((int) (((double) transferred / (double) contentLength) * 100));
    }

}
