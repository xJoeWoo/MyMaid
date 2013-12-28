package com.joewoo.ontime.support.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.joewoo.ontime.support.image.BitmapRoundCorner;

import static com.joewoo.ontime.support.info.Defines.TAG;

public final class DownloadUserProfileImage extends AsyncTask<String, Integer, Bitmap[]> {

    private ImageView iv;

    public DownloadUserProfileImage(ImageView iv) {
        this.iv = iv;
    }

    @Override
    protected Bitmap[] doInBackground(String... params) {

        Log.e(TAG, "Download User Proflie Image AsyncTask START");
        Log.e(TAG, "Pic URL - " + params[0]);

        Bitmap[] image = new Bitmap[2];

            try {

                byte[] imgBytes = new HttpUtility().executeDownloadImageTask(params[0], null);

                image[0] = BitmapRoundCorner.toRoundCorner(BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length), 25);


            } catch (Exception e) {
                Log.e(TAG, "Download User Profile Image AsyncTask FAILED");
                e.printStackTrace();
            }


        return image;
    }


    @Override
    protected void onPostExecute(Bitmap[] bitmap) {

        if (bitmap != null) {
            iv.setImageBitmap(bitmap[0]);
        }

        bitmap = null;
    }


}
