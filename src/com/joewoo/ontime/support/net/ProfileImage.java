package com.joewoo.ontime.support.net;

import java.io.ByteArrayOutputStream;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import static com.joewoo.ontime.support.info.Defines.*;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import com.joewoo.ontime.support.image.BitmapRoundCorner;

public final class ProfileImage extends Thread {

    private String url;
    private Handler mHandler;

    public ProfileImage(String url, Handler handler) {

        this.url = url;
        this.mHandler = handler;
    }

    public void run() {
        Log.e(TAG, "Profile Image Thread START");
        Bitmap bm;

        try {
            HttpUriRequest httpGet = new HttpGet(url);

            bm = BitmapRoundCorner.toRoundCorner(
                    BitmapFactory.decodeStream(new DefaultHttpClient()
                            .execute(httpGet).getEntity().getContent()), 25);

            Log.e(TAG, "GOT: Profile Image");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(CompressFormat.PNG, 100, baos);

            mHandler.obtainMessage(GOT_PROFILEIMG_INFO, baos.toByteArray())
                    .sendToTarget();

            bm.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
