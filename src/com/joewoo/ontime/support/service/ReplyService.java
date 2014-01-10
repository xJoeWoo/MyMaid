package com.joewoo.ontime.support.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.joewoo.ontime.action.MyMaidActionHelper;
import com.joewoo.ontime.support.notification.MyMaidNotificationHelper;
import com.joewoo.ontime.ui.CommentRepost;

import java.util.Timer;
import java.util.TimerTask;

import static com.joewoo.ontime.support.info.Defines.COMMENT;
import static com.joewoo.ontime.support.info.Defines.COMMENT_ID;
import static com.joewoo.ontime.support.info.Defines.GOT_REPLY_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_REPLY_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.IS_REPLY;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;

/**
 * Created by JoeWoo on 14-1-8.
 */
public class ReplyService extends Service {

    private MyMaidNotificationHelper mNotification;
    private String comment;
    private String commentID;
    private String weiboID;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GOT_REPLY_INFO:{
                    mNotification.setSuccess();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mNotification.setRemove();
                        }
                    }, MyMaidNotificationHelper.SUCCESS_NOTIFICATION_SHOW_TIME);
                    break;
                }
                case GOT_REPLY_INFO_FAIL: {
                    mNotification.setFail((String) msg.obj);
                    Intent i = new Intent(ReplyService.this, CommentRepost.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra(COMMENT, comment);
                    i.putExtra(COMMENT_ID, commentID);
                    i.putExtra(WEIBO_ID, weiboID);
                    i.putExtra(IS_REPLY, true);
                    startActivity(i);
                    break;
                }
            }
            stopSelf();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        comment = intent.getStringExtra(COMMENT);
        commentID = intent.getStringExtra(COMMENT_ID);
        weiboID = intent.getStringExtra(WEIBO_ID);

        MyMaidActionHelper.commentsReply(comment, weiboID, commentID, false, handler);
        mNotification = new MyMaidNotificationHelper(MyMaidNotificationHelper.REPLY, comment, this);
        mNotification.setSending();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Reply Service DESTROY!");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
