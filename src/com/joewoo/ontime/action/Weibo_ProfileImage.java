package com.joewoo.ontime.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import static com.joewoo.ontime.info.Defines.*;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.Log;

public class Weibo_ProfileImage extends Thread {

	private String url;
	private Handler mHandler;

	public Weibo_ProfileImage(String url, Handler handler) {

		this.url = url;
		this.mHandler = handler;
	}

	public void run() {

		Log.e(TAG, "Profile Image Thread Start");

		Bitmap bm;

		HttpUriRequest httpGet = new HttpGet(url);

		try {
			bm = toRoundCorner(
					BitmapFactory.decodeStream(new DefaultHttpClient()
							.execute(httpGet).getEntity().getContent()), 25);

			Log.e(TAG, "GOT: Profile Image");

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(CompressFormat.PNG, 100, baos);

			mHandler.obtainMessage(GOT_PROFILEIMG_INFO, baos.toByteArray())
					.sendToTarget();

			// }

		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

}
