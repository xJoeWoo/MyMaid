package com.joewoo.ontime.support.image;

/**
 * Created by JoeWoo on 13-11-23.
 */
public class ImageDownloadHelper {

    public interface ProgressListener {
        public void downloadProgress(int transferred, int contentLength);
    }
}
