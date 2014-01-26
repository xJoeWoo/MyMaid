package com.joewoo.ontime.support.span;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.joewoo.ontime.R;

/**
 * Created by JoeWoo on 13-11-2.
 */

public class NoUnderlineURLSpan extends ClickableSpan {

    private String URL;
    private Context context;

    public NoUnderlineURLSpan(String URL, Context context) {
        this.URL = URL;
        this.context = context;
    }

    @Override
    public void onClick(View sourceView) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getURL()));
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
        context.startActivity(intent);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(context.getResources().getColor(R.color.pinkHighlightSpan));
        ds.setUnderlineText(false);
    }

    public String getURL() {
        return URL;
    }
}


