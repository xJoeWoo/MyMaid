package com.joewoo.ontime.support.bean;

import java.util.List;

/**
 * Created by JoeWoo on 13-10-19.
 * OOOOOH I HAVE GRADE 3 IN SENIOR HIGH!!!
 */
public class RepostTimelineBean {

    private List<StatusesBean> reposts;
    private String total_number;

    public List<StatusesBean> getReposts(){
        return reposts;
    }
    public String getTotalNumber(){
        return total_number;
    }

}
