package com.joewoo.ontime.bean;

public class UnreadCountBean {
	private String status;
	private String follower;
	private String cmt;
	private String mention_status;
	private String mention_cmt;
	private String dm;
	
	public String getStatusCount(){
		return status;
	}
	public String getFollowerCount(){
		return follower;
	}
	public String getCmtCount(){
		return cmt;
	}
	public String getMentionStatusCount(){
		return mention_status;
	}
	public String getMentionCmtCount(){
		return mention_cmt;
	}
	public String getDmCount(){
		return dm;
	}
	
}
