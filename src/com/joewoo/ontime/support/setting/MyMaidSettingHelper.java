package com.joewoo.ontime.support.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.info.AcquireCount;
import com.joewoo.ontime.support.info.Defines;
import com.joewoo.ontime.support.util.GlobalContext;

/**
 * Created by Joe on 14-1-24.
 */
public class MyMaidSettingHelper {

    public static final String STORAGE_NAME = "SettingsFragment";
    public static final String KEY_NETWORK_ACQUIRE_COUNT_COMMENTS_MENTIONS = GlobalContext.getResString(R.string.setting_network_acquire_count_comments_mentions_key);
    public static final String KEY_NETWORK_ACQUIRE_COUNT_FRIENDS_TIMELINE = GlobalContext.getResString(R.string.setting_network_acquire_count_friends_timeline_key);
    public static final String KEY_NETWORK_ACQUIRE_COUNT_COMMENTS_TO_ME = GlobalContext.getResString(R.string.setting_network_acquire_count_comments_to_me_key);
    public static final String KEY_NETWORK_ACQUIRE_COUNT_MENTIONS = GlobalContext.getResString(R.string.setting_network_acquire_count_mentions_key);
    public static final String KEY_UI_DARK_MODE = GlobalContext.getResString(R.string.setting_ui_dark_mode_key);
    public static final String KEY_OTHER_CHECK_UPDATE = GlobalContext.getResString(R.string.setting_other_check_update_key);

    public static final SharedPreferences settings;
    public static final SharedPreferences.Editor editor;

    static {
        settings = GlobalContext.getAppContext().getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
    }

    public static void load() {
        AcquireCount.setUserTimelineCount(settings.getString(KEY_NETWORK_ACQUIRE_COUNT_FRIENDS_TIMELINE, "50"));
        AcquireCount.setMentionsCount(settings.getString(KEY_NETWORK_ACQUIRE_COUNT_MENTIONS, "25"));
        AcquireCount.setCommentsToMeCount(settings.getString(KEY_NETWORK_ACQUIRE_COUNT_COMMENTS_TO_ME, "25"));
        AcquireCount.setCommentsMentionsCount(settings.getString(KEY_NETWORK_ACQUIRE_COUNT_COMMENTS_MENTIONS, "25"));
    }

    public static void save(String key, String value) {
        editor.putString(key, value);
        Log.e(Defines.TAG, key);
        Log.e(Defines.TAG, value);
        commit();
    }

    public static void save(String key, boolean value) {
        editor.putBoolean(key, value);
        commit();
    }

    public static void save(String key, int value) {
        editor.putInt(key, value);
        commit();
    }

    private static void commit() {
        if(editor.commit())
            Log.e(Defines.TAG, "commited");
        editor.clear();
    }

}
