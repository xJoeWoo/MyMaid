package com.joewoo.ontime.support.bean;

import java.util.List;

public class FriendsTimelineBean {
	
	private List<StatusesBean> statuses;
    private List<AdBean> ad;
	private String total_number;

	public List<StatusesBean> getStatuses(){
		return statuses;
	}
    public List<AdBean> getAd() {
        return ad;
    }
	public String getTotalNumber(){
		return total_number;
	}

}
