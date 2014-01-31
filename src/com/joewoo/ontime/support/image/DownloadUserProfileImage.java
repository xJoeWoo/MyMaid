package com.joewoo.ontime.support.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.joewoo.ontime.support.net.HttpUtility;

import java.io.ByteArrayInputStream;

import static com.joewoo.ontime.support.info.Defines.TAG;

public class DownloadUserProfileImage extends AsyncTask<String, Integer, Bitmap> {

    private ImageView iv;

    public DownloadUserProfileImage(ImageView iv) {
        this.iv = iv;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        Log.e(TAG, "Download User Proflie Image AsyncTask START");
        Log.e(TAG, "Pic URL - " + params[0]);

        Bitmap image = null;

        try {

//                byte[] imgBytes = new HttpUtility().executeDownloadImageTask(params[0], null);
//
//                image = BitmapRoundCorner.toRoundCorner(BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length), 25);

            image = BitmapRoundCorner.toRoundCorner(BitmapFactory.decodeStream(new ByteArrayInputStream(new HttpUtility().executeDownloadImageTask(params[0], null))), 25);


        } catch (Exception e) {
            Log.e(TAG, "Download User Profile Image AsyncTask FAILED");
            e.printStackTrace();
        }


        return image;
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (bitmap != null) {
            iv.setImageBitmap(bitmap);
        }

        bitmap = null;
    }


}
