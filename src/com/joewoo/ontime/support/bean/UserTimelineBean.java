package com.joewoo.ontime.support.bean;

import java.util.List;

public class UserTimelineBean {

	private List<StatusesBean> statuses;
	private String total_number;

	public List<StatusesBean> getStatuses(){
		return statuses;
	}
	public String getTotalNumber(){
		return total_number;
	}

}
