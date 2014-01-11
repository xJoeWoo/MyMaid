package com.joewoo.ontime.support.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.util.GlobalContext;

import static com.joewoo.ontime.support.info.Defines.TAG;

/**
 * Created by JoeWoo on 13-12-26.
 */
public class DownloadMuiltPic extends AsyncTask<String, Integer, Bitmap> {

    private ImageView iv;
    private Animation in;

    public DownloadMuiltPic(ImageView iv) {
        this.iv = iv;
    }

    @Override
    protected void onPreExecute() {
        in = AnimationUtils.loadAnimation(GlobalContext.getAppContext(), R.anim.in);
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        Log.e(TAG, "Download Muilt Pic AsyncTask START");
        Log.e(TAG, "Pic URL - " + params[0]);

        if(!params[0].endsWith(".gif")){
            byte[] bytes;

            try {

                bytes = new HttpUtility().executeDownloadImageTask(params[0], null);

                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap != null) {
            iv.setImageBitmap(bitmap);
            iv.startAnimation(in);
        } else {
            iv.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        }
    }
}
