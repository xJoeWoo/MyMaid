package com.joewoo.ontime.support.service;

import android.content.Intent;

import com.joewoo.ontime.support.util.GlobalContext;

import static com.joewoo.ontime.support.info.Defines.COMMENT;
import static com.joewoo.ontime.support.info.Defines.COMMENT_ID;
import static com.joewoo.ontime.support.info.Defines.FILE_PATH;
import static com.joewoo.ontime.support.info.Defines.STATUS;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;

/**
 * Created by JoeWoo on 14-1-7.
 */
public class MyMaidServiceHelper {

    public static void update(String status) {
        Intent i = new Intent(GlobalContext.getAppContext(), UpdateService.class);
        i.putExtra(STATUS, status);
        GlobalContext.getAppContext().startService(i);
    }

    public static void upload(String status) {
        Intent i = new Intent(GlobalContext.getAppContext(), UploadService.class);
        i.putExtra(STATUS, status);
        i.putExtra(FILE_PATH, GlobalContext.getPicPath());
        GlobalContext.getAppContext().startService(i);
    }

    public static void reply(String comment, String weiboID, String commentID) {
        Intent i = new Intent(GlobalContext.getAppContext(), ReplyService.class);
        i.putExtra(COMMENT_ID, commentID);
        i.putExtra(WEIBO_ID, weiboID);
        i.putExtra(COMMENT, comment);
        GlobalContext.getAppContext().startService(i);
    }

    public static void repost(String status, String weiboID) {
        Intent i = new Intent(GlobalContext.getAppContext(), RepostService.class);
        i.putExtra(STATUS, status);
        i.putExtra(WEIBO_ID, weiboID);
        GlobalContext.getAppContext().startService(i);
    }

    public static void commentCreate(String comment, String weiboID) {
        Intent i = new Intent(GlobalContext.getAppContext(), CommentCreateService.class);
        i.putExtra(WEIBO_ID, weiboID);
        i.putExtra(COMMENT, comment);
        GlobalContext.getAppContext().startService(i);
    }

}
