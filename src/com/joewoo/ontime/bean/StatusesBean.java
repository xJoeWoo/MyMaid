package com.joewoo.ontime.bean;

public class StatusesBean {
	private String created_at;
	private String id;
	private String text;
	private String source;
	private boolean favorited;
	private StatusesBean retweeted_status;
	private String reposts_count;
	private String comments_count;
	private UserBean user;
	private String thumbnail_pic;
	private String bmiddle_pic;
	private String original_pic;

	public String getCreatedAt() {
		return created_at;
	}

	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public String getSource() {
		return source;
	}

	public StatusesBean getRetweetedStatus() {
		if (retweeted_status != null)
			return retweeted_status;
		else
			return null;
	}

	public boolean isFavorited() {
		return favorited;
	}

	public String getRepostsCount() {
		return reposts_count;
	}

	public String getCommentsCount() {
		return comments_count;
	}

	public UserBean getUser() {
		return user;
	}

	public String getThumbnailPic() {
		return thumbnail_pic;
	}

	public String getBmiddlePic() {
		return bmiddle_pic;
	}

	public String getOriginalPic() {
		return original_pic;
	}

}
