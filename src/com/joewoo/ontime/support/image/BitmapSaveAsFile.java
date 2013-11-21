package com.joewoo.ontime.support.image;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by JoeWoo on 13-11-21.
 */
public class BitmapSaveAsFile {

    public static final int SAVE_AS_PNG = 1;
    public static final int SAVE_AS_JEPG = 2;
    public static final int SAVE_AS_WEBP = 3;

    public static boolean save(Bitmap bitmap, int saveAsWhat, String filePath, String fileName) {
        return toSave(bitmap, saveAsWhat, 100, filePath, fileName);
    }

    public static boolean save(Bitmap bitmap, int saveAsWhat, int quality, String filePath, String fileName) {
        return toSave(bitmap, saveAsWhat, quality, filePath, fileName);
    }

    private static boolean toSave(Bitmap bitmap, int saveAsWhat, int quality, String filePath, String fileName) {
        try {
            File f = new File(filePath);
            if (!f.exists()) {
                f.mkdirs();
            }
            f = new File(filePath, fileName);
            OutputStream fOut = new FileOutputStream(f);
            switch (saveAsWhat) {
                case SAVE_AS_PNG:
                    bitmap.compress(Bitmap.CompressFormat.PNG, quality, fOut);
                    break;
                case SAVE_AS_JEPG:
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fOut);
                    break;
                case SAVE_AS_WEBP:
                    bitmap.compress(Bitmap.CompressFormat.WEBP, quality, fOut);
                    break;
            }
            fOut.flush();
            fOut.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
