package com.joewoo.ontime.support.bean.aqi;

import java.util.List;

/**
 * Created by JoeWoo on 14-1-6.
 */
public class AQIDetailsBean {

    private List<AQIBean> aqi;
    private String error;

    public List<AQIBean> getAQIs() {
        return aqi;
    }

    public String getError() {
        return error;
    }
}
