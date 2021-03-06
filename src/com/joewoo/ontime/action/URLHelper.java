package com.joewoo.ontime.action;

import com.joewoo.ontime.support.info.Defines;

/**
 * Created by JoeWoo on 13-10-19.
 * OOOOOH I HAVE GRADE 3 IN SENIOR HIGH!!!
 */
public class URLHelper {

    public final static String CALLBACK = "http://mymaid.sinaapp.com/callback.php";
    public final static String UPDATE = "https://api.weibo.com/2/statuses/update.json";
    public final static String UPLOAD = "https://upload.api.weibo.com/2/statuses/upload.json";
    public final static String USER_SHOW = "https://api.weibo.com/2/users/show.json";

    public final static String AUTH = "https://api.weibo.com/oauth2/authorize?client_id="
            + Defines.APP_KEY
            + "&response_type=code&redirect_uri="
            + CALLBACK
            + "&display=mobile";

    public final static String AT_SUGGESTIONS = "https://api.weibo.com/2/search/suggestions/at_users.json";
    public final static String TOKEN = "https://api.weibo.com/oauth2/access_token";
    public final static String FRIENDS_TIMELINE = "https://api.weibo.com/2/statuses/friends_timeline.json";
    public final static String COMMENTS_CREATE = "https://api.weibo.com/2/comments/create.json";
    public final static String REPOST = "https://api.weibo.com/2/statuses/repost.json";
    public final static String FAVOURITE_CREATE = "https://api.weibo.com/2/favorites/create.json";
    public final static String COMMENTS_TO_ME = "https://api.weibo.com/2/comments/to_me.json";
    public final static String COMMENTS_BY_ME = "https://api.weibo.com/2/comments/by_me.json";
    public final static String COMMENTS_DESTROY = "https://api.weibo.com/2/comments/destroy.json";
    public final static String MENTIONS = "https://api.weibo.com/2/statuses/mentions.json";
    public final static String REPLY = "https://api.weibo.com/2/comments/reply.json";
    public final static String UNREAD_COUNT = "https://rm.api.weibo.com/2/remind/unread_count.json";
    public final static String SET_REMIND_COUNT = "https://rm.api.weibo.com/2/remind/set_count.json";
    public final static String USER_TIMELINE = "https://api.weibo.com/2/statuses/user_timeline.json";
    public final static String COMMENTS_SHOW = "https://api.weibo.com/2/comments/show.json";
    public final static String STATUSES_DESTROY = "https://api.weibo.com/2/statuses/destroy.json";
    public final static String REPOST_TIMELINE = "https://api.weibo.com/2/statuses/repost_timeline.json";
    public final static String COMMENTS_MENTIONS = "https://api.weibo.com/2/comments/mentions.json";
    public final static String FRIENDS_IDS = "https://api.weibo.com/2/friendships/friends/ids.json";
    public final static String STATUSES_SHOW = "https://api.weibo.com/2/statuses/show.json";

    public final static String AQI_DETAILS = "http://www.pm25.in/api/querys/aqi_details.json";
    public final static String WEATHER_FORECAST = "http://m.weather.com.cn/data/";
    public final static String WEATHER_NOW = "http://www.weather.com.cn/data/sk/";

    public final static String GOOGLE_GEO_CODE = "https://maps.googleapis.com/maps/api/geocode/json";

    public final static String MYMAID_VERSION = "http://mymaid.sinaapp.com/version.mymaid";
    public final static String MYMAID_DOWNLOAD = "http://mymaid-apk.stor.sinaapp.com/MyMaid.apk";

}
