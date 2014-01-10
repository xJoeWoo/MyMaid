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
import com.joewoo.ontime.ui.CommentRepost;

import java.util.Timer;
import java.util.TimerTask;

import static com.joewoo.ontime.support.info.Defines.GOT_REPOST_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_REPOST_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.IS_REPOST;
import static com.joewoo.ontime.support.info.Defines.STATUS;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;

/**
 * Created by JoeWoo on 13-12-31.
 */
public class RepostService extends Service {

    private MyMaidNotificationHelper mNotification;
    private String status;
    private String weiboID;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GOT_REPOST_INFO: {
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
                case GOT_REPOST_INFO_FAIL: {
                    mNotification.setFail((String) msg.obj);
                    Intent i = new Intent(RepostService.this, CommentRepost.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra(WEIBO_ID, weiboID);
                    i.putExtra(STATUS, status);
                    i.putExtra(IS_REPOST, true);
                    startActivity(i);
                    break;
                }
            }
            stopSelf();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        status = intent.getStringExtra(STATUS);
        weiboID = intent.getStringExtra(WEIBO_ID);

        MyMaidActionHelper.statusesRepost(status, weiboID, handler);

        mNotification = new MyMaidNotificationHelper(MyMaidNotificationHelper.REPOST, status, this);
        mNotification.setSending();


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Repost Service DESTROY!");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
