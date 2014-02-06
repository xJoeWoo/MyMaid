package com.joewoo.ontime.support.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.joewoo.ontime.support.notification.MyMaidNotificationHelper;
import com.joewoo.ontime.support.sql.MyMaidSQLHelper;

/**
 * Created by Joe on 14-2-6.
 */
public class ClearDraftService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        MyMaidSQLHelper.clearDraft();
        MyMaidNotificationHelper.cancel(MyMaidNotificationHelper.UPDATE);
        MyMaidNotificationHelper.cancel(MyMaidNotificationHelper.UPLOAD);
        stopSelf();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
