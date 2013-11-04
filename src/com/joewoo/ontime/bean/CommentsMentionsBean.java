package com.joewoo.ontime.bean;

import java.util.List;

public class CommentsMentionsBean {
	
	private List<CommentsBean> comments;
	private String total_number;
	
	public List<CommentsBean> getComments(){
		return comments;
	}
	public String getTotalNumber(){
		return total_number;
	}
}
