package com.joewoo.ontime;

import java.io.File;

import com.joewoo.ontime.action.Weibo_FavoritesCreate;
import com.joewoo.ontime.bean.WeiboBackBean;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static com.joewoo.ontime.info.Defines.*;

@SuppressLint("HandlerLeak")
public class SingleWeibo extends Activity {

	String weibo_id;

	TextView tv_profile_img;
	TextView tv_screen_name;
	TextView tv_created_at;
	TextView tv_text;
	TextView tv_rt_rl;
	TextView tv_rt_screen_name;
	TextView tv_rt_text;
	TextView tv_rt_source;
	TextView tv_rt_created_at;
	TextView tv_rt_comments_count;
	TextView tv_rt_reposts_count;
	TextView tv_comments_count;
	TextView tv_reposts_count;
	TextView tv_source;
	ImageView iv_image;
	ImageView iv_rt_image;
	
	File cache;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.singleweibo);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		findViews();

		Intent i = getIntent();

		weibo_id = i.getStringExtra(WEIBO_ID);
		tv_screen_name.setText(i.getStringExtra(SCREEN_NAME));
		tv_created_at.setText(i.getStringExtra(CREATED_AT));
		tv_text.setText(i.getStringExtra(TEXT));
		tv_comments_count.setText(i.getStringExtra(COMMENTS_COUNT));
		tv_reposts_count.setText(i.getStringExtra(REPOSTS_COUNT));
		tv_source.setText(i.getStringExtra(SOURCE));
		if(i.getStringExtra(HAVE_PIC) == null)
			iv_image.setVisibility(View.GONE);

		if (i.getStringExtra(IS_REPOST) == null) {
			tv_rt_rl.setVisibility(View.GONE);
			tv_rt_screen_name.setVisibility(View.GONE);
			tv_rt_created_at.setVisibility(View.GONE);
			tv_rt_text.setVisibility(View.GONE);
			tv_rt_comments_count.setVisibility(View.GONE);
			tv_rt_reposts_count.setVisibility(View.GONE);
			tv_rt_source.setVisibility(View.GONE);
			iv_rt_image.setVisibility(View.GONE);
		} else {
			if(i.getStringExtra(RETWEETED_STATUS_HAVE_PIC) == null)
				iv_rt_image.setVisibility(View.GONE);
			tv_rt_screen_name.setText(i
					.getStringExtra(RETWEETED_STATUS_SCREEN_NAME));
			tv_rt_created_at.setText(i
					.getStringExtra(RETWEETED_STATUS_CREATED_AT));
			tv_rt_text.setText(i.getStringExtra(RETWEETED_STATUS));
			tv_rt_reposts_count.setText(i
					.getStringExtra(RETWEETED_STATUS_REPOSTS_COUNT));
			tv_rt_comments_count.setText(i
					.getStringExtra(RETWEETED_STATUS_COMMENTS_COUNT));
			tv_rt_source.setText(i.getStringExtra(RETWEETED_STATUS_SOURCE));
		}
		
//		cache = new File(Environment.getExternalStorageDirectory(), "cache/MyMaid");
//		
//		if(!cache.exists())
//		{
//			cache.mkdir();
//		}
//		
		

	}

	public boolean onPrepareOptionsMenu(Menu menu) {

		menu.clear();
		
		menu.add(0, MENU_FAVOURITE_CREATE, 0, "收藏")
		.setIcon(R.drawable.rating_important)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		menu.add(0, MENU_REPOST, 0, "转发").setIcon(R.drawable.social_reply)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		menu.add(0, MENU_COMMENT_CREATE, 0, "评论")
				.setIcon(R.drawable.content_edit)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		

		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: {

			break;
		}
		case MENU_REPOST: {
			Intent i = new Intent();
			i.setClass(SingleWeibo.this, Comment_Repost.class);
			i.putExtra(IS_REPOST, true);
			i.putExtra(WEIBO_ID, weibo_id);
			startActivity(i);
			break;
		}
		case MENU_COMMENT_CREATE: {
			Intent i = new Intent();
			i.setClass(SingleWeibo.this, Comment_Repost.class);
			i.putExtra(IS_COMMENT, true);
			i.putExtra(WEIBO_ID, weibo_id);
			startActivity(i);
			break;
		}
		case MENU_FAVOURITE_CREATE: {
			new Weibo_FavoritesCreate(weibo_id, mHandler).start();
			setProgressBarIndeterminateVisibility(true);
			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			setProgressBarIndeterminateVisibility(false);

			switch (msg.what) {
			case GOT_FAVOURITE_CREATE_INFO: {
				
				WeiboBackBean b = (WeiboBackBean) msg.obj;
				
				if (b.getFavoritedTime() != null) {
					Toast.makeText(SingleWeibo.this, "收藏成功",
							Toast.LENGTH_SHORT).show();
					finish();
				} else {
					Toast.makeText(SingleWeibo.this, "收藏失败…",
							Toast.LENGTH_SHORT).show();
				}
				finish();
				break;
			}
			}
		}

	};

	private void findViews() {

		tv_profile_img = (TextView) findViewById(R.id.single_weibo_profile_image);
		tv_screen_name = (TextView) findViewById(R.id.single_weibo_screen_name);
		tv_created_at = (TextView) findViewById(R.id.single_weibo_created_at);
		tv_text = (TextView) findViewById(R.id.single_weibo_text);
		tv_rt_rl = (TextView) findViewById(R.id.single_weibo_retweeted_status_rl);
		tv_rt_screen_name = (TextView) findViewById(R.id.single_weibo_retweeted_status_screen_name);
		tv_rt_created_at = (TextView) findViewById(R.id.single_weibo_retweeted_status_created_at);
		tv_rt_source = (TextView) findViewById(R.id.single_weibo_retweeted_status_source);
		tv_rt_comments_count = (TextView) findViewById(R.id.single_weibo_retweeted_status_comments_count);
		tv_rt_reposts_count = (TextView) findViewById(R.id.single_weibo_retweeted_status_reposts_count);
		tv_rt_text = (TextView) findViewById(R.id.single_weibo_retweeted_status);
		tv_comments_count = (TextView) findViewById(R.id.single_weibo_comments_count);
		tv_reposts_count = (TextView) findViewById(R.id.single_weibo_reposts_count);
		tv_source = (TextView) findViewById(R.id.single_weibo_source);
		iv_image = (ImageView) findViewById(R.id.single_weibo_image);
		iv_rt_image = (ImageView ) findViewById(R.id.single_weibo_retweeted_status_weibo_image);
	}

}
