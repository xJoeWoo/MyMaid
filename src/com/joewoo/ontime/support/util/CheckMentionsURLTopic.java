package com.joewoo.ontime.support.util;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;

import com.joewoo.ontime.support.span.NoUnderlineURLSpan;
import com.joewoo.ontime.support.span.UserSpan;

import static com.joewoo.ontime.support.info.Defines.TAG;

/**
 * Created by JoeWoo on 13-11-21.
 */
public final class CheckMentionsURLTopic {

    public static SpannableString getSpannableString(String str, Context context) {
        str = str + " ";
        char strarray[] = str.toCharArray();
        SpannableString ss = new SpannableString(str);

        try {
            for (int i = 0; i < str.length(); i++) {

                // Check mention
                if (strarray[i] == '@') {
                    StringBuilder sb = new StringBuilder();
                    for (int j = i + 1; j < str.length(); j++) {
                        if (strarray[j] != ' ' && strarray[j] != ':'
                                && strarray[j] != '/' && strarray[j] != '…'
                                && strarray[j] != '。' && strarray[j] != '，'
                                && strarray[j] != '@' && strarray[j] != '：'
                                && strarray[j] != '（' && strarray[j] != '(' && strarray[j] != ')'
                                && strarray[j] != '！' && strarray[j] != '.'
                                && strarray[j] != ',' && strarray[j] != '）') {
                            sb.append(strarray[j]);
                        } else {
                            if (j != i + 1) {
                                ss.setSpan(new UserSpan(sb.toString(), context), i, j,
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                Log.e(TAG, "@" + sb.toString());
                            }
                            i = j;
                            break;
                        }
                    }
                }

                // Check topic
                if (strarray[i] == '#') {
                    StringBuilder sb = new StringBuilder("http://s.weibo.com/weibo/");
                    for (int j = i + 1; j < str.length(); j++) {
                        if (strarray[j] != '#') {
                            sb.append(strarray[j]);
                        } else {
                            if (j != i + 1) {
                                Log.e(TAG, sb.toString());
                                ss.setSpan(new NoUnderlineURLSpan(sb.toString(), context), i, j + 1,
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            i = j;
                            break;
                        }
                    }
                }

                // Check website
                if (strarray[i] == 'h' && strarray[i + 4] == ':'
                        && strarray[i + 5] == '/') {
                    StringBuilder sb = new StringBuilder("http://t.cn/");

                    int j;
                    for (j = i + 12; j < i + 19; j++) {
                        sb.append(strarray[j]);
                    }
                    ss.setSpan(new NoUnderlineURLSpan(sb.toString(), context), i, j,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    Log.e(TAG, "HTTP URL - " + sb.toString());
                    i = j;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Check mentions FAILED");
        }

        strarray = null;
        return ss;}
}
