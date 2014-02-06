package com.joewoo.ontime.support.info;

/**
 * Created by JoeWoo on 13-10-19.
 * OOOOOH I HAVE GRADE 3 IN SENIOR HIGH!!!
 */
public class AcquireCount {

    public static String FRIENDS_TIMELINE_COUNT = "50";
    public static String MENTIONS_COUNT = "25";
    public static String COMMENTS_TO_ME_COUNT = "25";
    public static String COMMENTS_SHOW_COUNT = "50";
    public static String REPOSTS_TIMELINE_COUNT = "50";
    public static String COMMENTS_MENTIONS_COUNT = "25";
    public static String USER_TIMELINE_COUNT = "50";
    public static String FRIENDS_IDS_COUNT = "1000";

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

}
