package com.joewoo.ontime.support.net;

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

    public String executeUploadImageTask(String urlStr, Map<String, String> param, String path, ImageNetworkListener.UploadProgressListener listener) throws Exception {
        return new JavaHttpUtility().doUploadFile(urlStr, param, path, listener);
    }

    public byte[] executeDownloadImageTask(String urlStr, ImageNetworkListener.DownloadProgressListener listener) throws Exception {
        return new JavaHttpUtility().doDownloadImage(urlStr, listener);
    }

}
