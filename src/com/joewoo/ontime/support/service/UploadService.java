package com.joewoo.ontime.support.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.joewoo.ontime.action.MyMaidActionHelper;
import com.joewoo.ontime.support.listener.MyMaidListeners;
import com.joewoo.ontime.support.notification.MyMaidNotificationHelper;
import com.joewoo.ontime.support.sql.MyMaidSQLHelper;

import java.util.Timer;
import java.util.TimerTask;

import static com.joewoo.ontime.support.info.Defines.FILE_PATH;
import static com.joewoo.ontime.support.info.Defines.GOT_UPLOAD_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_UPLOAD_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.STATUS;
import static com.joewoo.ontime.support.info.Defines.TAG;

/**
 * Created by JoeWoo on 14-1-3.
 */
public class UploadService extends Service implements MyMaidListeners.UploadProgressListener {

    private MyMaidNotificationHelper mNotification;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case GOT_UPLOAD_INFO: {
                    mNotification.setSuccess();
                    MyMaidSQLHelper.clearDraft();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mNotification.setRemove();
                        }
                    }, MyMaidNotificationHelper.SUCCESS_NOTIFICATION_SHOW_TIME);
                    break;
                }
                case GOT_UPLOAD_INFO_FAIL: {
                    mNotification.setFail((String) msg.obj, UploadService.this);
                    break;
                }
            }
            stopSelf();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Upload Service DESTROY!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        MyMaidActionHelper.statusesUpload(intent.getStringExtra(STATUS), intent.getStringExtra(FILE_PATH), this, handler);

        mNotification = new MyMaidNotificationHelper(MyMaidNotificationHelper.UPLOAD, intent, this);
        mNotification.setSending(BitmapFactory.decodeFile(intent.getStringExtra(FILE_PATH)));

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void uploadProgress(int transferred, int contentLength) {
        mNotification.setProgress((int) (((float) transferred / (float) contentLength) * 100) + 1);
    }

    @Override
    public void waitResponse() {
        mNotification.setWaitResponse();
    }
}
