package com.joewoo.ontime.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.info.Defines;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import uk.co.senab.photoview.PhotoViewAttacher;


public class Photo extends Activity implements PhotoViewAttacher.OnMatrixChangedListener, PhotoViewAttacher.OnViewTapListener {

    private GifImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_photo);

//        getActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        getActionBar().hide();

        iv = (GifImageView) findViewById(R.id.iv_act_photo);
        iv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        final File imgFile = (File) getIntent().getSerializableExtra(Defines.PHOTO_FILE);
        final boolean isGIF = getIntent().getBooleanExtra(Defines.IS_GIF, false);

        if (imgFile != null) {
            if (isGIF) {
                setGIF(imgFile);
            } else {
                try {
                    setBitmap(BitmapFactory.decodeStream(new FileInputStream(imgFile)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private void setBitmap(Bitmap bitmap) {
        iv.setImageBitmap(bitmap);
        setAttach();
    }

    private void setGIF(File file) {
        try {
            GifDrawable gifDrawable = new GifDrawable(file);
            iv.setImageDrawable(gifDrawable);
            gifDrawable.start();
            setAttach();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setAttach() {
        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(iv);
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
