package com.joewoo.ontime.support.error;

import android.util.Log;

import com.google.gson.Gson;
import com.joewoo.ontime.R;
import com.joewoo.ontime.support.bean.ErrorBean;
import com.joewoo.ontime.support.util.GlobalContext;

import static com.joewoo.ontime.support.info.Defines.TAG;

/**
 * Created by JoeWoo on 13-11-12.
 */
public class ErrorCheck {

    public static String getError(String httpResult) {
        int code;
        try {
            code = Integer.valueOf(getErrorCode(httpResult));
            return matchError(code);
        } catch (Exception e) {
            return null;
        }

    }

    public static String getError(ErrorBean bean) {
        int code;
        try {
            code = Integer.valueOf(bean.getErrorCode());
            return matchError(code);
        } catch (Exception e) {
            return null;
        }
    }

    private static String getErrorCode(String httpResult) {
        try {
            return getErrorBean(httpResult).getErrorCode();
        } catch (Exception e) {
            return null;
        }
    }

    private static ErrorBean getErrorBean(String httpResult) {
        try {
            return new Gson().fromJson(httpResult, ErrorBean.class);
        } catch (Exception e) {
            return null;
        }
    }

    private static String matchError(int code){
        Log.e(TAG, "Error code: " + String.valueOf(code));
        if (code > 0) {
            switch (code) {

                // 认证过期、不合法
                case 21327:
                case 21319:
                case 21314:
                case 21315:
                case 21316:
                case 21317:
                    return GlobalContext.getResString(R.string.error_token_problem);

                // 认证失败
                case 21301:
                    return GlobalContext.getResString(R.string.error_auth_fail);

                // 用户名或密码错误
                case 21302:
                case 21303:
                    return GlobalContext.getResString(R.string.error_user_passwork_error);

                // 用户不存在
                case 20003:
                    return GlobalContext.getResString(R.string.error_user_not_exist);

                // 上传图片太大
                case 20006:
                    return GlobalContext.getResString(R.string.error_img_too_large);

                // 内容为空
                case 20008:
                    return GlobalContext.getResString(R.string.error_content_is_null);

                // 文字太长
                case 20012:
                case 20013:
                    return GlobalContext.getResString(R.string.error_text_too_long);

                // 重复内容（评论、微博内容、微博内容）
                case 20019:
                case 20017:
                case 20111:
                    return GlobalContext.getResString(R.string.error_repeat_content);

                // 成功发布，不过要等待
                case 20032:
                    return GlobalContext.getResString(R.string.error_update_success_but_wait);

                // 微博评论不存在
                case 20201:
                case 20202:
                    return GlobalContext.getResString(R.string.error_comment_not_exist);

                // 微博不存在
                case 20101:
                    return GlobalContext.getResString(R.string.error_weibo_not_exist);

                // 黑名单
                case 20205:
                    return GlobalContext.getResString(R.string.error_in_block);

                case 20704:
                    return GlobalContext.getResString(R.string.error_favourited);

                default:
                    return GlobalContext.getResString(R.string.error_unknown);
            }
        } else {
            return null;
        }

    }
}
