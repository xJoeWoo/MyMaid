package com.joewoo.ontime.support.info;

import android.os.Environment;

public class Defines {

    public final static String APP_KEY = "462564571";
    public final static String APP_SECRET = "f08ee7c15ba9afced27b3b916126f5ac";

    public final static int RESULT_DESTROYED_WEIBO = 2;

    public final static String STATUS_BEAN = "status_bean";
    public final static String STATUS_BEAN_POSITION = "bean_position";

    public final static String TEMP_IMAGE_NAME = "tmp.png";
    public final static String TEMP_IMAGE_PATH = Environment.getExternalStorageDirectory() + "/Android/data/com.joewoo.ontime/";

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
	public final static int GOT_FRIENDS_TIMELINE_INFO_FAIL = 16;
	public final static int GOT_FAVOURITE_CREATE_INFO = 17;
	public final static int GOT_COMMENTS_TO_ME_INFO = 18;
	public final static int GOT_COMMENTS_TO_ME_INFO_FAIL = 19;
	public final static int GOT_MENTIONS_INFO = 20;
	public final static int GOT_MENTIONS_INFO_FAIL = 21;
	public final static int GOT_REPLY_INFO = 22;
	public final static int GOT_UNREAD_COUNT_INFO = 23;
//	public final static int GOT_FRIENDS_TIMELINE_EXTRA_INFO = 24;
	public final static int GOT_SET_REMIND_COUNT_INFO = 25;
	public final static int GOT_SET_REMIND_COUNT_INFO_FAIL = 26;
	public final static int GOT_USER_TIMELINE_INFO = 27;
	public final static int GOT_USER_TIMELINE_INFO_FAIL = 28;
	public final static int GOT_FRIENDS_TIMELINE_ADD_INFO = 29;
	public final static int GOT_UNREAD_COUNT_INFO_FAIL = 30;
	public final static int GOT_COMMNETS_SHOW_INFO = 31;
	public final static int GOT_COMMNETS_SHOW_INFO_FAIL = 32;
	public final static int GOT_STATUSES_DESTROY_INFO = 33;
	public final static int GOT_STATUSES_DESTROY_INFO_FAIL = 34;
    public final static int GOT_REPOST_TIMELINE_INFO = 35;
    public final static int GOT_REPOST_TIMELINE_INFO_FAIL = 36;
    public final static int GOT_COMMENTS_MENTIONS_INFO = 37;
    public final static int GOT_COMMENTS_MENTIONS_INFO_FAIL = 38;
    public final static int GOT_FAVOURITE_CREATE_INFO_FAIL = 39;
    public final static int GOT_COMMENT_CREATE_INFO_FAIL = 40;
    public final static int GOT_REPOST_INFO_FAIL = 41;
    public final static int GOT_REPLY_INFO_FAIL = 42;
    public final static int GOT_FRIENDS_IDS_INFO = 43;
    public final static int GOT_FRIENDS_IDS_INFO_FAIL = 44;
    public final static int GOT_STATUSES_SHOW_INFO = 45;
    public final static int GOT_STATUSES_SHOW_INFO_FAIL = 46;
    public final static int GOT_USER_TIMELINE_ADD_INFO = 47;
    public final static int GOT_MENTIONS_ADD_INFO = 48;
    public final static int GOT_COMMENTS_MENTIONS_ADD_INFO = 49;
    public final static int GOT_COMMENTS_TO_ME_ADD_INFO = 50;
    public final static int GOT_COMMNETS_SHOW_ADD_INFO = 51;
    public final static int GOT_REPOST_TIMELINE_ADD_INFO = 52;


	public final static int ACT_GOT_PHOTO = 1;
	public final static int ACT_GOT_AT = 2;

	public final static String TAG = "OnTime --- ";
    public final static String LOG_DEVIDER = "=========================";

	public final static String KEY_AT_USER = "at_user";

