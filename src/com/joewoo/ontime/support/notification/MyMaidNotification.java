package com.joewoo.ontime.support.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.util.GlobalContext;
import com.joewoo.ontime.ui.Post;

/**
 * Created by JoeWoo on 14-1-3.
 */
public class MyMaidNotification {

    public static final int UPDATE = 0;
    public static final int UPLOAD = 1;
    public static final int COMMENT = 2;
    public static final int REPLY = 3;
    public static final int REPOST = 4;

    public static int PROGRESS_UPDATE_DELAY = 30;
    public static int NOTIFICATION_SHOW_TIME = 2 * 1000;

    private int what = -1;
    private String status;
    private Context context;

    private long downTime = 0;

    private NotificationCompat.Builder nBuilder;
    private NotificationManager nManager;

    public MyMaidNotification(int what, String status, Context context) {
        this.what = what;
        this.status = status;
        this.context = context;

        String title = null;

        switch (what) {
            case UPDATE:
            case UPLOAD: {
                title = GlobalContext.getResString(R.string.notify_post_sending);
                break;
            }
            case REPLY: {
                title = GlobalContext.getResString(R.string.notify_reply_sending);
                break;
            }
            case REPOST: {
                title = GlobalContext.getResString(R.string.notify_repost_sending);
                break;
            }
            case COMMENT: {
                title = GlobalContext.getResString(R.string.notify_comment_sending);
                break;
            }
        }

        nBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_notify)
                .setContentTitle(title)
                .setContentText("");

        nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void setSending() {
        switch (what) {
            case UPDATE:
            case UPLOAD: {
                nBuilder.setContentText("“" + status + "”");
                nBuilder.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, Post.class), 0));
                if (what == UPLOAD) {
                    nBuilder.setProgress(100, 0, false);
                    nManager.notify(UPLOAD, nBuilder.build());
                } else
                    nManager.notify(UPDATE, nBuilder.build());
                break;
            }
        }
    }

    public void setProgress(int progress) {
        if (what == UPLOAD && System.currentTimeMillis() - downTime > PROGRESS_UPDATE_DELAY) {
            downTime = System.currentTimeMillis();
            nBuilder.setProgress(100, progress, false);
            nBuilder.setContentInfo(String.valueOf(progress));
            nManager.notify(UPLOAD, nBuilder.build());
        }
    }

    public void setWaitResponse() {
        if(what == UPLOAD) {
            nBuilder.setContentTitle(GlobalContext.getResString(R.string.notify_post_waiting_response));
            nBuilder.setContentText(GlobalContext.getResString(R.string.notify_post_upload_pic_finish));
            nManager.notify(UPLOAD, nBuilder.build());
        }
    }

    public void setSuccess() {
        switch (what) {
            case UPDATE:
            case UPLOAD: {
                nBuilder.setTicker(GlobalContext.getResString(R.string.notify_post_success));
                nBuilder.setContentTitle(GlobalContext.getResString(R.string.notify_post_success));
                nBuilder.setContentIntent(null);
                nBuilder.setAutoCancel(true);
                if (what == UPLOAD) {
                    nBuilder.setProgress(0, 0, false);
                    nBuilder.setContentInfo(null);
                    nManager.notify(UPLOAD, nBuilder.build());
                } else
                    nManager.notify(UPDATE, nBuilder.build());
                break;
            }
        }
    }

    public void setFail(String error) {
        switch (what) {
            case UPDATE:
            case UPLOAD: {
                nBuilder.setTicker(GlobalContext.getResString(R.string.notify_post_fail));
                nBuilder.setContentTitle(GlobalContext.getResString(R.string.notify_post_fail));
                nBuilder.setContentText(error);
                if (what == UPLOAD) {
                    nBuilder.setProgress(0, 0, false);
                    nManager.notify(UPLOAD, nBuilder.build());
                } else
                    nManager.notify(UPDATE, nBuilder.build());
                break;
            }
        }
    }

    public void setRemove() {
        nManager.cancel(what);
    }


}
