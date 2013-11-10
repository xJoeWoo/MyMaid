package com.joewoo.ontime;

import java.io.File;

import com.joewoo.ontime.action.Weibo_Update;
import com.joewoo.ontime.action.Weibo_Upload;
import com.joewoo.ontime.bean.WeiboBackBean;
import com.joewoo.ontime.info.Weibo_Constants;
import com.joewoo.ontime.tools.MyMaidSQLHelper;
import com.joewoo.ontime.tools.MyMaidUtilities;

import static com.joewoo.ontime.info.Constants.*;

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

	MyMaidSQLHelper sqlHelper = new MyMaidSQLHelper(Post.this, MyMaidSQLHelper.SQL_NAME, null,
            MyMaidSQLHelper.SQL_VERSION);
	SQLiteDatabase sql;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.post);
		setProgressBarIndeterminateVisibility(false);
		findViews();

		Log.e(TAG, "Post Weibo");

		getActionBar().setDisplayUseLogoEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		sql = sqlHelper.getWritableDatabase();

		preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
		editor = preferences.edit();

		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		if (!intent.getBooleanExtra(IS_FRAG_POST, false)) {

			if (preferences.getString(LASTUID, null) != null) {

				String lastUid = preferences.getString(LASTUID, null);

				Cursor c = sql.query(MyMaidSQLHelper.tableName, new String[] {
						MyMaidSQLHelper.UID, MyMaidSQLHelper.ACCESS_TOKEN,
						MyMaidSQLHelper.LOCATION, MyMaidSQLHelper.EXPIRES_IN,
						MyMaidSQLHelper.SCREEN_NAME, MyMaidSQLHelper.DRAFT,
						MyMaidSQLHelper.PROFILEIMG }, MyMaidSQLHelper.UID + "=?",
						new String[] { lastUid }, null, null, null);

				Log.e(TAG, "2");

				Log.e(TAG, "3");

				if (c.moveToFirst()) {

					Log.e(TAG, "4");

					Weibo_Constants.UID = c.getString(c
							.getColumnIndex(MyMaidSQLHelper.UID));
					Weibo_Constants.ACCESS_TOKEN = c.getString(c
							.getColumnIndex(MyMaidSQLHelper.ACCESS_TOKEN));
					Weibo_Constants.LOCATION = c.getString(c
							.getColumnIndex(MyMaidSQLHelper.LOCATION));
					Weibo_Constants.EXPIRES_IN = Integer.valueOf(c.getString(c
							.getColumnIndex(MyMaidSQLHelper.EXPIRES_IN)));
					Weibo_Constants.SCREEN_NAME = c.getString(c
							.getColumnIndex(MyMaidSQLHelper.SCREEN_NAME));

					byte[] profileImg = c.getBlob(c
							.getColumnIndex(MyMaidSQLHelper.PROFILEIMG));
					getActionBar().setLogo(
							new BitmapDrawable(getResources(), BitmapFactory
									.decodeByteArray(profileImg, 0,
											profileImg.length)));

					Log.e(TAG, "Weibo_Constants: " + Weibo_Constants.ACCESS_TOKEN
							+ Weibo_Constants.SCREEN_NAME + Weibo_Constants.UID
							+ Weibo_Constants.LOCATION);

					if ((draft = c.getString(c.getColumnIndex(MyMaidSQLHelper.DRAFT))) != null) {
						Log.e(TAG, "5");
						if (!"".equals(draft) && draft != null) {
							Log.e(TAG, "Draft: " + draft);
							et_post.setText(draft);
							et_post.setSelection(draft.length());
						}
					}

					Log.e(TAG, "6");

					if (Weibo_Constants.PICPATH != null
							|| Weibo_Constants.WORDS != null) {
						Log.e(TAG, Weibo_Constants.PICPATH + Weibo_Constants.WORDS);
						if (Weibo_Constants.PICPATH != null) {
							picFile = new File(Weibo_Constants.PICPATH);
							rfBar();
						} else if (Weibo_Constants.WORDS != null) {
							et_post.setText(Weibo_Constants.WORDS);
						}
					}

					Log.e(TAG, "7");
					if (Intent.ACTION_SEND.equals(action) && type != null) {// 分享到此Activity
						Log.e(TAG, "8");
//						Toast.makeText(
//								Post.this,
//								getResources().getString(
//										R.string.toast_post_welcome_part_1)
//										+ Weibo_Constants.LOCATION
//										+ getResources()
//												.getString(
//														R.string.toast_post_welcome_part_2)
//										+ Weibo_Constants.SCREEN_NAME,
//								Toast.LENGTH_LONG).show();
						Log.e(TAG, "9");
						if ("text/plain".equals(type)) { // 传入文件为文字
							Log.e(TAG, "10 TEXT");
							et_post.setText(intent
									.getStringExtra(Intent.EXTRA_TEXT));
							et_post.setSelection(et_post.getText().length());
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
						if (Weibo_Constants.SCREEN_NAME != null
								&& Weibo_Constants.LOCATION != null) {
							Log.e(TAG, "12");
//							Toast.makeText(
//									Post.this,
//									getResources().getString(
//											R.string.toast_post_welcome_part_1)
//											+ Weibo_Constants.LOCATION
//											+ getResources()
//													.getString(
//															R.string.toast_post_welcome_part_2)
//											+ Weibo_Constants.SCREEN_NAME,
//									Toast.LENGTH_LONG).show();
						}
					}
				} else {// 没有查询到数据库信息
					Log.e(TAG, "17");
					saveSharingInfo(intent, type, action);
					Toast.makeText(Post.this, R.string.toast_login_acquired,
							Toast.LENGTH_LONG).show();
					jumpToLogin();
				}
			} else {// 不存在配置文件，需要登录
				Log.e(TAG, "18");
				saveSharingInfo(intent, type, action);
				Toast.makeText(Post.this, R.string.toast_login_acquired,
						Toast.LENGTH_LONG).show();
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
			Cursor c = sql.query(MyMaidSQLHelper.tableName,
					new String[] { MyMaidSQLHelper.DRAFT }, MyMaidSQLHelper.UID + "=?",
					new String[] { Weibo_Constants.UID }, null, null, null);
			Log.e(TAG, "22");
			if (c.moveToFirst()) {
				if ((draft = c.getString(c.getColumnIndex(MyMaidSQLHelper.DRAFT))) != null) {
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

		setTitle(R.string.title_act_post);
		getActionBar().setSubtitle(Weibo_Constants.SCREEN_NAME);

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
			menu.add(0, MENU_ADD, 0, R.string.menu_image_clear)
					.setIcon(R.drawable.content_picture_ok)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		} else {
			menu.add(0, MENU_ADD, 0, R.string.menu_image_add)
					.setIcon(R.drawable.content_picture)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}

		// menu.add(0, MENU_AT, 0, R.string.action_at)
		// .setIcon(R.drawable.ic_menu_btn_at)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		menu.add(0, MENU_AT, 0, R.string.menu_at)
				.setIcon(R.drawable.social_group)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		menu.add(0, MENU_EMOTION, 0, R.string.menu_emotion)
				.setIcon(R.drawable.ic_menu_emoticons)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		menu.add(0, MENU_TOPIC, 0, R.string.menu_topic);

		if (!sending) {
			menu.add(0, MENU_POST, 0, R.string.menu_post)
					.setIcon(R.drawable.social_send_now)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		} else {
			// menu.add(0, MENU_POST, 0, R.string.action_post).setEnabled(false)
			// .setIcon(R.drawable.send)
			// .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}

		menu.add(0, MENU_CLEAR_DRAFT, 0, R.string.menu_draft_clear);
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
				Toast.makeText(Post.this,
						R.string.toast_press_again_to_clear_text,
						Toast.LENGTH_SHORT).show();
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
					Toast.makeText(Post.this,
							R.string.toast_press_again_to_clear_img,
							Toast.LENGTH_SHORT).show();
					downTime = System.currentTimeMillis();
				} else {
					picFile = null;
				}
			}
			rfBar();
			break;
		}
		case MENU_POST: {
			if (MyMaidUtilities.isNetworkAvailable(this)) {
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
					Toast.makeText(Post.this, R.string.toast_say_sth,
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(Post.this, R.string.toast_no_network,
						Toast.LENGTH_SHORT).show();
			}

			break;
		}
		// case MENU_LOGOUT: {
		//
		// editor.putString(LASTUID, "");
		// editor.commit();
		//
		// if (sql.delete(MyMaidSQLHelper.tableName, MyMaidSQLHelper.UID + "=?",
		// new String[] { Weibo_Constants.UID }) > 0) {
		// Log.e(TAG_SQL, "LOGOUT - Cleared user info");
		// }
		//
		// clearWeiboConstant();
		//
		// Toast.makeText(Post.this, "<(￣︶￣)>", Toast.LENGTH_SHORT).show();
		// Intent i = new Intent();
		// i.setClass(Post.this, Start.class);
		// startActivity(i);
		// finish();
		// break;
		// }
		case MENU_CLEAR_DRAFT: {
			clearDraft();
			Toast.makeText(Post.this, R.string.toast_clear_draft_success,
					Toast.LENGTH_SHORT).show();
			break;
		}
		case MENU_AT: {
			if (MyMaidUtilities.isNetworkAvailable(this))
				startActivityForResult(new Intent(Post.this, At.class),
						ACT_GOT_AT);
			else {
				Toast.makeText(Post.this, R.string.toast_no_network_to_at,
						Toast.LENGTH_SHORT).show();
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
			rfBar();

			switch (msg.what) {
			case GOT_UPDATE_INFO: {
				final WeiboBackBean update = (WeiboBackBean) msg.obj;

				if (update.getId() == null) {
					checkError(update);
				} else {
					clearDraft();
					tv_post_info.setVisibility(View.VISIBLE);
					tv_post_info.startAnimation(a_in);
					tv_post_info.setText(getResources().getString(R.string.post_send_success_part_1)
							+ update.getCreatedAt()
							+ getResources().getString(R.string.post_send_success_part_2)
							+ update.getId()
							+ getResources().getString(R.string.post_send_success_part_3));
					tv_post_info.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							String mid = MyMaidUtilities.Id2Mid(update.getId());
							Uri link = Uri.parse("http://weibo.com/"
									+ Weibo_Constants.UID + "/" + mid);
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
					tv_post_info.setText(getResources().getString(R.string.post_send_success_part_1)
							+ upload.getCreatedAt()
							+ getResources().getString(R.string.post_send_success_part_2)
							+ upload.getId()
							+ getResources().getString(R.string.post_send_success_part_img) + picURL
							+ getResources().getString(R.string.post_send_success_part_3));
					tv_post_info.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							String mid = MyMaidUtilities.Id2Mid(upload.getId());
							Uri link = Uri.parse("http://weibo.com/"
									+ Weibo_Constants.UID + "/" + mid);
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
				rfBar();
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
			cv.put(MyMaidSQLHelper.DRAFT, draft);
			if (sql.update(MyMaidSQLHelper.tableName, cv, MyMaidSQLHelper.UID + "='"
					+ Weibo_Constants.UID + "'", null) != 0) {
				Log.e(MyMaidSQLHelper.TAG_SQL, "Saved draft: " + draft);
			}
		}
	}

	private void clearDraft() {
		ContentValues cv = new ContentValues();
		cv.put(MyMaidSQLHelper.DRAFT, "");
		if (sql.update(MyMaidSQLHelper.tableName, cv, MyMaidSQLHelper.UID + "='"
				+ Weibo_Constants.UID + "'", null) != 0) {
			Log.e(MyMaidSQLHelper.TAG_SQL, "Cleared draft");
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
			Toast.makeText(Post.this, R.string.error_token_out_of_date,
					Toast.LENGTH_LONG).show();
			saveDraft();
			jumpToLogin();
		} else if (errorCode.equals("20019") || errorCode.equals("20017")) {
			tv_post_info.setVisibility(View.VISIBLE);
			tv_post_info.setText(R.string.error_repeat_content
					+ error.getError());
		} else if (errorCode.equals("233")) {
			tv_post_info.setVisibility(View.VISIBLE);
			tv_post_info.setText(R.string.error_out_of_max_text_count
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


	public void saveSharingInfo(Intent intent, String type, String action) {
		if (Intent.ACTION_SEND.equals(action) && type != null) {
			Log.e(TAG, "14");
			if ("text/plain".equals(type)) { // 传入文件为文字
				Log.e(TAG, "15 TEXT");
				Weibo_Constants.WORDS = intent.getStringExtra(Intent.EXTRA_TEXT);
			} else if (type.startsWith("image/")) { // 传入文件为图片
				Log.e(TAG, "16 PHOTO");
				Weibo_Constants.PICPATH = (String) getFilePath((Uri) intent
						.getParcelableExtra(Intent.EXTRA_STREAM));
			}
		}

	}
}
