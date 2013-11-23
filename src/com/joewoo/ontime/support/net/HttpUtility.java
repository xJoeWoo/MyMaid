package com.joewoo.ontime.support.net;

import android.graphics.Bitmap;
import android.webkit.DownloadListener;

import com.joewoo.ontime.support.image.ImageDownloadHelper;
import com.joewoo.ontime.support.image.ImageUploadHelper;

import java.util.Map;

/**
 * Created by JoeWoo on 13-11-23.
 */
public class HttpUtility {

    public String executeGetTask(String urlStr, Map<String, String> param) throws Exception {
        return new JavaHttpUtility().doGet(urlStr, param);
    }

    public String executePostTask(String urlStr, Map<String, String> param) throws Exception {
        return new JavaHttpUtility().doPost(urlStr, param);
    }

    public String executeUploadImageTask(String urlStr, Map<String, String> param, String path, String imageParamName, ImageUploadHelper.ProgressListener listener) throws Exception {
        return new JavaHttpUtility().doUploadFile(urlStr, param, path, imageParamName, listener);
    }

    public byte[] executeDownloadImageTask(String urlStr, ImageDownloadHelper.ProgressListener listener) throws Exception {
        return new JavaHttpUtility().doDownloadImage(urlStr, listener);
    }

}
