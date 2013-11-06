package com.joewoo.ontime;

import com.joewoo.ontime.action.Weibo_AccessToken;
import com.joewoo.ontime.action.Weibo_ProfileImage;
import com.joewoo.ontime.action.Weibo_UserShow;
import com.joewoo.ontime.bean.WeiboBackBean;
import com.joewoo.ontime.info.Weibo_Constants;
import com.joewoo.ontime.info.Weibo_URLs;
import com.joewoo.ontime.main.Main;
import com.joewoo.ontime.tools.MyMaidSQLHelper;

import static com.joewoo.ontime.info.Constants.*;

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

	private MyMaidSQLHelper sqlHelper = new MyMaidSQLHelper(Login.this, MyMaidSQLHelper.SQL_NAME, null,
            MyMaidSQLHelper.SQL_VERSION);
	private SQLiteDatabase sql;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_instance = this;
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.login);
		setProgressBarIndeterminateVisibility(false);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		wv_login = (WebView) findViewById(R.id.wv_login);
		wv_login.getSettings().setJavaScriptEnabled(true);
		wv_login.loadUrl(Weibo_URLs.AUTH);

		uids = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
		uidsE = uids.edit();

		sql = sqlHelper.getWritableDatabase();

		wv_login.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				Log.e(TAG, "onPageStarted URL: " + url);
				setProgressBarIndeterminateVisibility(true);
				if (url.startsWith(Weibo_URLs.CALLBACK)) {
					view.cancelLongPress();
					view.stopLoading();
					Weibo_Constants.AUTH_CODE = url.substring(url.indexOf("=") + 1);
					Log.e(TAG, "Auth Code: " + Weibo_Constants.AUTH_CODE);
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
				Weibo_Constants.ACCESS_TOKEN = token.getAccessToken();
				Weibo_Constants.UID = token.getUid();
				Weibo_Constants.EXPIRES_IN = token.getExpiresIn();

				// SimpleDateFormat sdf = new SimpleDateFormat("dd天 HH:MM:SS");
				// sdf.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
				// tv_login_code.setText("Access Token - "
				// + Weibo_Constants.ACCESS_TOKEN + "\n剩余时间 - "
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

				Weibo_Constants.SCREEN_NAME = show.getScreenName();
				Weibo_Constants.LOCATION = show.getLocation();

				new Weibo_ProfileImage(show.getProfileImageUrl(), mHandler)
						.start();

				break;
			}
			case GOT_PROFILEIMG_INFO: {

				Cursor c = sql.query(MyMaidSQLHelper.tableName, new String[] {
                        MyMaidSQLHelper.UID, MyMaidSQLHelper.ACCESS_TOKEN,
						MyMaidSQLHelper.LOCATION, MyMaidSQLHelper.EXPIRES_IN,
						MyMaidSQLHelper.SCREEN_NAME }, MyMaidSQLHelper.UID + "=?",
						new String[] { Weibo_Constants.UID }, null, null, null);

				ContentValues cv = new ContentValues();

				cv.put(MyMaidSQLHelper.ACCESS_TOKEN, Weibo_Constants.ACCESS_TOKEN);
				cv.put(MyMaidSQLHelper.LOCATION, Weibo_Constants.LOCATION);
				cv.put(MyMaidSQLHelper.SCREEN_NAME, Weibo_Constants.SCREEN_NAME);
				cv.put(MyMaidSQLHelper.EXPIRES_IN, Weibo_Constants.EXPIRES_IN);
				cv.put(MyMaidSQLHelper.PROFILEIMG, (byte[]) msg.obj);

				if (c.getCount() > 0)// 查询到已经存在UID（已经登录）
				{

					Log.e(MyMaidSQLHelper.TAG_SQL, "Got login info");

					if (sql.update(MyMaidSQLHelper.tableName, cv, MyMaidSQLHelper.UID
							+ "='" + Weibo_Constants.UID + "'", null) != 0) {
						Log.e(MyMaidSQLHelper.TAG_SQL, "SQL login info Updated");
					}

					c.moveToFirst();
					Log.e(MyMaidSQLHelper.TAG_SQL, MyMaidSQLHelper.UID + c.getString(0));
					Log.e(MyMaidSQLHelper.TAG_SQL, MyMaidSQLHelper.ACCESS_TOKEN + c.getString(1));
					Log.e(MyMaidSQLHelper.TAG_SQL, MyMaidSQLHelper.LOCATION + c.getString(2));
					Log.e(MyMaidSQLHelper.TAG_SQL, MyMaidSQLHelper.EXPIRES_IN + c.getString(3));
					Log.e(MyMaidSQLHelper.TAG_SQL, MyMaidSQLHelper.SCREEN_NAME + c.getString(4));

				} else {// 否则插入登录信息

					cv.put(MyMaidSQLHelper.UID, Weibo_Constants.UID);

					sql.insert(MyMaidSQLHelper.tableName, null, cv);

					Cursor cursor = sql.query(MyMaidSQLHelper.tableName, null, null,
							null, null, null, null);
					Log.e(MyMaidSQLHelper.TAG_SQL, "Inserted");
					Log.e(MyMaidSQLHelper.TAG_SQL, "Display all data:");
					for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
							.moveToNext()) {
						Log.e(MyMaidSQLHelper.TAG_SQL, "No. " + cursor.getInt(0));
						Log.e(MyMaidSQLHelper.TAG_SQL, MyMaidSQLHelper.UID + cursor.getString(1));
						Log.e(MyMaidSQLHelper.TAG_SQL,
								MyMaidSQLHelper.ACCESS_TOKEN + cursor.getString(2));
						Log.e(MyMaidSQLHelper.TAG_SQL, MyMaidSQLHelper.LOCATION + cursor.getString(3));
						Log.e(MyMaidSQLHelper.TAG_SQL,
								MyMaidSQLHelper.EXPIRES_IN + cursor.getString(4));
						Log.e(MyMaidSQLHelper.TAG_SQL,
								MyMaidSQLHelper.SCREEN_NAME + cursor.getString(5));
						Log.e(MyMaidSQLHelper.TAG_SQL, LOG_DEVIDER);
					}

				}

				uidsE.putString(LASTUID, Weibo_Constants.UID);
				uidsE.commit();
				setProgressBarIndeterminateVisibility(false);
//				Start._instance.finish();
				startActivity(new Intent(Login.this, Main.class));
				
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
