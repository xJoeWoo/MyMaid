package com.joewoo.ontime.support.setting;

import android.content.Context;
import android.content.SharedPreferences;

import com.joewoo.ontime.support.util.GlobalContext;

/**
 * Created by Joe on 14-2-7.
 */
public class MyMaidSettingsHelper {

    public static final String WEATHER_STATUS = "weather";
    public static final String WEATHER_CITY = "weather_city";

    public static final String STORE_NAME = "Settings.mymaid";
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    static {
        preferences = GlobalContext.getAppContext().getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static String getString(String key) {
        return preferences.getString(key, "");
    }

    public static boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public static void save(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public static void save(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }


}
