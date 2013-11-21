package com.joewoo.ontime.support.bean;

public class CommentsBean {
	private String created_at;
	private String id;
	private String text;
	private String source;
	private UserBean user;
	private StatusesBean status;
	private CommentsBean reply_comment;
	
	public String getCreatedAt(){
		return created_at;
	}
	public String getId(){
		return id;
	}
	public String getText(){
		return text;
	}
	public String getSource(){
		return source;
	}
	public UserBean getUser(){
		return user;
	}
	public StatusesBean getStatus(){
		return status;
	}
	public CommentsBean getReplyComment(){
		return reply_comment;
	}
}
