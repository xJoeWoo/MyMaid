package com.joewoo.ontime.support.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.widget.ImageView;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.span.NoUnderlineURLSpan;
import com.joewoo.ontime.support.span.UserSpan;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static com.joewoo.ontime.support.info.Defines.TAG;

/**
 * Created by Joe on 14-1-30.
 */
public class MyMaidUtilites {

    public static void setBytesToImageView(byte[] bytes, ImageView iv) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new ByteArrayInputStream(bytes), null, options);

        if (options.outHeight < 500)
            options.inSampleSize = 1;
        else if (options.outHeight < 1000)
            options.inSampleSize = 2;
        else if (options.outHeight < 2000)
            options.inSampleSize = 3;
        else if (options.outHeight < 3000)
            options.inSampleSize = 4;
        else
            options.inSampleSize = 5;

        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes), null, options);
        iv.setImageBitmap(bitmap);
        bitmap = null;
    }

    /**
     * Created by JoeWoo on 13-11-21.
     */
    public static class IDtoMID {

        private static String[] str62keys = {"0", "1", "2", "3", "4", "5", "6",
                "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
                "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w",
                "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
                "X", "Y", "Z"};

        private static String IntToEnode62(Integer int10) {
            String s62 = "";
            int r = 0;
            while (int10 != 0) {
                r = int10 % 62;
                s62 = str62keys[r] + s62;
                int10 = (int) Math.floor(int10 / 62.0);
            }
            return s62;
        }

        public static String Id2Mid(String str10) {
            String mid = "";
            int count = 1;
            for (int i = str10.length() - 7; i > -7; i = i - 7) // 从最后往前以7字节为一组读取字符
            {
                int offset = i < 0 ? 0 : i;
                int len = i < 0 ? str10.length() % 7 : 7;
                String temp = str10.substring(offset, offset + len);
                String url = IntToEnode62(Integer.valueOf(temp));
                if (count != 3) {// z xghm uXym 生成的链接从右往左的前2组，4位一组，不足4位的补0
                    for (int j = 0; j < 4 - url.length(); j++) {
                        url = "0" + url;
                    }
                }
                mid = url + mid;
                count++;
            }
            return mid;
        }

    }

    /**
     * Created by JoeWoo on 13-11-29.
     */
    public static class TimeFormat {

        private static final SimpleDateFormat format = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy", Locale.US);
        private static final Calendar cc = Calendar.getInstance();
        private static int currentDay;

        static {
            format.setTimeZone(TimeZone.getTimeZone("GMT+0800"));
            cc.setTimeInMillis(System.currentTimeMillis());
            currentDay = cc.get(Calendar.DAY_OF_YEAR);

            //        Log.e(TAG, "System day: " + String.valueOf(currentDay));
        }

        public static String parse(String GMTTime) {
            try {

                Calendar c = Calendar.getInstance();
                c.setTime(format.parse(GMTTime));
                int statusDay = c.get(Calendar.DAY_OF_YEAR);
                //            Log.e(TAG, "Status day: " + String.valueOf(statusDay));

                if (currentDay - statusDay < 1 && currentDay - statusDay >= 0) {
                    return GMTTime.substring(11, 16);
                } else if (currentDay - statusDay < 2 && currentDay - statusDay >= 0) {
                    return GlobalContext.getResString(R.string.time_yesterday) + GMTTime.substring(11, 16);
                } else if (currentDay - statusDay < 3 && currentDay - statusDay >= 0) {
                    return GlobalContext.getResString(R.string.time_2_days_ago) + GMTTime.substring(11, 16);
                } else if (currentDay - statusDay < 4 && currentDay - statusDay >= 0) {
                    return GlobalContext.getResString(R.string.time_3_days_ago) + GMTTime.substring(11, 16);
                } else {
                    return String.valueOf(c.get(Calendar.MONTH) + 1) + "-" + String.valueOf(c.get(Calendar.DAY_OF_MONTH)) + " · " + GMTTime.substring(11, 16);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            return GMTTime.substring(11, 16);
        }

    }

    /**
     * Created by JoeWoo on 13-11-21.
     */
    public static class CheckMentionsURLTopic {

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
            return ss;
        }
    }
}
