package com.joewoo.ontime;

import java.io.File;

import com.joewoo.ontime.action.Weibo_Update;
import com.joewoo.ontime.action.Weibo_Upload;
import com.joewoo.ontime.bean.WeiboBackBean;
import com.joewoo.ontime.info.WeiboConstant;
import com.joewoo.ontime.tools.Id2MidUtil;
import com.joewoo.ontime.tools.MySQLHelper;

import static com.joewoo.ontime.info.Defines.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "NewApi", "HandlerLeak" })
public class Post extends Activity {

	EditText et_post;
	File picFile;
	TextView tv_post_info;
	long downTime = 0;
	String draft;
	boolean sending;
	boolean sent;
	Animation a_out;
	Animation a_in;
	ProgressBar pb_post;

	SharedPreferences preferences;
	SharedPreferences.Editor editor;

	MySQLHelper sqlHelper = new MySQLHelper(Post.this, SQL_NAME, null,
			SQL_VERSION);
	SQLiteDatabase sql;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.post);
		findViews();

		Log.e(TAG, "Post Weibo");

		getActionBar().setDisplayUseLogoEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setTitle(getString(R.string.act_post_title));

		sql = sqlHelper.getWritableDatabase();

		preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
		editor = preferences.edit();

		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		if (!intent.getBooleanExtra(IS_FRAG_POST, false)) {

			if (preferences.getString(LASTUID, null) != null) {

				String lastUid = preferences.getString(LASTUID, null);

				Cursor c = sql.query(sqlHelper.tableName, new String[] {
						sqlHelper.UID, sqlHelper.ACCESS_TOKEN,
						sqlHelper.LOCATION, sqlHelper.EXPIRES_IN,
						sqlHelper.SCREEN_NAME, sqlHelper.DRAFT,
						sqlHelper.PROFILEIMG }, sqlHelper.UID + "=?",
						new String[] { lastUid }, null, null, null);

				Log.e(TAG, "2");

				Log.e(TAG, "3");

				if (c.moveToFirst()) {

					Log.e(TAG, "4");

					WeiboConstant.UID = c.getString(c
							.getColumnIndex(sqlHelper.UID));
					WeiboConstant.ACCESS_TOKEN = c.getString(c
							.getColumnIndex(sqlHelper.ACCESS_TOKEN));
					WeiboConstant.LOCATION = c.getString(c
							.getColumnIndex(sqlHelper.LOCATION));
					WeiboConstant.EXPIRES_IN = Integer.valueOf(c.getString(c
							.getColumnIndex(sqlHelper.EXPIRES_IN)));
					WeiboConstant.SCREEN_NAME = c.getString(c
							.getColumnIndex(sqlHelper.SCREEN_NAME));

					byte[] profileImg = c.getBlob(c
							.getColumnIndex(sqlHelper.PROFILEIMG));
					getActionBar().setLogo(
							new BitmapDrawable(getResources(), BitmapFactory
									.decodeByteArray(profileImg, 0,
											profileImg.length)));

					Log.e(TAG, "WeiboConstant: " + WeiboConstant.ACCESS_TOKEN
							+ WeiboConstant.SCREEN_NAME + WeiboConstant.UID
							+ WeiboConstant.LOCATION);

					if ((draft = c.getString(c.getColumnIndex(sqlHelper.DRAFT))) != null) {
						Log.e(TAG, "5");
						if (!"".equals(draft) && draft != null) {
							Log.e(TAG, "Draft: " + draft);
							et_post.setText(draft);
							et_post.setSelection(draft.length());
						}
					}

					Log.e(TAG, "6");

					if (WeiboConstant.PICPATH != null
							|| WeiboConstant.WORDS != null) {
						Log.e(TAG, WeiboConstant.PICPATH + WeiboConstant.WORDS);
						if (WeiboConstant.PICPATH != null) {
							picFile = new File(WeiboConstant.PICPATH);
							rfBar();
						} else if (WeiboConstant.WORDS != null) {
							et_post.setText(WeiboConstant.WORDS);
						}
					}

					Log.e(TAG, "7");
					if (Intent.ACTION_SEND.equals(action) && type != null) {// 分享到此Activity
						Log.e(TAG, "8");
						Toast.makeText(
								Post.this,
								"欢迎" + WeiboConstant.LOCATION + "Po主 "
										+ WeiboConstant.SCREEN_NAME,
								Toast.LENGTH_LONG).show();
						Log.e(TAG, "9");
						if ("text/plain".equals(type)) { // 传入文件为文字
							Log.e(TAG, "10 TEXT");
							et_post.setText(intent
									.getStringExtra(Intent.EXTRA_TEXT));
						} else if (type.startsWith("image/")) { // 传入文件为图片
							Log.e(TAG, "10 PHOTO");
							Uri uri = (Uri) intent
									.getParcelableExtra(Intent.EXTRA_STREAM);
							if (uri != null) {
								String picPath = getFilePath(uri);
								picFile = new File(picPath);
							}
							rfBar();
						}

					} else {// 登录到此Activity

						Log.e(TAG, "11");
						if (WeiboConstant.SCREEN_NAME != null
								&& WeiboConstant.LOCATION != null) {
							Log.e(TAG, "12");
							Toast.makeText(
									Post.this,
									"欢迎" + WeiboConstant.LOCATION + "Po主 "
											+ WeiboConstant.SCREEN_NAME,
									Toast.LENGTH_LONG).show();
						}
					}
				} else {// 没有查询到数据库信息
					Log.e(TAG, "17");
					saveSharingInfo(intent, type, action);
					Toast.makeText(Post.this, "请先登录~", Toast.LENGTH_LONG)
							.show();
					jumpToLogin();
				}
			} else {// 不存在配置文件，需要登录
				Log.e(TAG, "18");
				saveSharingInfo(intent, type, action);
				Toast.makeText(Post.this, "请先登录~", Toast.LENGTH_LONG).show();
				jumpToLogin();
			}
		} else {
			Log.e(TAG, "19");
			byte[] profileImg = intent.getByteArrayExtra(PROFILE_IMAGE);
			Log.e(TAG, "20");
			getActionBar()
					.setLogo(
							new BitmapDrawable(getResources(), BitmapFactory
									.decodeByteArray(profileImg, 0,
											profileImg.length)));
			Log.e(TAG, "21");
			Cursor c = sql.query(sqlHelper.tableName,
					new String[] { sqlHelper.DRAFT }, sqlHelper.UID + "=?",
					new String[] { WeiboConstant.UID }, null, null, null);
			Log.e(TAG, "22");
			if (c.moveToFirst()) {
				if ((draft = c.getString(c.getColumnIndex(sqlHelper.DRAFT))) != null) {
					if (!"".equals(draft) && draft != null) {
						Log.e(TAG, "23");
						Log.e(TAG, "Draft: " + draft);
						et_post.setText(draft);
						et_post.setSelection(draft.length());
					}
				}
			}
		}

		et_post.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				rfBar(); // 刷新ActionBar
			}
		});

		a_out = AnimationUtils.loadAnimation(Post.this, R.anim.alpha_out);
		a_in = AnimationUtils.loadAnimation(Post.this, R.anim.alpha_in);

	}

	private void findViews() {
		et_post = (EditText) findViewById(R.id.et_post);
		tv_post_info = (TextView) findViewById(R.id.tv_post_info);
		pb_post = (ProgressBar) findViewById(R.id.pb_post);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		menu.add(0, MENU_LETTERS, 0,
				String.valueOf(140 - et_post.getText().length()))
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		if (picFile != null) {
			menu.add(0, MENU_ADD, 0, R.string.action_added)
					.setIcon(R.drawable.content_picture_ok)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		} else {
			menu.add(0, MENU_ADD, 0, R.string.action_add)
					.setIcon(R.drawable.content_picture)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}

		// menu.add(0, MENU_AT, 0, R.string.action_at)
		// .setIcon(R.drawable.ic_menu_btn_at)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		menu.add(0, MENU_AT, 0, "@人").setIcon(R.drawable.social_group)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		menu.add(0, MENU_EMOTION, 0, R.string.action_add_emotion)
				.setIcon(R.drawable.ic_menu_emoticons)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		menu.add(0, MENU_TOPIC, 0, R.string.action_add_topic)
				.setIcon(R.drawable.collections_labels)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		if (!sending) {
			menu.add(0, MENU_POST, 0, R.string.action_post)
					.setIcon(R.drawable.social_send_now)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		} else {
			// menu.add(0, MENU_POST, 0, R.string.action_post).setEnabled(false)
			// .setIcon(R.drawable.send)
			// .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}

		menu.add(0, MENU_CLEAR_DRAFT, 0, R.string.action_clear_draft);
		// menu.add(0, MENU_LOGOUT, 0, R.string.action_logout);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: {
			finish();
			break;
		}
		case MENU_LETTERS: {
			if (System.currentTimeMillis() - downTime > 2000) {
				Toast.makeText(Post.this, "再按一次清除文字", Toast.LENGTH_SHORT)
						.show();
				downTime = System.currentTimeMillis();
			} else {
				et_post.setText("");
			}
			break;
		}
		case MENU_ADD: {
			if (picFile == null) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, ACT_GOT_PHOTO);
			} else {
				if (System.currentTimeMillis() - downTime > 2000) {
					Toast.makeText(Post.this, "再按一次删除图片", Toast.LENGTH_SHORT)
							.show();
					downTime = System.currentTimeMillis();
				} else {
					picFile = null;
				}
			}
			rfBar(); // 刷新ActionBar
			break;
		}
		case MENU_POST: {
			if (checkNetwork()) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(et_post.getWindowToken(), 0);
				if (!"".equals(et_post.getText().toString().trim())) {
					if (picFile != null)

						new Weibo_Upload(et_post.getText().toString(), picFile,
								pb_post, mHandler).execute();
					else
						new Weibo_Update(et_post.getText().toString(), pb_post,
								mHandler).execute();

					setProgressBarIndeterminateVisibility(true);
					sending = true;
					rfBar(); // 刷新ActionBar
				} else {
					Toast.makeText(Post.this, "说点什么吧", Toast.LENGTH_SHORT)
							.show();
				}
			} else {
				Toast.makeText(Post.this, "没有网络神烦…", Toast.LENGTH_SHORT).show();
			}

			break;
		}
