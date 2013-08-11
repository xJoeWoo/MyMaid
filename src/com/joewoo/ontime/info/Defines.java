package com.joewoo.ontime.info;

public class Defines {

	public final static int GOT_ACCESS_TOKEN = 1;
	public final static int GOT_ACCESS_TOKEN_FAIL = 2;
	public final static int GOT_UPDATE_INFO = 3;
	public final static int GOT_UPDATE_INFO_FAIL = 4;
	public final static int GOT_UPLOAD_INFO = 5;
	public final static int GOT_UPLOAD_INFO_FAIL = 6;
	public final static int GOT_SHOW_INFO = 7;
	public final static int GOT_SHOW_INFO_FAIL = 8;
	public final static int GOT_FAIL_INFO = 9;
	public final static int GOT_AT_SUGGESTIONS_INFO = 10;
	public final static int GOT_WEATHER_INFO = 11;
	public final static int GOT_PROFILEIMG_INFO = 12;
	public final static int GOT_FRIENDS_TIMELINE_INFO = 13;
	public final static int GOT_COMMENT_CREATE_INFO = 14;
	public final static int GOT_REPOST_INFO = 15;
	
	public final static int ACT_GOT_PHOTO = 1;
	public final static int ACT_GOT_AT = 2;
	
	public final static String TAG = "OnTime --- ";
	public final static String TAG_SQL = "OnTime SQL ---";
	public final static String LOG_DEVIDER = "=========================";
	public final static String SQL_NAME = "MyMaid.db";
	
	public final static String KEY_AT_USER = "at_user";
	
	public final static String EXPIRES_IN =  "expires_in";
	public final static String ACCESS_TOKEN = "access_token";
	public final static String STATUS = "status";
	public final static String PIC = "pic";
	public final static String AUTH_CODE = "auth_code";
	public final static String UID = "uid";
	public final static String SCREEN_NAME = "screen_name";
	public final static String LOCATION = "location";
	public final static String NICKNAME = "nickname";
	public final static String TEXT = "text";
	public final static String COMMENTS_COUNT = "comments_count";
	public final static String REPOSTS_COUNT = "reposts_count";
	public final static String SOURCE = "source";
	public final static String CREATED_AT = "created_at";
	public final static String RETWEETED_STATUS = "retweeted_status";
	public final static String RETWEETED_STATUS_SCREEN_NAME = "retweeted_status_screen_name";
	public final static String WEIBO_ID = "weibo_id";
	public final static String IS_COMMENT = "isComment";
	
	public final static String[] dayNames = {"星期几", "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
	
	public final static String PREFERENCES = "ontime";
	public final static String LASTUID = "last_uid";
	public final static String ALLUID = "all_uid";
	public final static String ALLUSER = "all_user";
	public final static String DRAFT = "draft";
	
	public final static int MENU_ADD = 1;
	public final static int MENU_POST = 2;
	public final static int MENU_ACCESS_TOKEN = 3;
	public final static int MENU_LOGOUT = 4;
	public final static int MENU_LETTERS = 5;
	public final static int MENU_CLEAR_DRAFT = 6;
	public final static int MENU_AT = 7;
	public final static int MENU_EMOTION = 8;
	public final static int MENU_TOPIC = 9;
	public final static int MENU_PROFILE_SWITCH = 10;
	
	public final static int CONNECT_TIMEOUT = 5000;
	public final static int READ_TIMEOUT = 5000;
	
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
	public final static String COMMENT_CREATE_URL = "https://api.weibo.com/2/comments/create.json";
	public final static String REPOST_URL = "https://api.weibo.com/2/statuses/repost.json";
}
