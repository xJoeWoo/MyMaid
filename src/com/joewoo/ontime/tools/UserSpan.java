package com.joewoo.ontime.tools;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;

import com.joewoo.ontime.R;
import com.joewoo.ontime.SingleUser;

import static com.joewoo.ontime.info.Defines.SCREEN_NAME;

/**
 * Created by JoeWoo on 13-11-2.
 */

public class UserSpan extends ClickableSpan {

    private String screenName;
    private Context context;

    public UserSpan(String screenName, Context context) {
        this.screenName = screenName;
        this.context = context;
    }

    @Override
    public void onClick(View sourceView) {
        Intent i = new Intent(context, SingleUser.class);
        i.putExtra(SCREEN_NAME, getScreenName());
        context.startActivity(i);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(context.getResources().getColor(R.color.highlightPink));
        ds.setUnderlineText(false); //去掉下划线
    }

    public String getScreenName(){
        return screenName;
    }
}


