package com.joewoo.ontime.support.bean;

import java.util.List;

public class CommentsToMeBean {
	
	private List<CommentsBean> comments;
	private int total_number;
	
	public List<CommentsBean> getComments(){
		return comments;
	}
	public int getTotalNumber(){
		return total_number;
	}
}
