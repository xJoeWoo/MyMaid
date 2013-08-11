package com.joewoo.ontime;

import static com.joewoo.ontime.info.Defines.*;

import com.joewoo.ontime.action.Weibo_Comment_Create;
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
	boolean isComment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.comment);
		et = (EditText) findViewById(R.id.comment_et);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Intent i = getIntent();
		
		isComment = i.getBooleanExtra(IS_COMMENT, true);
		
		if(!isComment)
		{
			setTitle("转发");
			if(i.getStringExtra(TEXT) != null)
			{
				et.setText(i.getStringExtra(TEXT));
				et.setSelection(0);
			}
		}
		
		weibo_id = i.getStringExtra(WEIBO_ID);
		Log.e(TAG, "Weibo to comment id: " + weibo_id);		
		
		

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

		menu.add(0, MENU_AT, 0, "@人").setIcon(R.drawable.user)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		menu.add(0, MENU_EMOTION, 0, R.string.action_add_emotion)
				.setIcon(R.drawable.wink)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		menu.add(0, MENU_TOPIC, 0, R.string.action_add_topic)
				.setIcon(R.drawable.bubbles)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
		menu.add(0, 1000, 0, "转发");

		if (!sending) {
			menu.add(0, MENU_POST, 0, R.string.action_post)
					.setIcon(R.drawable.send)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		} else {
			menu.add(0, MENU_POST, 0, R.string.action_post).setEnabled(false)
					.setIcon(R.drawable.send)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}

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
				Toast.makeText(Comment_Repost.this, "再按一次清除文字", Toast.LENGTH_SHORT)
						.show();
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
				if(isComment)
					new Weibo_Comment_Create(et.getText().toString(), weibo_id, mHandler).start();
				else
					new Weibo_Repost(et.getText().toString(), weibo_id, mHandler).start();
			} else {
				Toast.makeText(Comment_Repost.this, "说点什么吧", Toast.LENGTH_SHORT)
						.show();
			}

			break;
		}
		case 1000:{
			if (!"".equals(et.getText().toString().trim())) {
				sending = true;
				rfBar(); // 刷新ActionBar
					new Weibo_Repost(et.getText().toString(), weibo_id, mHandler).start();
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
			rfBar(); // 刷新ActionBar

			switch (msg.what) {
			case GOT_COMMENT_CREATE_INFO:{
				WeiboBackBean b = (WeiboBackBean)msg.obj;
				if(b.getId() != null)
				{
					Toast.makeText(Comment_Repost.this, "评论成功", Toast.LENGTH_SHORT).show();
					finish();
				}else{
					Toast.makeText(Comment_Repost.this, "评论失败…", Toast.LENGTH_SHORT).show();
				}
				break;
			}
			case GOT_REPOST_INFO:{
				WeiboBackBean b = (WeiboBackBean)msg.obj;
				if(b.getId() != null)
				{
					Toast.makeText(Comment_Repost.this, "转发成功", Toast.LENGTH_SHORT).show();
					finish();
				}else{
					Toast.makeText(Comment_Repost.this, "转发失败…", Toast.LENGTH_SHORT).show();
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
