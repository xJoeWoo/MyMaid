package com.joewoo.ontime;

import static com.joewoo.ontime.info.Defines.*;

import com.joewoo.ontime.tools.MySQLHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

@SuppressLint("NewApi")
public class Start extends Activity {

	public static Start _instance = null;

	private MySQLHelper sqlHelper = new MySQLHelper(Start.this, SQL_NAME,
			null, SQL_VERSION);
	private SQLiteDatabase sql;

	private static final Uri JouYiu = Uri.parse("http://weibo.com/1665287983");

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_instance = this;

		setContentView(R.layout.start);

		sql = sqlHelper.getWritableDatabase();

		getActionBar().setDisplayHomeAsUpEnabled(true);

		Button login = (Button) findViewById(R.id.btn_start_login);
		Button post = (Button) findViewById(R.id.btn_start_post);
		Button info = (Button) findViewById(R.id.btn_start_info);

		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (checkNetwork())
					startActivity(new Intent(Start.this, Login.class));
				else
					Toast.makeText(Start.this, "没有网络不能登录…", Toast.LENGTH_SHORT)
							.show();
			}
		});

		post.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (checkNetwork())
					startActivity(new Intent(Start.this, Post.class));
				else
					Toast.makeText(Start.this, "没有网络不能发Po…", Toast.LENGTH_SHORT)
							.show();

			}
		});

		info.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW, JouYiu));
			}
		});
	}

	public boolean checkNetwork() {
		ConnectivityManager cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cManager.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isAvailable()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		menu.add(0, MENU_PROFILE_SWITCH, 0, "切换用户").setIcon(R.drawable.social_cc_bcc)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: {
			finish();
			break;
		}
		case MENU_PROFILE_SWITCH: {

			Cursor cursor = sql.query(sqlHelper.tableName, new String[] {
					sqlHelper.UID, sqlHelper.SCREEN_NAME }, null, null, null,
					null, null);
			Log.e(TAG_SQL, "Queried users");

			if (cursor.getCount() > 0) {
				final String[] singleUid = new String[cursor.getCount()];
				final String[] singleUser = new String[cursor.getCount()];

				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
						.moveToNext()) {
					singleUid[cursor.getPosition()] = cursor.getString(0);
					singleUser[cursor.getPosition()] = cursor.getString(1);
					Log.e(TAG, "Cursor position - " + cursor.getPosition());
					Log.e(TAG, "Single Uid - "
							+ singleUid[cursor.getPosition()]);
					Log.e(TAG,
							"Single User - " + singleUser[cursor.getPosition()]);
					Log.e(TAG, LOG_DEVIDER);
				}

				new AlertDialog.Builder(this).setTitle("选择帐号")
						.setItems(singleUser, new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								SharedPreferences uids = getSharedPreferences(
										PREFERENCES, MODE_PRIVATE);
								SharedPreferences.Editor uidsE = uids.edit();

								uidsE.putString(LASTUID, singleUid[which]);
								Log.e(TAG, "Chose UID: " + singleUid[which]);
								Log.e(TAG, "Chose Screen Name: "
										+ singleUid[which]);
								uidsE.commit();
								startActivity(new Intent(Start.this, Post.class));
								finish();
							}
						}).show();
			} else {
				Toast.makeText(Start.this, "木有哪怕一个帐号", Toast.LENGTH_SHORT)
						.show();
			}

			// final SharedPreferences uids = getSharedPreferences(PREFERENCES,
			// MODE_PRIVATE);
			// final SharedPreferences.Editor uidsE = uids.edit();
			//
			// String allUids = uids.getString(ALLUID, null);
			// String allUsers = uids.getString(ALLUSER, null);
			//
			// if (allUids != null && allUsers != null) {
			// final String[] singleUid = allUids.split(",");
			// final String[] singleUser = allUsers.split(",");
			// if (!singleUser[0].equals("")) {
			// new AlertDialog.Builder(this).setTitle("选择帐号")
			// .setItems(singleUser, new OnClickListener() {
			// @Override
			// public void onClick(DialogInterface dialog,
			// int which) {
			// uidsE.putString(LASTUID, singleUid[which]);
			// Log.e(TAG, singleUid[which]);
			// uidsE.commit();
			// startActivity(new Intent(Start.this,
			// Post.class));
			// finish();
			// }
			// }).show();
			// } else {
			// Toast.makeText(Start.this, "木有哪怕一个帐号", Toast.LENGTH_SHORT)
			// .show();
			// }
			// } else {
			// Toast.makeText(Start.this, "木有哪怕一个帐号", Toast.LENGTH_SHORT)
			// .show();
			// }

			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}

}
