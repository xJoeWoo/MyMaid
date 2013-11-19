package com.joewoo.ontime.tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;

import com.joewoo.ontime.info.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static com.joewoo.ontime.info.Constants.TAG;
import static com.joewoo.ontime.info.Constants.TEMP_IMAGE_NAME;
import static com.joewoo.ontime.info.Constants.TEMP_IMAGE_PATH;

/**
 * Created by JoeWoo on 13-11-7.
 */
public final class MyMaidUtilities {

    /**
     * Set clickable links about @/#/http in {@link java.lang.String}
     *
     * @param str the String to check
     * @param act a {@link android.app.Activity} is needed to build {@link android.text.SpannableString}
     * @return a {@link android.text.SpannableString} included @/#/http
     */
    public static SpannableString checkMentionsURL(String str, Activity act) {

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
                                ss.setSpan(new UserSpan(sb.toString(), act), i, j,
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
                                ss.setSpan(new NoUnderlineURLSpan(sb.toString(), act), i, j + 1,
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
                    ss.setSpan(new NoUnderlineURLSpan(sb.toString(), act), i, j,
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

    /**
     * Set round corner to normal photo
     *
     * @param bm the photo to set
     * @param px radius to set
     * @return a round corner photo
     */
    public static Bitmap toRoundCorner(Bitmap bm, int px) {
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
     * @return network status
     */
    public static boolean isNetworkAvailable(Activity activity) {
        NetworkInfo netInfo = ((ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return netInfo != null && netInfo.isAvailable();
    }


    private static String[] str62keys = {"0", "1", "2", "3", "4", "5", "6",
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
    public static String IntToEnode62(Integer int10) {
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

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    public static Bitmap decodeSampledBitmap(byte[] bytes, int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }


    public static void saveBitmapToPNG(Bitmap bitmap, String filePath, String fileName) throws IOException {
        File f = new File(filePath);
        if(!f.exists()){
            f.mkdirs();
        }
        f = new File(filePath, fileName);
        OutputStream fOut = new FileOutputStream(f);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100 , fOut);
        fOut.flush();
        fOut.close();
    }

}
