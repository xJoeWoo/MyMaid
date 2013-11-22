package com.joewoo.ontime.support.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by JoeWoo on 13-11-21.
 */
public class GlobalContext extends Application{

    private static GlobalContext globalContext;
    private static String AUTH_CODE;
    private static String ACCESS_TOKEN;
    private static String UID;
    private static String SCREEN_NAME;
    private static String PIC_PATH;
    private static String WORDS;

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

    public static String getAuthCode() {
        return AUTH_CODE;
    }

    public static void setAuthCode(String authCode) {
        AUTH_CODE = authCode;
    }

    public static String getAccessToken() {
        return ACCESS_TOKEN;
    }

    public static void setAccessToken(String accessToken) {
        ACCESS_TOKEN = accessToken;
    }

    public static String getUID() {
        return UID;
    }

    public static void setUID(String UID) {
        GlobalContext.UID = UID;
    }

    public static String getScreenName() {
        return SCREEN_NAME;
    }

    public static void setScreenName(String screenName) {
        SCREEN_NAME = screenName;
    }

    public static String getPicPath() {
        return PIC_PATH;
    }

    public static void setPicPath(String picPath) {
        PIC_PATH = picPath;
    }

    public static String getWords() {
        return WORDS;
    }

    public static void setWords(String words) {
        WORDS = words;
    }



}
