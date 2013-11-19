package com.joewoo.ontime.tools;

import com.google.gson.Gson;
import com.joewoo.ontime.bean.ErrorBean;

/**
 * Created by JoeWoo on 13-11-12.
 */
public class Weibo_Errors {

    public static String getError(String httpResult) {
        try {
            return new Gson().fromJson(httpResult, ErrorBean.class).getError();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getErrorCode(String httpResult) {
        try {
            return new Gson().fromJson(httpResult, ErrorBean.class).getErrorCode();
        } catch (Exception e) {
            return null;
        }
    }

    public static ErrorBean getErrorBean(String httpResult) {
        ErrorBean err;
        try {
            err = new Gson().fromJson(httpResult, ErrorBean.class);
            if (err.getError() == null)
                return null;
            else
                return err;
        } catch (Exception e) {
            return null;
        }
    }
}
