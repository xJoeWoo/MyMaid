package com.joewoo.ontime.support.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class UserBean implements Parcelable{

    private String id;
    private String screen_name;
    private String location;
    private String profile_image_url;
    private String followers_count;
    private String friends_count;
    private String statuses_count;
    private String favourites_count;
    private String description;
    private boolean follow_me;
    private String avatar_large;
    private String avatar_hd;

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getFollowersCount() {
        return followers_count;
    }

    public String getFriendsCount() {
        return friends_count;
    }

    public String getStatusesCount() {
        return statuses_count;
    }

    public String getFavouritesCount() {
        return favourites_count;
    }

    public boolean isFollowMe() {
        return follow_me;
    }

    public String getAvatarLarge() {
        return avatar_large;
    }

    public String getAvatarHD() {
        return avatar_hd;
    }

    public String getScreenName() {
        return screen_name;
    }

    public String getProfileImageUrl() {
        return profile_image_url;
    }

    public UserBean(Parcel parcel) {
        id = parcel.readString();
        screen_name = parcel.readString();
        location = parcel.readString();
        profile_image_url = parcel.readString();
        followers_count = parcel.readString();
        friends_count = parcel.readString();
        statuses_count = parcel.readString();
        favourites_count = parcel.readString();
        description = parcel.readString();
        follow_me = parcel.readByte() != 0;
        avatar_large = parcel.readString();
        avatar_hd = parcel.readString();
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel source) {
            return new UserBean(source);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(screen_name);
        dest.writeString(location);
        dest.writeString(profile_image_url);
        dest.writeString(followers_count);
        dest.writeString(friends_count);
        dest.writeString(statuses_count);
        dest.writeString(favourites_count);
        dest.writeString(description);
        dest.writeByte((byte) (follow_me ? 1 : 0));
        dest.writeString(avatar_large);
        dest.writeString(avatar_hd);
    }
}
