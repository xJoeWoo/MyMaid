package com.joewoo.ontime.tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by JoeWoo on 13-11-7.
 */
public class MyMaidUtilities {

    /**
     * Set round corner to normal photo
     *
     * @param bm the photo to set
     * @param px radius to set
     * @return a round corner photo
     */
    public Bitmap toRoundCorner(Bitmap bm, int px) {
        Bitmap output = Bitmap.createBitmap(bm.getWidth(),
                bm.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bm.getWidth(), bm.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = px;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bm, rect, rect, paint);

        return output;
    }

    /**
     * Check the network whether available
     *
     * @param activity
     * @return network's status
     */
    public boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager cManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isAvailable()) {
            return true;
        } else {
            return false;
        }
    }


    private String[] str62keys = {"0", "1", "2", "3", "4", "5", "6",
            "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w",
            "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
            "X", "Y", "Z"};

    /**
     * Transform a short website to normal one
     *
     * @param int10 short website
     * @return normal website
     */
    public String IntToEnode62(Integer int10) {
        String s62 = "";
        int r = 0;
        while (int10 != 0) {
            r = int10 % 62;
            s62 = str62keys[r] + s62;
            int10 = (int) Math.floor(int10 / 62.0);
        }
        return s62;
    }

    /**
     * Transform a normal website to short without "http://t.cn/"
     *
     * @param str10 normal website
     * @return short website
     */
    public String Id2Mid(String str10) {
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
