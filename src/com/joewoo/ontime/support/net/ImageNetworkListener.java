package com.joewoo.ontime.support.net;

/**
 * Created by JoeWoo on 13-11-23.
 */
public final class ImageNetworkListener {

    public interface UploadProgressListener {
        public void uploadProgress(int transferred, int contentLength);
        public void waitResponse();
    }

    public interface DownloadProgressListener {
        public void downloadProgress(int transferred, int contentLength);
    }

}
