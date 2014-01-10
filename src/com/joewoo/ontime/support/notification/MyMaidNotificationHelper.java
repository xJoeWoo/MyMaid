package com.joewoo.ontime.support.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.bean.aqi.AQIBean;
import com.joewoo.ontime.support.util.GlobalContext;

/**
 * Created by JoeWoo on 14-1-3.
 */
public class MyMaidNotificationHelper {

    public static final int UPDATE = 0;
    public static final int UPLOAD = 1;
    public static final int COMMENT_CREATE = 2;
    public static final int REPLY = 3;
    public static final int REPOST = 4;

    public static int PROGRESS_UPDATE_DELAY = 30;
    public static int SUCCESS_NOTIFICATION_SHOW_TIME = 2 * 1000;

    private int what = -1;
    private String status;

    private long downTime = 0;

    private NotificationCompat.Builder nBuilder;
    private NotificationManager nManager;

    public MyMaidNotificationHelper(int what, String status, Context context) {
        this.what = what;
        this.status = status;

        String title = "";

        switch (what) {
            case UPDATE: {
                title = GlobalContext.getResString(R.string.notify_post_sending);
                break;
            }
            case UPLOAD: {
                title = GlobalContext.getResString(R.string.notify_post_uploading_pic);
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
            case COMMENT_CREATE: {
                title = GlobalContext.getResString(R.string.notify_comment_create_sending);
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
        nBuilder.setOngoing(true);
        nBuilder.setContentText("\"" + status + "\"");
        switch (what) {
            case UPDATE:
            case UPLOAD: {
//                nBuilder.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, Post.class), 0));
                if (what == UPLOAD) {
                    nBuilder.setProgress(100, 0, false);
                    nManager.notify(UPLOAD, nBuilder.build());
                } else
                    nManager.notify(UPDATE, nBuilder.build());
                break;
            }

            case REPOST: {
                nManager.notify(REPOST, nBuilder.build());
                break;
            }
            case REPLY: {
                nManager.notify(REPLY, nBuilder.build());
                break;
            }
            case COMMENT_CREATE: {
                nManager.notify(COMMENT_CREATE, nBuilder.build());
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
            nBuilder.setTicker(GlobalContext.getResString(R.string.notify_post_upload_pic_finish));
            nBuilder.setProgress(100, 100, false);
            nBuilder.setContentInfo("100");
            nManager.notify(UPLOAD, nBuilder.build());
        }
    }

    public void setSuccess() {
        nBuilder.setOngoing(false);
        nBuilder.setContentIntent(null);
        nBuilder.setAutoCancel(true);
        switch (what) {
            case UPDATE:
            case UPLOAD: {
                nBuilder.setTicker(GlobalContext.getResString(R.string.notify_post_success));
                nBuilder.setContentTitle(GlobalContext.getResString(R.string.notify_post_success));
                if (what == UPLOAD) {
                    nBuilder.setProgress(0, 0, false);
                    nBuilder.setContentInfo(null);
                    nManager.notify(UPLOAD, nBuilder.build());
                } else
                    nManager.notify(UPDATE, nBuilder.build());
                break;
            }
            case REPOST: {
                nBuilder.setTicker(GlobalContext.getResString(R.string.notify_repost_success));
                nBuilder.setContentTitle(GlobalContext.getResString(R.string.notify_repost_success));
                nManager.notify(REPOST, nBuilder.build());
                break;
            }
            case REPLY: {
                nBuilder.setTicker(GlobalContext.getResString(R.string.notify_reply_success));
                nBuilder.setContentTitle(GlobalContext.getResString(R.string.notify_reply_success));
                nManager.notify(REPLY, nBuilder.build());
                break;
            }
            case COMMENT_CREATE: {
                nBuilder.setTicker(GlobalContext.getResString(R.string.notify_comment_create_success));
                nBuilder.setContentTitle(GlobalContext.getResString(R.string.notify_comment_create_success));
                nManager.notify(COMMENT_CREATE, nBuilder.build());
                break;
            }
        }
    }

    public void setFail(String error) {
        nBuilder.setOngoing(false);
        nBuilder.setContentText(error);
        switch (what) {
            case UPDATE:
            case UPLOAD: {
                nBuilder.setTicker(GlobalContext.getResString(R.string.notify_post_fail));
                nBuilder.setContentTitle(GlobalContext.getResString(R.string.notify_post_fail));
                if (what == UPLOAD) {
                    nBuilder.setProgress(0, 0, false);
                    nBuilder.setContentInfo(null);
                    nManager.notify(UPLOAD, nBuilder.build());
                } else
                    nManager.notify(UPDATE, nBuilder.build());
                break;
            }
            case REPLY:
            {
                nBuilder.setTicker(GlobalContext.getResString(R.string.notify_reply_fail));
                nBuilder.setContentTitle(GlobalContext.getResString(R.string.notify_reply_fail));
                nManager.notify(REPLY, nBuilder.build());
                break;
            }
            case COMMENT_CREATE: {
                nBuilder.setTicker(GlobalContext.getResString(R.string.notify_comment_create_fail));
                nBuilder.setContentTitle(GlobalContext.getResString(R.string.notify_comment_create_fail));
                nManager.notify(COMMENT_CREATE, nBuilder.build());
                break;
            }
            case REPOST: {
                nBuilder.setTicker(GlobalContext.getResString(R.string.notify_repost_fail));
                nBuilder.setContentTitle(GlobalContext.getResString(R.string.notify_repost_fail));
                nManager.notify(REPOST, nBuilder.build());
                break;
            }
        }
    }

    public void setRemove() {
        nManager.cancel(what);
    }

    public void setAQI(AQIBean bean) {
        nBuilder.setContentTitle(bean.getQuality() + " - pm25.in");
        nBuilder.setContentText("AQI:" + bean.getAQI() + "  首要污染物:" + bean.getPrimaryPollutant());
        nBuilder.setTicker(bean.getQuality());
        nManager.cancel(99);
        nManager.notify(99, nBuilder.build());
    }

}
