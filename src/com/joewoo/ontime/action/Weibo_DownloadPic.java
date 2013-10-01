package com.joewoo.ontime.action;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import com.joewoo.ontime.tools.RoundCorner;

import static com.joewoo.ontime.info.Defines.*;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class Weibo_DownloadPic extends AsyncTask<String, Integer, Bitmap> {

	private ImageView iv;
	private Bitmap image;
	private ProgressBar pb;

	public Weibo_DownloadPic(ImageView iv, ProgressBar pb) {
		this.iv = iv;
		this.pb = pb;

	}

	public Weibo_DownloadPic(ImageView iv) {
		this.iv = iv;
	}

	@Override
	protected void onPreExecute() {
		// Toast.makeText(activity, "开始下载图片…", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected Bitmap doInBackground(String... params) {

		Log.e(TAG, "Download pic AsyncTask start");

		try {

			HttpUriRequest httpGet = new HttpGet(params[0]);

			Log.e(TAG, "Pic URL - " + params[0]);

			HttpEntity httpResponse = new DefaultHttpClient().execute(httpGet)
					.getEntity();

			// Log.e(TAG, "2");

			publishProgress(0);

			InputStream is = httpResponse.getContent();

			long maxSize = httpResponse.getContentLength();
			Log.e(TAG, "MaxSize - " + String.valueOf(maxSize));
			float nowSize = 0;

			// Log.e(TAG, "3");

			// Log.e(TAG, "4");

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			byte[] buffer = new byte[1024];
			int len = -1;

			try {
				while ((len = is.read(buffer)) != -1) {
					if (!isCancelled()) {
						baos.write(buffer, 0, len);
						baos.flush();
						nowSize += len;
//						Log.e(TAG, String.valueOf(nowSize));
						publishProgress((int) ((nowSize / (float) maxSize) * 100));
					}else{
						is.close();
					}
				}
			} catch (Exception e) {

			} finally {

			}

			byte[] imgBytes = baos.toByteArray();

			if (maxSize > 10000) {

				// image = new
				// GausscianBlur(BitmapFactory.decodeByteArray(imgBytes,
				// 0, imgBytes.length)).getBitmap();

				image = BitmapFactory.decodeByteArray(imgBytes, 0,
						imgBytes.length);

			} else {

				image = new RoundCorner(BitmapFactory.decodeByteArray(imgBytes,
						0, imgBytes.length), 25).getBitmap();

			}

			is.close();

		} catch (Exception e) {
			Log.e(TAG, "Download pic ERROR!");
		}

		return image;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		if (!isCancelled()) {
			if (pb != null) {
				if (progress[0] == 0)
					pb.setVisibility(View.VISIBLE);
				// Log.e(TAG, "Process - " + String.valueOf(progress[0]));
				pb.setProgress((int) progress[0]);
			}
		}
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (!isCancelled()) {
			iv.setImageBitmap(image);
			if (pb != null)
				pb.setVisibility(View.INVISIBLE);
		}
	}

}
