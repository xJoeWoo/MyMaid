package com.joewoo.ontime.bean;

import java.util.List;

public class UserTimeLineBean {

	private List<StatusesBean> statuses;
	private String total_number;

	public List<StatusesBean> getStatuses(){
		return statuses;
	}
	public String getTotalNumber(){
		return total_number;
	}

}