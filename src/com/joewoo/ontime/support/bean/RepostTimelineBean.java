package com.joewoo.ontime.support.bean;

import java.util.List;

/**
 * Created by JoeWoo on 13-10-19.
 * OOOOOH I HAVE GRADE 3 IN SENIOR HIGH!!!
 */
public class RepostTimelineBean {

    private List<StatusesBean> reposts;
    private int total_number;

    public List<StatusesBean> getReposts(){
        return reposts;
    }
    public int getTotalNumber(){
        return total_number;
    }

}
