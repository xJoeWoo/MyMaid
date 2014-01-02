package com.joewoo.ontime.support.service.post;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.joewoo.ontime.R;
import com.joewoo.ontime.action.statuses.StatusesUpdate;
import com.joewoo.ontime.support.info.NotificationIDs;
import com.joewoo.ontime.support.sql.MyMaidSQLHelper;
import com.joewoo.ontime.support.util.GlobalContext;
import com.joewoo.ontime.ui.Post;

import java.util.Timer;
import java.util.TimerTask;

import static com.joewoo.ontime.support.info.Defines.GOT_UPDATE_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_UPDATE_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.STATUS;
import static com.joewoo.ontime.support.info.Defines.TAG;

/**
 * Created by JoeWoo on 13-12-31.
 */
public class PostService extends Service {

    public final static String POST_STATUS = "post_status";

    private NotificationCompat.Builder nBuilder;
    private NotificationManager nManager;

    private String status;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent i = new Intent(PostService.this, Post.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            switch (msg.what) {
                case GOT_UPDATE_INFO: {
                    successNotification();
                    clearDraft();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            removeNotification();
                        }
                    }, 5000);
                    break;
                }
                case GOT_UPDATE_INFO_FAIL: {
                    failNotification((String) msg.obj);
                    i.putExtra(POST_STATUS, false);
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

        new StatusesUpdate(status, handler).start();

        showNotification();

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

        nBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_notify)
                .setContentTitle(getString(R.string.notify_post_posting))
                .setContentText("");

        nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showNotification() {
        nBuilder.setContentText("“" + status + "”");
        nBuilder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(PostService.this, Post.class), 0));
        nManager.notify(NotificationIDs.POST_SERVICE, nBuilder.build());
    }

    private void successNotification() {
        nBuilder.setTicker(getString(R.string.notify_post_success));
        nBuilder.setContentTitle(getString(R.string.notify_post_success));
        nBuilder.setContentIntent(null);
        nBuilder.setAutoCancel(true);
        nManager.notify(NotificationIDs.POST_SERVICE, nBuilder.build());
    }

    private void failNotification(String error) {
        nBuilder.setTicker(getString(R.string.notify_post_fail));
        nBuilder.setContentTitle(getString(R.string.notify_post_fail));
        nBuilder.setContentText(error);
        nManager.notify(NotificationIDs.POST_SERVICE, nBuilder.build());
    }

    private void removeNotification() {
        nManager.cancel(NotificationIDs.POST_SERVICE);
    }

    private void clearDraft() {
        ContentValues cv = new ContentValues();
        cv.put(MyMaidSQLHelper.DRAFT, "");
        if (GlobalContext.getSQL().update(MyMaidSQLHelper.tableName, cv, MyMaidSQLHelper.UID + "='"
                + GlobalContext.getUID() + "'", null) != 0) {
            Log.e(MyMaidSQLHelper.TAG_SQL, "Cleared draft");
        }
    }


}
