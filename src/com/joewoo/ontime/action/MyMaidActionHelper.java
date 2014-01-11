package com.joewoo.ontime.action;

import android.os.Handler;

import com.joewoo.ontime.action.comments.CommentsCreate;
import com.joewoo.ontime.action.comments.CommentsMentions;
import com.joewoo.ontime.action.comments.CommentsReply;
import com.joewoo.ontime.action.comments.CommentsToMe;
import com.joewoo.ontime.action.remind.RemindSetCount;
import com.joewoo.ontime.action.remind.RemindUnreadCount;
import com.joewoo.ontime.action.statuses.StatusesFriendsTimeLine;
import com.joewoo.ontime.action.statuses.StatusesMentions;
import com.joewoo.ontime.action.statuses.StatusesRepost;
import com.joewoo.ontime.action.statuses.StatusesUpdate;
import com.joewoo.ontime.action.statuses.StatusesUpload;
import com.joewoo.ontime.action.statuses.StatusesUserTimeLine;
import com.joewoo.ontime.support.net.ImageNetworkListener;
import com.joewoo.ontime.support.net.ProfileImage;

/**
 * Created by JoeWoo on 14-1-8.
 */
public class MyMaidActionHelper {

    public static Thread commentsReply(String comment, String weiboID, String commentID, boolean commentOri, Handler handler) {
        Thread thread = new CommentsReply(comment, weiboID, commentID, commentOri, handler);
        thread.start();
        return thread;
    }

    public static Thread statusesUpload(String status, String filePath, ImageNetworkListener.UploadProgressListener listener, Handler handler) {
        Thread thread = new StatusesUpload(status, filePath, listener, handler);
        thread.start();
        return thread;
    }

    public static Thread statusesUpdate(String status, Handler handler) {
        Thread thread = new StatusesUpdate(status, handler);
        thread.start();
        return thread;
    }

    public static Thread statusesRepost(String status, String weiboID, Handler handler) {
        Thread thread = new StatusesRepost(status, weiboID, handler);
        thread.start();
        return thread;
    }

    public static Thread commentsCreate(String comment, String weiboID, Handler handler) {
        Thread thread = new CommentsCreate(comment, weiboID, handler);
        thread.start();
        return thread;
    }

    public static Thread statusUserTimeLine(String screenName, Handler handler) {
        Thread thread = new StatusesUserTimeLine(screenName, handler);
        thread.start();
        return thread;
    }

    public static Thread statusUserTimeLine(String screenName, String maxID, Handler handler) {
        Thread thread = new StatusesUserTimeLine(screenName, maxID ,handler);
        thread.start();
        return thread;
    }

    public static Thread profileImage(String url, Handler handler) {
        Thread thread = new ProfileImage(url, handler);
        thread.start();
        return thread;
    }

    public static Thread commentsToMe(boolean isProvided, Handler handler) {
        Thread thread = new CommentsToMe(isProvided, handler);
        thread.start();
        return thread;
    }

    public static Thread commentsToMe(String maxID, Handler handler) {
        Thread thread = new CommentsToMe(maxID, handler);
        thread.start();
        return thread;
    }

    public static Thread remindUnreadCount(Handler handler) {
        Thread thread = new RemindUnreadCount(handler);
        thread.start();
        return thread;
    }

    public static Thread remindSetCount(String type) {
        Thread thread = new RemindSetCount(type);
        thread.start();
        return thread;
    }

    public static Thread statusesFriendsTimeLine(String maxID, Handler handler) {
        Thread thread = new StatusesFriendsTimeLine(maxID, handler);
        thread.start();
        return thread;
    }

    public static Thread statusesFriendsTimeLine(boolean isProvided, Handler handler) {
        Thread thread = new StatusesFriendsTimeLine(isProvided, handler);
        thread.start();
        return thread;
    }

    public static Thread statusesMentions(boolean isProvided, Handler handler) {
        Thread thread = new StatusesMentions(isProvided, handler);
        thread.start();
        return thread;
    }

    public static Thread statusesMentions(String maxID, Handler handler) {
        Thread thread = new StatusesMentions(maxID, handler);
        thread.start();
        return thread;
    }

    public static Thread commentsMentions(String maxID, Handler handler) {
        Thread thread = new CommentsMentions(maxID, handler);
        thread.start();
        return thread;
    }

    public static Thread commentsMentions(boolean isProvided, Handler handler) {
        Thread thread = new CommentsMentions(isProvided, handler);
        thread.start();
        return thread;
    }

}
