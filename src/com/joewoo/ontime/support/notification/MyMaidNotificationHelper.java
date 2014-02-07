package com.joewoo.ontime.support.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.v4.app.NotificationCompat;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.bean.aqi.AQIBean;
import com.joewoo.ontime.support.info.Defines;
import com.joewoo.ontime.support.service.ClearDraftService;
import com.joewoo.ontime.support.service.CommentCreateService;
import com.joewoo.ontime.support.service.ReplyService;
import com.joewoo.ontime.support.service.RepostService;
import com.joewoo.ontime.support.service.UpdateService;
import com.joewoo.ontime.support.service.UploadService;
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
    public static final int WEATHER = 5;
    public static final int ALL = 99;

    public static final int PROGRESS_UPDATE_DELAY = 500;
    public static final int SUCCESS_NOTIFICATION_SHOW_TIME = 2 * 1000;

    private int what = -1;
    private Intent i;
    private String status;
    private NotificationCompat.BigPictureStyle bigPictureStyle;

    private long downTime = 0;

    private NotificationCompat.Builder nBuilder;
    private NotificationManager nManager;

    public MyMaidNotificationHelper(int what, Intent intent, Context context) {
        this.what = what;
        this.i = intent;

        String title = "";

        switch (what) {
            case UPDATE: {
                status = i.getStringExtra(Defines.STATUS);
                title = GlobalContext.getResString(R.string.notify_post_sending);
                break;
            }
            case UPLOAD: {
                status = i.getStringExtra(Defines.STATUS);
                title = GlobalContext.getResString(R.string.notify_post_uploading_pic);
                break;
            }
            case REPLY: {
                status = i.getStringExtra(Defines.COMMENT);
                title = GlobalContext.getResString(R.string.notify_reply_sending);
                break;
            }
            case REPOST: {
                status = i.getStringExtra(Defines.STATUS);
                title = GlobalContext.getResString(R.string.notify_repost_sending);
                break;
            }
            case COMMENT_CREATE: {
                status = i.getStringExtra(Defines.COMMENT);
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
            case UPDATE: {
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

    public void setSending(Bitmap b) {
        if (what == UPLOAD) {
            nBuilder.setProgress(100, 0, false);
            bigPictureStyle = new NotificationCompat.BigPictureStyle();
            nBuilder.setLargeIcon(ThumbnailUtils.extractThumbnail(b, 80, 80));
            bigPictureStyle.bigLargeIcon(BitmapFactory.decodeResource(GlobalContext.getAppContext().getResources(), R.drawable.ic_stat_notify));
            bigPictureStyle.setBuilder(nBuilder);
            bigPictureStyle.bigPicture(ThumbnailUtils.extractThumbnail(b, 400, 300));
            nBuilder.setStyle(bigPictureStyle);
            nManager.notify(UPLOAD, nBuilder.build());
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
        if (what == UPLOAD) {
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

    public void setFail(String error, Context context) {
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        nBuilder.setOngoing(false);
        nBuilder.setContentText(error);
        nBuilder.setAutoCancel(true);
        switch (what) {
            case UPDATE:
            case UPLOAD: {
                if (what == UPDATE) {
                    i.setClass(context, UpdateService.class);
                } else {
                    i.setClass(context, UploadService.class);
                }
                PendingIntent againIntent = PendingIntent.getService(context, 0, i, 0);
                PendingIntent discardIntent = PendingIntent.getService(context, 0, new Intent(context, ClearDraftService.class), 0);
                nBuilder.setContentIntent(againIntent);
                nBuilder.addAction(R.drawable.ic_stat_resend, GlobalContext.getResString(R.string.notify_resend), againIntent);
                nBuilder.addAction(R.drawable.ic_stat_discard, GlobalContext.getResString(R.string.notify_discard), discardIntent);
                nBuilder.setTicker(GlobalContext.getResString(R.string.notify_post_fail));
                nBuilder.setContentTitle(GlobalContext.getResString(R.string.notify_post_fail));
                if (what == UPLOAD) {
                    bigPictureStyle.setSummaryText(error);
                    nBuilder.setProgress(0, 0, false);
                    nBuilder.setContentInfo(null);
                    nManager.cancel(UPLOAD);
                    nManager.notify(UPLOAD, nBuilder.build());
                } else {
                    nManager.notify(UPDATE, nBuilder.build());
                }
                break;
            }
            case REPLY: {
                i.setClass(context, ReplyService.class);
                PendingIntent againIntent = PendingIntent.getService(context, 0, i, 0);
                nBuilder.setContentIntent(againIntent);
                nBuilder.addAction(R.drawable.ic_stat_resend, GlobalContext.getResString(R.string.notify_resend), againIntent);
                nBuilder.setTicker(GlobalContext.getResString(R.string.notify_reply_fail));
                nBuilder.setContentTitle(GlobalContext.getResString(R.string.notify_reply_fail));
                nManager.notify(REPLY, nBuilder.build());
                break;
            }
            case COMMENT_CREATE: {
                i.setClass(context, CommentCreateService.class);
                PendingIntent againIntent = PendingIntent.getService(context, 0, i, 0);
                nBuilder.setContentIntent(againIntent);
                nBuilder.addAction(R.drawable.ic_stat_resend, GlobalContext.getResString(R.string.notify_resend), againIntent);
                nBuilder.setTicker(GlobalContext.getResString(R.string.notify_comment_create_fail));
                nBuilder.setContentTitle(GlobalContext.getResString(R.string.notify_comment_create_fail));
                nManager.notify(COMMENT_CREATE, nBuilder.build());
                break;
            }
            case REPOST: {
                i.setClass(context, RepostService.class);
                PendingIntent againIntent = PendingIntent.getService(context, 0, i, 0);
                nBuilder.setContentIntent(againIntent);
                nBuilder.addAction(R.drawable.ic_stat_resend, GlobalContext.getResString(R.string.notify_resend), againIntent);
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
        nManager.cancel(WEATHER);
        nManager.notify(WEATHER, nBuilder.build());
    }

    public static void cancel(int what) {
        NotificationManager nM = (NotificationManager) GlobalContext.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (what == ALL)
            nM.cancelAll();
        else
            nM.cancel(what);
    }
}
