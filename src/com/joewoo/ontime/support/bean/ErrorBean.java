package com.joewoo.ontime.support.bean;

/**
 * Created by JoeWoo on 13-11-12.
 */
public class ErrorBean {

    private String error;
    private String error_code;
    private String request;

    public String getError(){
        return error;
    }
    public String getErrorCode(){
        return error_code;
    }
    public String getRequest(){
        return request;
    }
}
