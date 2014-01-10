package com.joewoo.ontime.support.net;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.joewoo.ontime.support.image.BitmapSaveAsFile;
import com.joewoo.ontime.support.image.BitmapScale;
import com.joewoo.ontime.support.info.Defines;
import com.joewoo.ontime.support.util.GlobalContext;

import java.io.File;

import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.TEMP_IMAGE_NAME;
import static com.joewoo.ontime.support.info.Defines.TEMP_IMAGE_PATH;

public class DownloadPic extends AsyncTask<String, Integer, Bitmap> implements ImageNetworkListener.DownloadProgressListener {

    private ImageView iv;
    private TextView tv;
    private boolean isRepost = false;
    private Activity act;
    private Animation in;
    private Animation out;
    private float width;
    private ViewGroup.LayoutParams lp;

    public DownloadPic(ImageView iv, TextView tv, boolean isRepost, Activity act) {
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

        if(tv != null) {
            DisplayMetrics dm = new DisplayMetrics();
            act.getWindowManager().getDefaultDisplay().getMetrics(dm);
            width = ((dm.widthPixels * dm.density) - 32) / 100;
            tv.setVisibility(View.VISIBLE);
            lp = tv.getLayoutParams();
        }

    }

    @Override
    protected Bitmap doInBackground(String... params) {

        Log.e(TAG, "Download Pic AsyncTask START");
        Log.e(TAG, "Pic URL - " + params[0]);

        Bitmap image = null;

        if (!params[0].endsWith(".gif")) {
            try {

                byte[] imgBytes = new HttpUtility().executeDownloadImageTask(params[0], this);

                image = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);


                BitmapSaveAsFile.save(image, BitmapSaveAsFile.SAVE_AS_PNG, Defines.TEMP_IMAGE_PATH, Defines.TEMP_IMAGE_NAME);


                if(iv != null)
                    image = BitmapScale.resizeBitmap(image, 400, 400);
                else
                    return null;
//
//                Bitmap inputBitmap = image;
//                Bitmap outputBitmap = image;
//
//                RenderScript rs = RenderScript.create(act);
//                ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));;
//                Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
//                Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
//                theIntrinsic.setRadius(25.f);
//                theIntrinsic.setInput(tmpIn);
//                theIntrinsic.forEach(tmpOut);
//                tmpOut.copyTo(outputBitmap);
//
//                return outputBitmap;


//                    image = Bitmap.createScaledBitmap(image, 256, 256, true);
//                    image.recycle();
//                    image = BitmapScale.scaleBitmapFromArray(imgBytes, 128, 128);
//                    Log.e(TAG, "Hegiht: " + String.valueOf(image.getHeight()) + " Width: " + String.valueOf(image.getWidth()));


            } catch (Exception e) {
                Log.e(TAG, "Download Pic AsyncTask FAILED");
                e.printStackTrace();
                return null;
            }

        } else {
            Log.e(TAG, "GIF image...");
            return null;
        }

        return image;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
//        Log.e(TAG, "Progress: "+String.valueOf(progress[0]));

        if (tv != null) {
            lp.width = (int) (width * progress[0]);
            tv.setLayoutParams(lp);
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (tv != null && !isRepost) {
            tv.setVisibility(View.GONE);
            tv.startAnimation(out);
            lp.width = 0;
            tv.setLayoutParams(lp);
        }

        if(iv == null) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(TEMP_IMAGE_PATH, TEMP_IMAGE_NAME)), "image/*");
            act.startActivity(intent);
            return;
        }

        if (bitmap != null) {
            iv.setVisibility(View.VISIBLE);
            iv.setImageBitmap(bitmap);
        } else {
            if (tv != null) {
                lp.width = 10000;
                tv.setLayoutParams(lp);
            }
            iv.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        }
        iv.startAnimation(in);

        bitmap = null;
    }

    @Override
    public void downloadProgress(int transferred, int contentLength) {
        publishProgress((int) (((double) transferred / (double) contentLength) * 100));
    }

}
