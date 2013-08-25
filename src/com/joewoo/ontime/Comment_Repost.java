package com.joewoo.ontime;

import static com.joewoo.ontime.info.Defines.*;

import com.joewoo.ontime.action.Weibo_CommentCreate;
import com.joewoo.ontime.action.Weibo_Reply;
import com.joewoo.ontime.action.Weibo_Repost;
import com.joewoo.ontime.bean.WeiboBackBean;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class Comment_Repost extends Activity {

	long downTime;
	EditText et;
	boolean sending;
	String weibo_id;
	String comment_id;
	boolean isComment;
	boolean isReply;
	boolean isRepost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.comment);
		et = (EditText) findViewById(R.id.comment_et);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Intent i = getIntent();

		isComment = i.getBooleanExtra(IS_COMMENT, false);
		isReply = i.getBooleanExtra(IS_REPLY, false);
		isRepost = i.getBooleanExtra(IS_REPOST, false);
		
		weibo_id = i.getStringExtra(WEIBO_ID);

		if (isRepost) {
			setTitle("转发");
			if (i.getStringExtra(TEXT) != null) {
				et.setText(i.getStringExtra(TEXT));
				et.setSelection(0);
			}
		} else if (isReply) {
			setTitle("回复");
			comment_id = i.getStringExtra(COMMENT_ID);
		} else if (isComment) {
			setTitle("评论");
		}

		if (weibo_id != null)
			Log.e(TAG, "Weibo to comment id: " + weibo_id);
		if (comment_id != null)
			Log.e(TAG, "Replay comment id: " + comment_id);

		et.addTextChangedListener(new TextWatcher() {
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
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		menu.add(0, MENU_LETTERS, 0,
				String.valueOf(140 - et.getText().length())).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS);

		menu.add(0, MENU_AT, 0, "@人").setIcon(R.drawable.social_group)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		menu.add(0, MENU_EMOTION, 0, R.string.action_add_emotion)
				.setIcon(R.drawable.ic_menu_emoticons)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		menu.add(0, MENU_TOPIC, 0, R.string.action_add_topic)
				.setIcon(R.drawable.collections_labels)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		menu.add(0, 1000, 0, "转发");

		if (!sending) {
			menu.add(0, MENU_POST, 0, R.string.action_post)
					.setIcon(R.drawable.social_send_now)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		} else {
			// menu.add(0, MENU_POST, 0, R.string.action_post).setEnabled(false)
			// .setIcon(R.drawable.send)
			// .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}

//		menu.add(0, MENU_FAVOURITE_CREATE, 0,
//				getString(R.string.menu_favourite_create));

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: {
			finish();
			break;
		}
//		case MENU_FAVOURITE_CREATE: {
//			rfBar(); // 刷新ActionBar
//			sending = true;
//			new Weibo_FavoritesCreate(weibo_id, mHandler).start();
//			setProgressBarIndeterminateVisibility(true);
//			break;
//		}
		case MENU_LETTERS: {
			if (System.currentTimeMillis() - downTime > 2000) {
				Toast.makeText(Comment_Repost.this, "再按一次清除文字",
						Toast.LENGTH_SHORT).show();
				downTime = System.currentTimeMillis();
			} else {
				et.setText("");
			}
			break;
		}

		case MENU_POST: {

			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
			if (!"".equals(et.getText().toString().trim())) {
				sending = true;
				rfBar(); // 刷新ActionBar

				if (isComment)
					new Weibo_CommentCreate(et.getText().toString(), weibo_id,
							mHandler).start();
				else if (isRepost)
					new Weibo_Repost(et.getText().toString(), weibo_id,
							mHandler).start();
				else if (isReply)
					new Weibo_Reply(et.getText().toString(), weibo_id,
							comment_id, mHandler).start();

				setProgressBarIndeterminateVisibility(true);

			} else {
				Toast.makeText(Comment_Repost.this, "说点什么吧", Toast.LENGTH_SHORT)
						.show();
			}

			break;
		}
		case 1000: {
			if (!"".equals(et.getText().toString().trim())) {
				sending = true;
				rfBar(); // 刷新ActionBar
				new Weibo_Repost(et.getText().toString(), weibo_id, mHandler)
						.start();
				setProgressBarIndeterminateVisibility(true);
			} else {
				Toast.makeText(Comment_Repost.this, "说点什么吧", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		}
		case MENU_AT: {

			startActivityForResult(new Intent(Comment_Repost.this, At.class),
					ACT_GOT_AT);

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
			setProgressBarIndeterminateVisibility(false);
			rfBar(); // 刷新ActionBar
			WeiboBackBean b = (WeiboBackBean) msg.obj;

			switch (msg.what) {
			case GOT_COMMENT_CREATE_INFO: {

				if (b.getId() != null) {
					Toast.makeText(Comment_Repost.this, "评论成功",
							Toast.LENGTH_SHORT).show();
					finish();
				} else {
					Toast.makeText(Comment_Repost.this, "评论失败…",
							Toast.LENGTH_SHORT).show();
				}
				break;
			}
			case GOT_REPOST_INFO: {

				if (b.getId() != null) {
					Toast.makeText(Comment_Repost.this, "转发成功",
							Toast.LENGTH_SHORT).show();
					finish();
				} else {
					Toast.makeText(Comment_Repost.this, "转发失败…",
							Toast.LENGTH_SHORT).show();
				}
				break;
			}
//			case GOT_FAVOURITE_CREATE_INFO: {
//
//				if (b.getFavoritedTime() != null) {
//					Toast.makeText(Comment_Repost.this, "收藏成功",
//							Toast.LENGTH_SHORT).show();
//					finish();
//				} else {
//					Toast.makeText(Comment_Repost.this, "收藏失败…",
//							Toast.LENGTH_SHORT).show();
//				}
//				break;
//			}
			case GOT_REPLY_INFO: {

				if (b.getId() != null) {
					Toast.makeText(Comment_Repost.this, "回复成功",
							Toast.LENGTH_SHORT).show();
					finish();
				} else {
					Toast.makeText(Comment_Repost.this, "回复失败…",
							Toast.LENGTH_SHORT).show();
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
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void insertString(String toInert, boolean toInsertEnd) {
		int selection = et.getSelectionStart();
		Log.e(TAG, String.valueOf(selection));
		String inEt = et.getText().toString();
		String beforeSel = inEt.substring(0, selection);
		String afterSel = inEt.substring(selection);
		Log.e(TAG, beforeSel);
		Log.e(TAG, afterSel);
		et.setText(beforeSel + toInert + afterSel);
		if (toInsertEnd) {
			et.setSelection((beforeSel + toInert).length());
		} else {
			et.setSelection(beforeSel.length() + 1);
		}
	}

	private void rfBar() {
		getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL); // 刷新ActionBar
	}

}