	public final static String EXPIRES_IN = "expires_in";
	public final static String ACCESS_TOKEN = "access_token";
    public final static String COUNT = "count";
    public final static String MAX_ID = "max_id";
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
	public final static String RETWEETED_STATUS_UID = "retweeted_status_uid";
	public final static String RETWEETED_STATUS_SCREEN_NAME = "retweeted_status_screen_name";
	public final static String RETWEETED_STATUS_CREATED_AT = "retweeted_status_created_at";
	public final static String RETWEETED_STATUS_COMMENTS_COUNT = "retweeted_status_comments_count";
	public final static String RETWEETED_STATUS_REPOSTS_COUNT = "retweeted_status_reposts_count";
	public final static String RETWEETED_STATUS_THUMBNAIL_PIC = "retweeted_status_thumbnail_pic";
	public final static String RETWEETED_STATUS_BMIDDLE_PIC = "retweeted_status_bmiddle_pic";
	public final static String RETWEETED_STATUS_ORIGINAL_PIC = "retweeted_status_original_pic";
	public final static String RETWEETED_STATUS_PIC_URLS = "retweeted_status_pic_urls";
	public final static String RETWEETED_STATUS_SOURCE = "retweeted_status_source";
	public final static String WEIBO_ID = "id";
    public final static String REPOST_WEIBO_ID = "repost_weibo_id";
	public final static String COMMENT_ID = "comment_id";
	public final static String IS_COMMENT = "isComment";
	public final static String HAVE_PIC = "havePic";
	public final static String RETWEETED_STATUS_HAVE_PIC = "retweeted_status_havePic";
	public final static String IS_REPOST = "isRepost";
	public final static String STATUS_TEXT = "status_text";
    public final static String STATUS_SOURCE = "status_source";
	public final static String STATUS_CREATED_AT = "status_created_at";
	public final static String STATUS_COMMENTS_COUNT = "status_comments_count";
	public final static String STATUS_REPOSTS_COUNT = "status_reposts_count";
	public final static String STATUS_USER_SCREEN_NAME = "status_user_screen_name";
    public final static String STATUS_PROFILE_IMAGE_URL = "status_profile_image";
    public final static String STATUS_THUMBNAIL_PIC = "status_thumbnail_pic";
    public final static String STATUS_BMIDDLE_PIC = "status_bmiddle_pic";
    public final static String REPLY_COMMNET_TEXT = "reply_comment_text";
    public final static String REPLY_COMMNET_USER_SCREEN_NAME = "reply_comment_user_screen_name";
	public final static String IS_REPLY = "isReply";
	public final static String THUMBNAIL_PIC = "thumbnail_pic";
	public final static String BMIDDLE_PIC = "bmiddle_pic";
	public final static String ORIGINAL_PIC = "original_pic";
	public final static String PIC_URLS = "pic_urls";
	public final static String IS_FRAG_POST = "isFragPost";
	public final static String PROFILE_IMAGE = "profile_image";
	public final static String USER_WEIBO = "user_weibo";
    public final static String BLANK = "blank";
	
	public final static String PROFILE_IMAGE_URL = "profile_image_url";

	public final static String[] dayNames = { "星期几", "星期日", "星期一", "星期二",
			"星期三", "星期四", "星期五", "星期六" };

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
	public final static int MENU_REFRESH = 11;
	public final static int MENU_FAVOURITE_CREATE = 12;
	public final static int MENU_UNREAD_COUNT = 13;
	public final static int MENU_REPOST = 14;
	public final static int MENU_COMMENT_CREATE = 15;
	public final static int MENU_PROFILE_IMAGE = 16;
	public final static int MENU_FOLLOWERS_COUNT = 17;
	public final static int MENU_FRIENDS_COUNT = 18;
	public final static int MENU_STATUSES_COUNT = 19;
	public final static int MENU_STATUSES_DESTROY = 20;
    public final static int MENU_COPY_TEXT = 21;

    public final static String LOGIN_FROM_POST = "login_from_post";
}
