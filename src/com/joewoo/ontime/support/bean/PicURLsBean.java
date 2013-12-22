package com.joewoo.ontime.support.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JoeWoo on 13-11-10.
 */
public class PicURLsBean implements Parcelable {
    private String thumbnail_pic;

    public String getThumbnailPic(){
        return thumbnail_pic;
    }

    public PicURLsBean(Parcel parcel) {
        thumbnail_pic = parcel.readString();
    }

    public static final Creator<PicURLsBean> CREATOR = new Creator<PicURLsBean>() {
        @Override
        public PicURLsBean createFromParcel(Parcel source) {
            return new PicURLsBean(source);
        }

        @Override
        public PicURLsBean[] newArray(int size) {
            return new PicURLsBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(thumbnail_pic);
    }
}
