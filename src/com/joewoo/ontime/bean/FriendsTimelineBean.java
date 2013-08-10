package com.joewoo.ontime.bean;

public class FriendsTimelineBean {

	private String statuses;
	private String id;
	private String text;
	private String reposts_count;
	private String comments_count;
	private String thumbnail_pic;

	private String total_number;
	
	public String getStatuses(){
		return statuses;
	}
	public String getId(){
		return id;
	}
	public String getText(){
		return text;
	}
	public String getRepostsCount(){
		return reposts_count;
	}
	public String getCommentsCount(){
		return comments_count;
	}
	public String getThumbnailPic(){
		return thumbnail_pic;
	}

	public String getTotalNumber(){
		return total_number;
	}
}
