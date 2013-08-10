package com.joewoo.ontime.info;

public class WeiboConstant {
	
	public static String AUTH_CODE;
	public static String ACCESS_TOKEN;
	public static String UID;
	public static String SCREEN_NAME;
	public static String LOCATION;
	public static long EXPIRES_IN;
	public final static String CALLBACK_URL = "http://mymaid.sinaapp.com/callback.php";
	public final static String UPDATE_URL = "https://api.weibo.com/2/statuses/update.json";
	public final static String UPLOAD_URL = "https://upload.api.weibo.com/2/statuses/upload.json";
	public final static String SHOW_URL = "https://api.weibo.com/2/users/show.json";
	public final static String APP_KEY = "462564571";
	public final static String APP_SECRET = "f08ee7c15ba9afced27b3b916126f5ac";
	public final static String AUTH_URL = "https://api.weibo.com/oauth2/authorize?client_id="
			+ APP_KEY
			+ "&response_type=code&redirect_uri="
			+ CALLBACK_URL
			+ "&display=mobile";
	public final static String AT_SUGGESTIONS_URL = "https://api.weibo.com/2/search/suggestions/at_users.json";
	public final static String TOKEN_URL = "https://api.weibo.com/oauth2/access_token";
	public final static String TIMELINE_URL = "https://api.weibo.com/2/statuses/friends_timeline.json";
	
	public static String PICPATH;
	public static String WORDS;
}
