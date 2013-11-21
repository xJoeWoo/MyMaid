package com.joewoo.ontime.support.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by JoeWoo on 13-11-21.
 */
public class GlobalContext extends Application{

    private static GlobalContext globalContext;

    @Override
    public void onCreate() {
        super.onCreate();
        globalContext = this;
    }

    public static GlobalContext getInstance() {
        return globalContext;
    }

    public static Context getAppContext() {
        return globalContext.getApplicationContext();
    }
}
