package com.joewoo.ontime.support.bean;

public class AtSuggestionBean {
	private String uid;
	private String nickname;
	
	public String getUid(){
		return uid;
	}
	public String getNickname(){
		return nickname;
	}
	
	@Override
	public String toString(){
		return nickname;
	}
}
