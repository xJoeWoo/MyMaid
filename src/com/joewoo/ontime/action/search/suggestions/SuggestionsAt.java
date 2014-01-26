package com.joewoo.ontime.action.search.suggestions;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.joewoo.ontime.R;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.bean.AtSuggestionBean;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.util.GlobalContext;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import static com.joewoo.ontime.support.info.Defines.ACCESS_TOKEN;
import static com.joewoo.ontime.support.info.Defines.GOT_AT_SUGGESTIONS_INFO;
import static com.joewoo.ontime.support.info.Defines.TAG;

public class SuggestionsAt extends Thread {

    private String user;
    private Handler mHandler;
    private Activity act;

    public SuggestionsAt(String user, Handler handler, Activity act) {
        this.user = user;
        this.mHandler = handler;
        this.act = act;
    }

    public void run() {
        Log.e(TAG, "At User Suggestions Thread START");
        String httpResult;

        try {

            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());
            hm.put("q", user);
            hm.put("type", "0");

            httpResult = new HttpUtility().executeGetTask(URLHelper.AT_SUGGESTIONS, hm);

            hm = null;

            Type listType = new TypeToken<List<AtSuggestionBean>>() {
            }.getType();

            List<AtSuggestionBean> events = new Gson().fromJson(httpResult,
                    listType);

            ArrayAdapter<AtSuggestionBean> files = new ArrayAdapter<AtSuggestionBean>(
                    act, R.layout.lv_at, R.id.lv_tv1, events);

            mHandler.obtainMessage(GOT_AT_SUGGESTIONS_INFO, files)
                    .sendToTarget();


        } catch (Exception e) {
            Log.e(TAG, "At User Suggesstions FAILED");
            e.printStackTrace();
        }

    }

}
