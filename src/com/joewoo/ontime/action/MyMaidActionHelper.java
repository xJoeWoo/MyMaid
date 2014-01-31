package com.joewoo.ontime.action;

import android.app.Activity;
import android.os.Handler;

import com.joewoo.ontime.action.comments.CommentsCreate;
import com.joewoo.ontime.action.comments.CommentsMentions;
import com.joewoo.ontime.action.comments.CommentsReply;
import com.joewoo.ontime.action.comments.CommentsShow;
import com.joewoo.ontime.action.comments.CommentsToMe;
import com.joewoo.ontime.action.favorites.FavoritesCreate;
import com.joewoo.ontime.action.remind.RemindSetCount;
import com.joewoo.ontime.action.remind.RemindUnreadCount;
import com.joewoo.ontime.action.search.suggestions.SuggestionsAt;
import com.joewoo.ontime.action.statuses.StatusesDestroy;
import com.joewoo.ontime.action.statuses.StatusesFriendsTimeLine;
import com.joewoo.ontime.action.statuses.StatusesMentions;
import com.joewoo.ontime.action.statuses.StatusesRepost;
import com.joewoo.ontime.action.statuses.StatusesRepostTimeline;
import com.joewoo.ontime.action.statuses.StatusesShow;
import com.joewoo.ontime.action.statuses.StatusesUpdate;
import com.joewoo.ontime.action.statuses.StatusesUpload;
import com.joewoo.ontime.action.statuses.StatusesUserTimeLine;
import com.joewoo.ontime.support.listener.MyMaidListeners;
import com.joewoo.ontime.support.net.ProfileImage;

/**
 * Created by JoeWoo on 14-1-8.
 */
public class MyMaidActionHelper {

    public static Thread commentsReply(String comment, String weiboID, String commentID, boolean commentOri, Handler handler) {
        Thread thread = new CommentsReply(comment, weiboID, commentID, commentOri, handler);
        return startThread(thread);
    }

    public static Thread statusesUpload(String status, String filePath, MyMaidListeners.UploadProgressListener listener, Handler handler) {
        Thread thread = new StatusesUpload(status, filePath, listener, handler);
        return startThread(thread);
    }

    public static Thread statusesUpdate(String status, Handler handler) {
        Thread thread = new StatusesUpdate(status, handler);
        return startThread(thread);
    }

    public static Thread statusesRepost(String status, String weiboID, Handler handler) {
        Thread thread = new StatusesRepost(status, weiboID, handler);
        return startThread(thread);
    }

    public static Thread commentsCreate(String comment, String weiboID, Handler handler) {
        Thread thread = new CommentsCreate(comment, weiboID, handler);
        return startThread(thread);
    }

    public static Thread statusUserTimeLine(String screenName, Handler handler) {
        Thread thread = new StatusesUserTimeLine(screenName, handler);
        return startThread(thread);
    }

    public static Thread statusUserTimeLine(String screenName, String maxID, Handler handler) {
        Thread thread = new StatusesUserTimeLine(screenName, maxID, handler);
        return startThread(thread);
    }

    public static Thread profileImage(String url, Handler handler) {
        Thread thread = new ProfileImage(url, handler);
        return startThread(thread);
    }

    public static Thread commentsToMe(boolean isProvided, Handler handler) {
        Thread thread = new CommentsToMe(isProvided, handler);
        return startThread(thread);
    }

    public static Thread commentsToMe(String maxID, Handler handler) {
        Thread thread = new CommentsToMe(maxID, handler);
        return startThread(thread);
    }

    public static Thread remindUnreadCount(Handler handler) {
        Thread thread = new RemindUnreadCount(handler);
        return startThread(thread);
    }

    public static Thread remindSetCount(String type) {
        Thread thread = new RemindSetCount(type);
        return startThread(thread);
    }

    public static Thread statusesFriendsTimeLine(String maxID, Handler handler) {
        Thread thread = new StatusesFriendsTimeLine(maxID, handler);
        return startThread(thread);
    }

    public static Thread statusesFriendsTimeLine(boolean isProvided, Handler handler) {
        Thread thread = new StatusesFriendsTimeLine(isProvided, handler);
        return startThread(thread);
    }

    public static Thread statusesMentions(boolean isProvided, Handler handler) {
        Thread thread = new StatusesMentions(isProvided, handler);
        return startThread(thread);
    }

    public static Thread statusesMentions(String maxID, Handler handler) {
        Thread thread = new StatusesMentions(maxID, handler);
        return startThread(thread);
    }

    public static Thread commentsMentions(String maxID, Handler handler) {
        Thread thread = new CommentsMentions(maxID, handler);
        return startThread(thread);
    }

    public static Thread commentsMentions(boolean isProvided, Handler handler) {
        Thread thread = new CommentsMentions(isProvided, handler);
        return startThread(thread);
    }

    public static Thread statusesShow(String weiboID, Handler handler) {
        Thread thread = new StatusesShow(weiboID, handler);
        return startThread(thread);
    }

    public static Thread favouriteCreate(String weiboID, Handler handler) {
        Thread thread = new FavoritesCreate(weiboID, handler);
        return startThread(thread);
    }

    public static Thread statusesDestroy(String weiboID, Handler handler) {
        Thread thread = new StatusesDestroy(weiboID, handler);
        return startThread(thread);
    }

    public static Thread commentsShow(String weiboID, Handler handler) {
        Thread thread = new CommentsShow(weiboID, handler);
        return startThread(thread);
    }

    public static Thread commentsShow(String weiboID, String maxID, Handler handler) {
        Thread thread = new CommentsShow(weiboID, maxID, handler);
        return startThread(thread);
    }

    public static Thread statusesRepostTimeline(String weiboID, String maxID, Handler handler) {
        Thread thread = new StatusesRepostTimeline(weiboID, maxID, handler);
        return startThread(thread);
    }

    public static Thread statusesRepostTimeline(String weiboID, Handler handler) {
        Thread thread = new StatusesRepostTimeline(weiboID, handler);
        return startThread(thread);
    }

    public static Thread suggestionsAt(String user, Handler handler, Activity act) {
        Thread thread = new SuggestionsAt(user, handler, act);
        return startThread(thread);
    }

    private static Thread startThread(Thread thread) {
        thread.start();
        return thread;
    }

}
