package com.joewoo.ontime.action;

import java.io.ByteArrayOutputStream;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import com.joewoo.ontime.tools.RoundCorner;

import static com.joewoo.ontime.info.Constants.*;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

public class Weibo_ProfileImage extends Thread {

    private String url;
    private Handler mHandler;

    public Weibo_ProfileImage(String url, Handler handler) {

        this.url = url;
        this.mHandler = handler;
    }

    public void run() {

        Log.e(TAG, "Profile Image Thread START");

        Bitmap bm;

        HttpUriRequest httpGet = new HttpGet(url);

        try {
            bm = new RoundCorner(
                    BitmapFactory.decodeStream(new DefaultHttpClient()
                            .execute(httpGet).getEntity().getContent()), 25).getBitmap();

            Log.e(TAG, "GOT: Profile Image");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(CompressFormat.PNG, 100, baos);

            mHandler.obtainMessage(GOT_PROFILEIMG_INFO, baos.toByteArray())
                    .sendToTarget();

            // }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
