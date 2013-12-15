package com.joewoo.ontime.action.remind;

import android.util.Log;

import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.net.HttpUtility;
import com.joewoo.ontime.support.util.GlobalContext;

import java.util.HashMap;

import static com.joewoo.ontime.support.info.Defines.ACCESS_TOKEN;
import static com.joewoo.ontime.support.info.Defines.TAG;

public class RemindSetCount extends Thread {

    public final static String CommentsCount = "cmt";
    public final static String MentionsCount = "mention_status";
    public final static String CommentMentionsCount = "mention_cmt";
    public final static String FollowersCount = "follower";

    private String type;

    public RemindSetCount(String type) {
        this.type = type;
    }

    @Override
    public void run() {

        Log.e(TAG, "Set Remind Count AsycnTask start");
        Log.e(TAG, "type: " + type);

        String httpResult;

        try {
            HashMap<String, String> hm = new HashMap<String, String>();

            hm.put(ACCESS_TOKEN, GlobalContext.getAccessToken());
            hm.put("type", type);

            httpResult = new HttpUtility().executePostTask(URLHelper.SET_REMIND_COUNT, hm);
            //        if(result != null && ErrorCheck.getError(result) != null)
//            Toast.makeText(GlobalContext.getAppContext(), ErrorCheck.getError(result), Toast.LENGTH_SHORT).show();
//        else if(result == null)
//            Toast.makeText(GlobalContext.getAppContext(), GlobalContext.getResString(R.string.toast_mentions_fail), Toast.LENGTH_SHORT).show();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
