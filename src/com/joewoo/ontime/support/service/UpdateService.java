package com.joewoo.ontime.support.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.joewoo.ontime.action.statuses.StatusesUpdate;
import com.joewoo.ontime.support.notification.MyMaidNotification;
import com.joewoo.ontime.support.sql.MyMaidSQLHelper;
import com.joewoo.ontime.ui.Post;

import java.util.Timer;
import java.util.TimerTask;

import static com.joewoo.ontime.support.info.Defines.GOT_UPDATE_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_UPDATE_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.TAG;

/**
 * Created by JoeWoo on 13-12-31.
 */
public class UpdateService extends Service {

    public final static String STATUS = "post_status";

    private MyMaidNotification mNotification;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GOT_UPDATE_INFO: {
                    mNotification.setSuccess();
                    MyMaidSQLHelper.clearDraft();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mNotification.setRemove();
                        }
                    }, MyMaidNotification.NOTIFICATION_SHOW_TIME);
                    break;
                }
                case GOT_UPDATE_INFO_FAIL: {
                    mNotification.setFail((String) msg.obj);
                    Intent i = new Intent(UpdateService.this, Post.class);
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

        new StatusesUpdate(status, handler).start();
        mNotification = new MyMaidNotification(MyMaidNotification.UPDATE, status, this);
        mNotification.setSending();


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Post Service DESTROY!");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
