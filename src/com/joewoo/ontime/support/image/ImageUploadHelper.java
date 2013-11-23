package com.joewoo.ontime.support.image;

/**
 * Created by JoeWoo on 13-11-23.
 */
public class ImageUploadHelper {

    public interface ProgressListener {
        public void uploadProgress(int transferred, int contentLength);
        public void waitResponse();
    }

}
