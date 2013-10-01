package com.joewoo.ontime;

import com.joewoo.ontime.action.Weibo_AccessToken;
import com.joewoo.ontime.action.Weibo_ProfileImage;
import com.joewoo.ontime.action.Weibo_UserShow;
import com.joewoo.ontime.bean.WeiboBackBean;
import com.joewoo.ontime.fragment.Timeline_Comments_Mentions;
import com.joewoo.ontime.info.WeiboConstant;
import com.joewoo.ontime.tools.MySQLHelper;

import static com.joewoo.ontime.info.Defines.*;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

@SuppressLint({ "NewApi", "SetJavaScriptEnabled", "HandlerLeak" })
public class Login extends Activity {

	public WebView wv_login;

	SharedPreferences uids;
	SharedPreferences.Editor uidsE;

	public static Login _instance = null;

	private MySQLHelper sqlHelper = new MySQLHelper(Login.this, SQL_NAME, null,
			SQL_VERSION);
	private SQLiteDatabase sql;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_instance = this;
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.login);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		wv_login = (WebView) findViewById(R.id.wv_login);
		wv_login.getSettings().setJavaScriptEnabled(true);
		wv_login.loadUrl(AUTH_URL);

		uids = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
		uidsE = uids.edit();

		sql = sqlHelper.getWritableDatabase();

		wv_login.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				Log.e(TAG, "onPageStarted URL: " + url);
				setProgressBarIndeterminateVisibility(true);
				if (url.startsWith(CALLBACK_URL)) {
					view.cancelLongPress();
					view.stopLoading();
					WeiboConstant.AUTH_CODE = url.substring(url.indexOf("=") + 1);
					Log.e(TAG, "Auth Code: " + WeiboConstant.AUTH_CODE);
					new Weibo_AccessToken(mHandler).start();
				}
//				super.onPageStarted(view, url, favicon);
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				Log.e(TAG, "onPageFinished");
				setProgressBarIndeterminateVisibility(false);
//				super.onPageFinished(view, url);
			}
		});
		
		

	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			setProgressBarIndeterminateVisibility(true);
			switch (msg.what) {
			case GOT_ACCESS_TOKEN: {
				WeiboBackBean token = (WeiboBackBean) msg.obj;
				WeiboConstant.ACCESS_TOKEN = token.getAccessToken();
				WeiboConstant.UID = token.getUid();
				WeiboConstant.EXPIRES_IN = token.getExpiresIn();

				// SimpleDateFormat sdf = new SimpleDateFormat("dd天 HH:MM:SS");
				// sdf.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
				// tv_login_code.setText("Access Token - "
				// + WeiboConstant.ACCESS_TOKEN + "\n剩余时间 - "
				// + sdf.format(token.getExpiresIn() * 1000) + "\nUid - "
				// + token.getUid());

				new Weibo_UserShow(mHandler).start();
				break;
			}
			case GOT_ACCESS_TOKEN_FAIL: {
				Toast.makeText(Login.this, R.string.toast_access_token_fail, Toast.LENGTH_SHORT).show();
				break;
			}
			case GOT_SHOW_INFO: {
				WeiboBackBean show = (WeiboBackBean) msg.obj;

				WeiboConstant.SCREEN_NAME = show.getScreenName();
				WeiboConstant.LOCATION = show.getLocation();

				new Weibo_ProfileImage(show.getProfileImageUrl(), mHandler)
						.start();

				break;
			}
			case GOT_PROFILEIMG_INFO: {

				Cursor c = sql.query(sqlHelper.tableName, new String[] {
						sqlHelper.UID, sqlHelper.ACCESS_TOKEN,
						sqlHelper.LOCATION, sqlHelper.EXPIRES_IN,
						sqlHelper.SCREEN_NAME }, sqlHelper.UID + "=?",
						new String[] { WeiboConstant.UID }, null, null, null);

				ContentValues cv = new ContentValues();

				cv.put(sqlHelper.ACCESS_TOKEN, WeiboConstant.ACCESS_TOKEN);
				cv.put(sqlHelper.LOCATION, WeiboConstant.LOCATION);
				cv.put(sqlHelper.SCREEN_NAME, WeiboConstant.SCREEN_NAME);
				cv.put(sqlHelper.EXPIRES_IN, WeiboConstant.EXPIRES_IN);
				cv.put(sqlHelper.PROFILEIMG, (byte[]) msg.obj);

				if (c.getCount() > 0)// 查询到已经存在UID（已经登录）
				{

					Log.e(TAG_SQL, "Got login info");

					if (sql.update(sqlHelper.tableName, cv, sqlHelper.UID
							+ "='" + WeiboConstant.UID + "'", null) != 0) {
						Log.e(TAG_SQL, "SQL login info Updated");
					}

					c.moveToFirst();
					Log.e(TAG_SQL, sqlHelper.UID + c.getString(0));
					Log.e(TAG_SQL, sqlHelper.ACCESS_TOKEN + c.getString(1));
					Log.e(TAG_SQL, sqlHelper.LOCATION + c.getString(2));
					Log.e(TAG_SQL, sqlHelper.EXPIRES_IN + c.getString(3));
					Log.e(TAG_SQL, sqlHelper.SCREEN_NAME + c.getString(4));

				} else {// 否则插入登录信息

					cv.put(sqlHelper.UID, WeiboConstant.UID);

					sql.insert(sqlHelper.tableName, null, cv);

					Cursor cursor = sql.query(sqlHelper.tableName, null, null,
							null, null, null, null);
					Log.e(TAG_SQL, "Inserted");
					Log.e(TAG_SQL, "Display all data:");
					for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
							.moveToNext()) {
						Log.e(TAG_SQL, "No. " + cursor.getInt(0));
						Log.e(TAG_SQL, sqlHelper.UID + cursor.getString(1));
						Log.e(TAG_SQL,
								sqlHelper.ACCESS_TOKEN + cursor.getString(2));
						Log.e(TAG_SQL, sqlHelper.LOCATION + cursor.getString(3));
						Log.e(TAG_SQL,
								sqlHelper.EXPIRES_IN + cursor.getString(4));
						Log.e(TAG_SQL,
								sqlHelper.SCREEN_NAME + cursor.getString(5));
						Log.e(TAG_SQL, LOG_DEVIDER);
					}

				}

				uidsE.putString(LASTUID, WeiboConstant.UID);
				uidsE.commit();

//				Start._instance.finish();
				startActivity(new Intent(Login.this, Timeline_Comments_Mentions.class));
				finish();
				break;
			}
			case GOT_SHOW_INFO_FAIL: {
				break;
			}
			}
		}
	};
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: {
			finish();
			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}

}