//		case MENU_LOGOUT: {
//
//			editor.putString(LASTUID, "");
//			editor.commit();
//
//			if (sql.delete(sqlHelper.tableName, sqlHelper.UID + "=?",
//					new String[] { WeiboConstant.UID }) > 0) {
//				Log.e(TAG_SQL, "LOGOUT - Cleared user info");
//			}
//
//			clearWeiboConstant();
//
//			Toast.makeText(Post.this, "<(￣︶￣)>", Toast.LENGTH_SHORT).show();
//			Intent i = new Intent();
//			i.setClass(Post.this, Start.class);
//			startActivity(i);
//			finish();
//			break;
//		}
		case MENU_CLEAR_DRAFT: {
			clearDraft();
			Toast.makeText(Post.this, "清除草稿成功", Toast.LENGTH_SHORT).show();
			break;
		}
		case MENU_AT: {
			if (checkNetwork())
				startActivityForResult(new Intent(Post.this, At.class),
						ACT_GOT_AT);
			else {
				Toast.makeText(Post.this, "没有网络自己@吧…", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		}
		case MENU_EMOTION: {
			insertString("[]", false);
			break;
		}
		case MENU_TOPIC: {
			insertString("##", false);
			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			sending = false;
			sent = true;
			setProgressBarIndeterminateVisibility(false);
			rfBar(); // 刷新ActionBar

			switch (msg.what) {
			case GOT_UPDATE_INFO: {
				final WeiboBackBean update = (WeiboBackBean) msg.obj;

				if (update.getId() == null) {
					checkError(update);
				} else {
					clearDraft();
					tv_post_info.setVisibility(View.VISIBLE);
					tv_post_info.startAnimation(a_in);
					tv_post_info.setText("SUCCESS!\n发表于 - "
							+ update.getCreatedAt() + "\nID - "
							+ update.getId() + "\n> 点击这里查看微博 <");
					tv_post_info.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							String mid = Id2MidUtil.Id2Mid(update.getId());
							Uri link = Uri.parse("http://weibo.com/"
									+ WeiboConstant.UID + "/" + mid);
							startActivity(new Intent(Intent.ACTION_VIEW, link));
						}
					});

					editor.putString(DRAFT, "");
					editor.commit();
				}
				break;
			}
			case GOT_UPLOAD_INFO: {
				final WeiboBackBean upload = (WeiboBackBean) msg.obj;
				if (upload.getId() == null) {
					checkError(upload);
				} else {
					clearDraft();
					final String picURL = upload.getOriginalPic();
					tv_post_info.setVisibility(View.VISIBLE);
					tv_post_info.startAnimation(a_in);
					tv_post_info.setText("SUCCESS!\n发表于 - "
							+ upload.getCreatedAt() + "\nID - "
							+ upload.getId() + "\n图片 - " + picURL
							+ "\n> 点这里查看微博 <");
					tv_post_info.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							String mid = Id2MidUtil.Id2Mid(upload.getId());
							Uri link = Uri.parse("http://weibo.com/"
									+ WeiboConstant.UID + "/" + mid);
							startActivity(new Intent(Intent.ACTION_VIEW, link));
						}
					});
				}
				break;
			}
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Log.e(TAG, "on Activity Result OK");
			switch (requestCode) {
			case ACT_GOT_AT: {
				insertString("@" + data.getExtras().getString(KEY_AT_USER)
						+ " ", true);
				break;
			}
			case ACT_GOT_PHOTO: {
				picFile = new File(getFilePath(data.getData()));
				rfBar(); // 刷新ActionBar
				break;
			}
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private String getFilePath(Uri uri) {
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(uri, filePathColumn, null,
				null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String picPath = cursor.getString(columnIndex);
		cursor.close();
		Log.e(TAG, "File Path: " + picPath);
		return picPath;
	}

	private void jumpToLogin() {
		startActivity(new Intent(Post.this, Login.class));
		finish();
	}

	private void saveDraft() {
		if (!sent && !(et_post.getText().toString().trim()).equals("")) {
			draft = et_post.getText().toString();
			ContentValues cv = new ContentValues();
			cv.put(sqlHelper.DRAFT, draft);
			if (sql.update(sqlHelper.tableName, cv, sqlHelper.UID + "='"
					+ WeiboConstant.UID + "'", null) != 0) {
				Log.e(TAG_SQL, "Saved draft: " + draft);
			}
		}
	}

	private void clearDraft() {
		ContentValues cv = new ContentValues();
		cv.put(sqlHelper.DRAFT, "");
		if (sql.update(sqlHelper.tableName, cv, sqlHelper.UID + "='"
				+ WeiboConstant.UID + "'", null) != 0) {
			Log.e(TAG_SQL, "Cleared draft");
		}
	}

	private void rfBar() {
		getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL); // 刷新ActionBar
	}

	@Override
	protected void onPause() {
		Log.e(TAG, "PAUSE");
		super.onPause();

		saveDraft();
	}

	@Override
	protected void onStop() {
		Log.e(TAG, "STOP");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.e(TAG, "DESTROY");
		super.onDestroy();
	}

	private String checkError(WeiboBackBean error) {
		String errorCode = error.getErrorCode();
		Log.e(TAG, "HANDLER GOT ERROR CODE: " + errorCode);

		if (errorCode.equals("21315") || errorCode.equals("21314")
				|| errorCode.equals("21316") || errorCode.equals("21317")
				|| errorCode.equals("21301") || errorCode.equals("21332")) {
			Toast.makeText(Post.this, "身份认证过期…请重新登录", Toast.LENGTH_LONG).show();
			saveDraft();
			jumpToLogin();
		} else if (errorCode.equals("20019") || errorCode.equals("20017")) {
			tv_post_info.setVisibility(View.VISIBLE);
			tv_post_info.setText("ERROR!\n不能重复发同一个Po哦\nError - "
					+ error.getError());
		} else if (errorCode.equals("233")) {
			tv_post_info.setVisibility(View.VISIBLE);
			tv_post_info.setText("ERROR!\n目测超字数了…缩句吧~\nError - "
					+ error.getError());
		}
		return error.getError();
	}

	private void insertString(String toInert, boolean toInsertEnd) {
		int selection = et_post.getSelectionStart();
		Log.e(TAG, String.valueOf(selection));
		String inEt = et_post.getText().toString();
		String beforeSel = inEt.substring(0, selection);
		String afterSel = inEt.substring(selection);
		Log.e(TAG, beforeSel);
		Log.e(TAG, afterSel);
		et_post.setText(beforeSel + toInert + afterSel);
		if (toInsertEnd) {
			et_post.setSelection((beforeSel + toInert).length());
		} else {
			et_post.setSelection(beforeSel.length() + 1);
		}
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

	public void clearWeiboConstant() {
		WeiboConstant.AUTH_CODE = null;
		WeiboConstant.ACCESS_TOKEN = null;
		WeiboConstant.SCREEN_NAME = null;
		WeiboConstant.UID = null;
		WeiboConstant.LOCATION = null;
	}

	public void saveSharingInfo(Intent intent, String type, String action) {
		if (Intent.ACTION_SEND.equals(action) && type != null) {
			Log.e(TAG, "14");
			if ("text/plain".equals(type)) { // 传入文件为文字
				Log.e(TAG, "15 TEXT");
				WeiboConstant.WORDS = intent.getStringExtra(Intent.EXTRA_TEXT);
			} else if (type.startsWith("image/")) { // 传入文件为图片
				Log.e(TAG, "16 PHOTO");
				WeiboConstant.PICPATH = (String) getFilePath((Uri) intent
						.getParcelableExtra(Intent.EXTRA_STREAM));
			}
		}

	}
}
