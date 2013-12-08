package com.joewoo.ontime.action.favorites;

import android.os.Handler;
import android.util.Log;

import com.joewoo.ontime.R;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.error.ErrorCheck;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.util.GlobalContext;

import java.util.HashMap;

import static com.joewoo.ontime.support.info.Defines.ACCESS_TOKEN;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENT_CREATE_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_COMMENT_CREATE_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.GOT_FAVOURITE_CREATE_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_FAVOURITE_CREATE_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;

public class FavoritesCreate extends Thread {

    private String weiboID;
    private Handler mHandler;

    public FavoritesCreate(String weiboID, Handler handler) {
        this.weiboID = weiboID;
        this.mHandler = handler;
    }

    public void run() {
        Log.e(TAG, "Favourite Create Thread START");
        String httpResult;

        try {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(WEIBO_ID, weiboID);
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());

            httpResult = new HttpUtility().executePostTask(URLHelper.FAVOURITE_CREATE, hm);

            hm = null;

        } catch (Exception e) {
            e.printStackTrace();
            mHandler.obtainMessage(GOT_FAVOURITE_CREATE_INFO_FAIL, GlobalContext.getAppContext().getString(R.string.toast_add_favourite_fail)).sendToTarget();
            return;
        }

        if(ErrorCheck.getError(httpResult) == null)
            mHandler.obtainMessage(GOT_FAVOURITE_CREATE_INFO, GlobalContext.getAppContext().getString(R.string.toast_add_favourite_success)).sendToTarget();
        else
            mHandler.obtainMessage(GOT_FAVOURITE_CREATE_INFO_FAIL, ErrorCheck.getError(httpResult)).sendToTarget();

    }

}
