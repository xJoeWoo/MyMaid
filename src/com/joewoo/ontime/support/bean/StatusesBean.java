package com.joewoo.ontime.support.bean;

import java.util.List;

public class StatusesBean {
    private String created_at;
    private String id;
    private String text;
    private String source;
    private boolean favorited;
    private StatusesBean retweeted_status;
    private String reposts_count;
    private String comments_count;
    private UserBean user;
    private String thumbnail_pic;
    private String bmiddle_pic;
    private String original_pic;
    private List<PicURLsBean> pic_urls;

    public String getCreatedAt() {
        return created_at;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getSource() {
        return source;
    }

    public StatusesBean getRetweetedStatus() {

        return retweeted_status;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public String getRepostsCount() {
        return reposts_count;
    }

    public String getCommentsCount() {
        return comments_count;
    }

    public UserBean getUser() {
        return user;
    }

    public String getThumbnailPic() {
        return thumbnail_pic;
    }

    public String getBmiddlePic() {
        return bmiddle_pic;
    }

    public String getOriginalPic() {
        return original_pic;
    }

    public List<PicURLsBean> getPicURLs(){
        return pic_urls;
    }

}
