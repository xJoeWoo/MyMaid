package com.joewoo.ontime.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.image.BitmapSaveAsFile;
import com.joewoo.ontime.support.info.Defines;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import uk.co.senab.photoview.PhotoViewAttacher;


public class ShowPhotoActivity extends Activity implements PhotoViewAttacher.OnMatrixChangedListener, PhotoViewAttacher.OnViewTapListener {

    private GifImageView iv;
    private File imgFile;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Defines.GOT_SAVED_IMAGE: {
                    Toast.makeText(ShowPhotoActivity.this, R.string.toast_image_saved, Toast.LENGTH_SHORT).show();
                    break;
                }
                case Defines.GOT_SAVED_IMAGE_FAIL: {
                    Toast.makeText(ShowPhotoActivity.this, R.string.toast_image_save_failed, Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_photo);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().hide();

        iv = (GifImageView) findViewById(R.id.iv_act_photo);
        iv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        imgFile = (File) getIntent().getSerializableExtra(Defines.PHOTO_FILE);
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
            case Defines.MENU_SAVE_PHOTO: {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (BitmapSaveAsFile.saveToSd(imgFile)) {
                            handler.sendEmptyMessage(Defines.GOT_SAVED_IMAGE);
                        } else {
                            handler.sendEmptyMessage(Defines.GOT_SAVED_IMAGE_FAIL);
                        }
                    }
                }).start();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMatrixChanged(RectF rect) {

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.clear();

        menu.add(0, Defines.MENU_SAVE_PHOTO, 0, R.string.menu_save_photo)
                .setIcon(R.drawable.content_save)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        if (getActionBar().isShowing())
            getActionBar().hide();
        else
            getActionBar().show();
    }

}
