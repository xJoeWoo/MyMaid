package com.joewoo.ontime.bean;

public class WeiboBackBean {
	//Access Token
	private String access_token;
	private long remind_in;
	private long expires_in;
	private String uid;
	
	//Show
	private String screen_name;
	private String profile_image_url;
	private String location;
	private int followers_count;
	private int friends_count;
	private int statuses_count;
	
	//Create, Upload
	private String id;
	private String created_at;
	private String text;
	
	//Upload
	private String original_pic;
	
	//Error
	private String error;
	private String error_code;
	
	
	
	//Access Token
	public String getAccessToken()
	{
		return access_token;
	}
	public long getRemindIn()
	{
		return remind_in;
	}
	public long getExpiresIn()
	{
		return expires_in;
	}
	public String getUid()
	{
		return uid;
	}
	
	//Show
	public String getScreenName(){
		return screen_name;
	}
	public String getProfileImageUrl(){
		return profile_image_url;
	}
	public String getLocation() {
		location = location.replace(" ", "");
		return location;
	}
	public int getFollowersCount() {
		return followers_count;
	}
	public int getFriendsCount() {
		return friends_count;
	}
	public int getStatusesCount() {
		return statuses_count;
	}
	
	//Create, Upload
	public String getId(){
		return id;
	}
	public String getCreatedAt(){
		return created_at;
	}
	public String getText(){
		return text;
	}
	
	//Upload
	public String getOriginalPic(){
		return original_pic;
	}
	
	//Error
	public String getError(){
		return error;
	}
	public String getErrorCode(){
		return error_code;
	}
}
