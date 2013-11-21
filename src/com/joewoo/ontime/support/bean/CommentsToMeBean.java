package com.joewoo.ontime.support.bean;

import java.util.List;

public class CommentsToMeBean {
	
	private List<CommentsBean> comments;
	private String total_number;
	
	public List<CommentsBean> getComments(){
		return comments;
	}
	public String getTotalNumber(){
		return total_number;
	}
}
