package com.joewoo.ontime.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.info.Defines;

import uk.co.senab.photoview.PhotoViewAttacher;


public class Photo extends Activity implements PhotoViewAttacher.OnMatrixChangedListener, PhotoViewAttacher.OnViewTapListener {

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Defines.GOT_BITMAP_CREATED_INFO) {
                setBitmap((Bitmap) msg.obj);
                pb.setVisibility(View.GONE);

            }
        }
    };
    private ImageView iv;
    private ProgressBar pb;

    private PhotoViewAttacher photoViewAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_photo);

//        getActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        getActionBar().hide();


        iv = (ImageView) findViewById(R.id.iv_act_photo);
        pb = (ProgressBar) findViewById(R.id.pb_act_photo);
        iv.setVerticalScrollBarEnabled(true);
        iv.setHorizontalScrollBarEnabled(true);

        final byte[] imgBytes = getIntent().getByteArrayExtra(Defines.PHOTO_BYTES);

        if (imgBytes != null) {

            Log.e(Defines.TAG, String.valueOf(imgBytes.length));
            new Thread(new Runnable() {
                @Override
                public void run() {
//                    Bitmap bitmap1 = BitmapFactory.decodeStream(new ByteArrayInputStream(imgBytes));
                    Bitmap bitmap1 = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                    handler.obtainMessage(Defines.GOT_BITMAP_CREATED_INFO, bitmap1).sendToTarget();
                    bitmap1 = null;
                }
            }).start();

//            setBitmap(BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length));

        }

    }

    private void setBitmap(Bitmap bitmap) {
        iv.setImageBitmap(bitmap);

        photoViewAttacher = new PhotoViewAttacher(iv);
        photoViewAttacher.setOnMatrixChangeListener(this);
        photoViewAttacher.setOnViewTapListener(this);

        photoViewAttacher.setScaleType(ImageView.ScaleType.CENTER);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMatrixChanged(RectF rect) {

    }

    @Override
    public void onViewTap(View view, float x, float y) {
        finish();
    }
}
