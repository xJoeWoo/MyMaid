package com.joewoo.ontime.support.listener;

/**
 * Created by Joe on 14-1-31.
 */
public class MyMaidListeners {

    public interface UploadProgressListener {
        public void uploadProgress(int transferred, int contentLength);

        public void waitResponse();
    }

    public interface DownloadProgressListener {
        public void downloadProgress(int transferred, int contentLength);
    }

}
