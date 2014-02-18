package com.joewoo.ontime.support.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.joewoo.ontime.support.info.Defines;
import com.joewoo.ontime.support.util.GlobalContext;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by JoeWoo on 13-11-21.
 */
public class BitmapSaveAsFile {

    public static boolean saveToSd(File file) {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File f = new File(Defines.IMAGE_SAVE_PATH);
            if (!f.exists())
                f.mkdirs();

            f = new File(Defines.IMAGE_SAVE_PATH, String.valueOf(System.currentTimeMillis()) + ".jpg");

            try {

                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                OutputStream fOut = new FileOutputStream(f);

                return bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    public static File saveToData(byte[] bytes) {
        BufferedOutputStream bos = null;
        File cachePic = null;
        try {
            cachePic = new File(String.valueOf(GlobalContext.getAppContext().getCacheDir()), "CachePic.mymaid");
            FileOutputStream fos = new FileOutputStream(cachePic);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return cachePic;
    }
}
