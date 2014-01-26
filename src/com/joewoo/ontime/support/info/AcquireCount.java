package com.joewoo.ontime.support.info;

import android.preference.Preference;
import android.util.Log;

import com.joewoo.ontime.support.setting.MyMaidSettingHelper;

/**
 * Created by JoeWoo on 13-10-19.
 * OOOOOH I HAVE GRADE 3 IN SENIOR HIGH!!!
 */
public class AcquireCount implements Preference.OnPreferenceChangeListener{

    public static String FRIENDS_TIMELINE_COUNT = "50";
    public static String MENTIONS_COUNT = "25";
    public static String COMMENTS_TO_ME_COUNT = "25";
    public static String COMMENTS_SHOW_COUNT = "50";
    public static String REPOSTS_TIMELINE_COUNT = "50";
    public static String COMMENTS_MENTIONS_COUNT = "25";
    public static String USER_TIMELINE_COUNT = "50";
    public static String FRIENDS_IDS_COUNT = "1000";

    public static void setCount(String key, int value) {
        if(key.equals(MyMaidSettingHelper.KEY_NETWORK_ACQUIRE_COUNT_FRIENDS_TIMELINE)) {
            AcquireCount.setFriendsTimelineCount(String.valueOf(value));
        } else if(key.equals(MyMaidSettingHelper.KEY_NETWORK_ACQUIRE_COUNT_COMMENTS_TO_ME)) {
            AcquireCount.setCommentsToMeCount(String.valueOf(value));
        } else if(key.equals(MyMaidSettingHelper.KEY_NETWORK_ACQUIRE_COUNT_MENTIONS)) {
            AcquireCount.setMentionsCount(String.valueOf(value));
        } else if(key.equals(MyMaidSettingHelper.KEY_NETWORK_ACQUIRE_COUNT_COMMENTS_MENTIONS)) {
            AcquireCount.setCommentsMentionsCount(String.valueOf(value));
        }
    }

    public static void setFriendsTimelineCount(String count) {
        AcquireCount.FRIENDS_TIMELINE_COUNT = count;
    }

    public static void setMentionsCount(String count) {
        AcquireCount.MENTIONS_COUNT = count;
    }

    public static void setCommentsToMeCount(String count) {
        AcquireCount.COMMENTS_TO_ME_COUNT = count;
    }

    public static void setCommentsMentionsCount(String count) {
        AcquireCount.COMMENTS_MENTIONS_COUNT = count;
    }

    public static void setUserTimelineCount(String count) {
        AcquireCount.USER_TIMELINE_COUNT = count;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        String key = preference.getKey();
        Log.e(Defines.TAG, "preference changed");
        if(key.equals(MyMaidSettingHelper.KEY_NETWORK_ACQUIRE_COUNT_FRIENDS_TIMELINE)) {
            setFriendsTimelineCount((String) newValue);
        } else if(key.equals(MyMaidSettingHelper.KEY_NETWORK_ACQUIRE_COUNT_COMMENTS_TO_ME)) {
            setCommentsToMeCount((String) newValue);
        } else if(key.equals(MyMaidSettingHelper.KEY_NETWORK_ACQUIRE_COUNT_MENTIONS)) {
            setMentionsCount((String) newValue);
        } else if(key.equals(MyMaidSettingHelper.KEY_NETWORK_ACQUIRE_COUNT_COMMENTS_MENTIONS)) {
            setCommentsMentionsCount((String) newValue);
        }

        return true;
    }
}
