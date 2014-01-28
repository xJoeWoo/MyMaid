package com.joewoo.ontime.support.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.joewoo.ontime.action.MyMaidActionHelper;
import com.joewoo.ontime.support.notification.MyMaidNotificationHelper;
import com.joewoo.ontime.support.sql.MyMaidSQLHelper;

import java.util.Timer;
import java.util.TimerTask;

import static com.joewoo.ontime.support.info.Defines.GOT_UPDATE_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_UPDATE_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.STATUS;
import static com.joewoo.ontime.support.info.Defines.TAG;

/**
 * Created by JoeWoo on 13-12-31.
 */
public class UpdateService extends Service {

    private MyMaidNotificationHelper mNotification;

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
                    }, MyMaidNotificationHelper.SUCCESS_NOTIFICATION_SHOW_TIME);
                    break;
                }
                case GOT_UPDATE_INFO_FAIL: {
                    mNotification.setFail((String) msg.obj, UpdateService.this);
                    break;
                }
            }
            stopSelf();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        MyMaidActionHelper.statusesUpdate(intent.getStringExtra(STATUS), handler);
        mNotification = new MyMaidNotificationHelper(MyMaidNotificationHelper.UPDATE, intent, this);
        mNotification.setSending();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Post Service DESTROY!");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
