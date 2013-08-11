package com.joewoo.ontime.bean;

public class UserBean {

	private String id;
	private String screen_name;
	private String location;
	private String profile_image_url;
	private String followers_count;
	private String friends_count;
	private String statuses_count;
	private String favourites_count;
	private boolean follow_me;
	private String avatar_large;
	
	public String getId(){
		return id;
	}
	public String getLoaction(){
		return location;
	}
	public String getFollowersCount(){
		return followers_count;
	}
	public String getFriendsCount(){
		return friends_count;
	}
	public String getStatusesCount(){
		return statuses_count;
	}
	
	public String getFavouritesCount(){
		return favourites_count;
	}
	public boolean isFollowMe(){
		return follow_me;
	}
	public String getAvatarLarge(){
		return avatar_large;
	}
	public String getScreenName(){
		return screen_name;
	}
	public String getProfileImageUrl(){
		return profile_image_url;
	}
	
}
