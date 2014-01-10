package com.joewoo.ontime.support.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.joewoo.ontime.action.MyMaidActionHelper;
import com.joewoo.ontime.support.net.ImageNetworkListener;
import com.joewoo.ontime.support.notification.MyMaidNotificationHelper;
import com.joewoo.ontime.support.sql.MyMaidSQLHelper;
import com.joewoo.ontime.ui.Post;

import java.util.Timer;
import java.util.TimerTask;

import static com.joewoo.ontime.support.info.Defines.FILE_PATH;
import static com.joewoo.ontime.support.info.Defines.GOT_UPLOAD_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_UPLOAD_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.STATUS;

/**
 * Created by JoeWoo on 14-1-3.
 */
public class UploadService extends Service implements ImageNetworkListener.UploadProgressListener {

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
                    mNotification.setFail((String) msg.obj);
                    Intent i = new Intent(UploadService.this, Post.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    break;
                }
            }
            stopSelf();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        String status = intent.getStringExtra(STATUS);

        MyMaidActionHelper.statusesUpload(status, intent.getStringExtra(FILE_PATH), this, handler);

        mNotification = new MyMaidNotificationHelper(MyMaidNotificationHelper.UPLOAD, status, this);
        mNotification.setSending();

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
