package com.joewoo.ontime.support.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class StatusesBean implements Parcelable{
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

    public void setCreatedAt(String createdAt) { this.created_at = createdAt; }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) { this.source = source; }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(created_at);
        dest.writeString(id);
        dest.writeString(text);
        dest.writeString(source);
        dest.writeByte((byte) (favorited ? 1 : 0));
        dest.writeValue(retweeted_status);
        dest.writeString(reposts_count);
        dest.writeString(comments_count);
        dest.writeValue(user);
        dest.writeString(thumbnail_pic);
        dest.writeString(bmiddle_pic);
        dest.writeString(original_pic);
        dest.writeList(pic_urls);
    }

    public static final Creator<StatusesBean> CREATOR = new Creator<StatusesBean>() {
        @Override
        public StatusesBean createFromParcel(Parcel source) {
            return new StatusesBean(source);
        }

        @Override
        public StatusesBean[] newArray(int size) {
            return new StatusesBean[size];
        }
    };

    public StatusesBean(Parcel parcel) {
        created_at = parcel.readString();
        id = parcel.readString();
        text = parcel.readString();
        source = parcel.readString();
        favorited = parcel.readByte() != 0;
        retweeted_status = (StatusesBean) parcel.readValue(StatusesBean.class.getClassLoader());
        reposts_count = parcel.readString();
        comments_count = parcel.readString();
        user = (UserBean) parcel.readValue(UserBean.class.getClassLoader());
        thumbnail_pic = parcel.readString();
        bmiddle_pic = parcel.readString();
        original_pic = parcel.readString();
        pic_urls = new ArrayList<>();
        parcel.readList(pic_urls, PicURLsBean.class.getClassLoader());
    }
}
