package com.joewoo.ontime.action;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.joewoo.ontime.tools.RoundCorner;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static com.joewoo.ontime.info.Defines.TAG;

public class Weibo_DownloadPic extends AsyncTask<String, Integer, Bitmap> {

    private ImageView iv;
    private Bitmap image;
    private ProgressBar pb;
    private TextView tv;
    private boolean isRepost = false;
    private Activity act;

    public Weibo_DownloadPic(ImageView iv, ProgressBar pb) {
        this.iv = iv;
        this.pb = pb;

    }

    public Weibo_DownloadPic(ImageView iv) {
        this.iv = iv;
    }

    public Weibo_DownloadPic(ImageView iv, TextView tv, boolean isRepost, Activity act) {
        this.iv = iv;
        this.tv = tv;
        this.isRepost = isRepost;
        this.act = act;
    }

    @Override
    protected void onPreExecute() {
        // Toast.makeText(activity, "开始下载图片…", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        Log.e(TAG, "Download Pic AsyncTask START");

        try {

            HttpUriRequest httpGet = new HttpGet(params[0]);

            Log.e(TAG, "Pic URL - " + params[0]);

            HttpEntity httpResponse = new DefaultHttpClient().execute(httpGet)
                    .getEntity();

            // Log.e(TAG, "2");

            publishProgress(0);

            InputStream is = httpResponse.getContent();

            long maxSize = httpResponse.getContentLength();
            Log.e(TAG, "MaxSize - " + String.valueOf(maxSize));
            float nowSize = 0;

            // Log.e(TAG, "3");

            // Log.e(TAG, "4");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len = -1;

            try {
                while ((len = is.read(buffer)) != -1) {
                    if (!isCancelled()) {
                        baos.write(buffer, 0, len);
                        baos.flush();
                        nowSize += len;
//						Log.e(TAG, String.valueOf(nowSize));
                        publishProgress((int) ((nowSize / (float) maxSize) * 100));
                    } else {
                        is.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                is.close();
            }

            byte[] imgBytes = baos.toByteArray();

            if (maxSize > 4000) {

                // image = new
                // GausscianBlur(BitmapFactory.decodeByteArray(imgBytes,
                // 0, imgBytes.length)).getBitmap();

                image = BitmapFactory.decodeByteArray(imgBytes, 0,
                        imgBytes.length);

            } else {

                image = new RoundCorner(BitmapFactory.decodeByteArray(imgBytes,
                        0, imgBytes.length), 25).getBitmap();

            }

        } catch (Exception e) {
            Log.e(TAG, "Download Pic AsyncTask FALIED");
            e.printStackTrace();
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
            iv.setImageBitmap(image);
            if (pb != null)
                pb.setVisibility(View.INVISIBLE);
            if (tv != null && !isRepost)
                tv.setVisibility(View.GONE);
        }
        image = null;
    }


}
