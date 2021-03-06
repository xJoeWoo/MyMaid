package com.joewoo.ontime.support.image;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import com.joewoo.ontime.support.net.HttpUtility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static com.joewoo.ontime.support.info.Defines.GOT_PROFILEIMG_INFO;
import static com.joewoo.ontime.support.info.Defines.TAG;

public class LoginProfileImage extends Thread {

    private String url;
    private Handler mHandler;

    public LoginProfileImage(String url, Handler handler) {

        this.url = url;
        this.mHandler = handler;
    }

    public void run() {
        Log.e(TAG, "Profile Image Thread START");

        try {
            Bitmap bm = BitmapRoundCorner.toRoundCorner(BitmapFactory.decodeStream(new ByteArrayInputStream(new HttpUtility().executeDownloadImageTask(url, null))), 90);

            Log.e(TAG, "GOT: Profile Image");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(CompressFormat.PNG, 100, baos);

            mHandler.obtainMessage(GOT_PROFILEIMG_INFO, baos.toByteArray())
                    .sendToTarget();

            bm.recycle();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
