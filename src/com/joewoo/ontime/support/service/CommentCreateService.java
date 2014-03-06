package com.joewoo.ontime.support.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.joewoo.ontime.action.MyMaidActionHelper;
import com.joewoo.ontime.support.notification.MyMaidNotificationHelper;

import java.util.Timer;
import java.util.TimerTask;

import static com.joewoo.ontime.support.info.Defines.COMMENT;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENT_CREATE_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENT_CREATE_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;

/**
 * Created by JoeWoo on 14-1-8.
 */
public class CommentCreateService extends Service {

    private MyMaidNotificationHelper mNotification;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GOT_COMMENT_CREATE_INFO: {
                    mNotification.setSuccess();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mNotification.setRemove();
                        }
                    }, MyMaidNotificationHelper.SUCCESS_NOTIFICATION_SHOW_TIME);
                    break;
                }
                case GOT_COMMENT_CREATE_INFO_FAIL: {
                    mNotification.setFail((String) msg.obj);
                    break;
                }
            }
            stopSelf();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e(TAG, "Comment Create Service START!");

        MyMaidActionHelper.commentsCreate(intent.getStringExtra(COMMENT), intent.getStringExtra(WEIBO_ID), handler);
        mNotification = new MyMaidNotificationHelper(MyMaidNotificationHelper.COMMENT_CREATE, intent, this);
        mNotification.setSending();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Comment Create Service DESTROY!");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
