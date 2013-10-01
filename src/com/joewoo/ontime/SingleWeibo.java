package com.joewoo.ontime;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.joewoo.ontime.action.Weibo_CommentsShow;
import com.joewoo.ontime.action.Weibo_DownloadPic;
import com.joewoo.ontime.action.Weibo_FavoritesCreate;
import com.joewoo.ontime.action.Weibo_StatusesDestroy;
import com.joewoo.ontime.bean.WeiboBackBean;
import com.joewoo.ontime.info.WeiboConstant;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import static com.joewoo.ontime.info.Defines.*;

@SuppressLint("HandlerLeak")
public class SingleWeibo extends Activity {

	String repostReason;

	Intent i = null;

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
	ImageView iv_profile_image;
	ListView lv_comments;
	ProgressBar pb;
	Weibo_DownloadPic dp;
	ProgressBar pb_comments_loading;
	ScrollView sv;
	TextView tv_divider;
	long downTime = 0;
	public static SingleWeibo _instance = null;

	File cache;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_instance = this;
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.singleweibo);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		findViews();

		i = getIntent();

		tv_screen_name.setText(i.getStringExtra(SCREEN_NAME));
		tv_created_at.setText(i.getStringExtra(CREATED_AT));

		tv_text.setText(i.getStringExtra(TEXT));

		tv_comments_count.setText(i.getStringExtra(COMMENTS_COUNT));
		new Weibo_CommentsShow(i.getStringExtra(WEIBO_ID), mHandler).start();

		tv_reposts_count.setText(i.getStringExtra(REPOSTS_COUNT));
		tv_source.setText(i.getStringExtra(SOURCE));

		if (i.getStringExtra(BMIDDLE_PIC) == null)
			iv_image.setVisibility(View.GONE);
		else {

			dp = new Weibo_DownloadPic(iv_image, pb);
			dp.execute(i.getStringExtra(BMIDDLE_PIC));

		}

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
			if (i.getStringExtra(RETWEETED_STATUS_BMIDDLE_PIC) == null)
				iv_rt_image.setVisibility(View.GONE);
			else {

				new Weibo_DownloadPic(iv_rt_image, pb).execute(i
						.getStringExtra(RETWEETED_STATUS_BMIDDLE_PIC));

			}
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

		iv_profile_image.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new Weibo_DownloadPic(iv_profile_image, pb).execute(i
						.getStringExtra(PROFILE_IMAGE_URL));
				iv_profile_image.setClickable(false);
			}
		});

		if (i.getStringExtra(USER_WEIBO) == null) {
			tv_screen_name.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					jumpToSingleUser(i);
				}
			});
			tv_created_at.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					jumpToSingleUser(i);
				}
			});
		}

	}

	public boolean onPrepareOptionsMenu(Menu menu) {

		menu.clear();

		if (i.getStringExtra(SCREEN_NAME).equals(WeiboConstant.SCREEN_NAME)) {
			menu.add(0, MENU_STATUSES_DESTROY, 0, R.string.menu_delete)
					.setIcon(R.drawable.content_discard)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}

		menu.add(0, MENU_FAVOURITE_CREATE, 0, R.string.menu_add_favourite)
				.setIcon(R.drawable.rating_favorite)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		menu.add(0, MENU_REPOST, 0, R.string.menu_repost).setIcon(R.drawable.social_reply)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		menu.add(0, MENU_COMMENT_CREATE, 0, R.string.menu_comment)
				.setIcon(R.drawable.content_edit)
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
		case MENU_REPOST: {
			Intent it = new Intent();
			it.setClass(SingleWeibo.this, Comment_Repost.class);
			if (i.getStringExtra(IS_REPOST) != null)
				it.putExtra(TEXT, "//@" + i.getStringExtra(SCREEN_NAME) + ":"
						+ i.getStringExtra(TEXT));
			it.putExtra(IS_REPOST, true);
			it.putExtra(WEIBO_ID, i.getStringExtra(WEIBO_ID));
			startActivity(it);
			break;
		}
		case MENU_COMMENT_CREATE: {
			Intent it = new Intent();
			it.setClass(SingleWeibo.this, Comment_Repost.class);
			it.putExtra(IS_COMMENT, true);
			it.putExtra(WEIBO_ID, i.getStringExtra(WEIBO_ID));
			startActivity(it);
			break;
		}
		case MENU_FAVOURITE_CREATE: {
			new Weibo_FavoritesCreate(i.getStringExtra(WEIBO_ID), mHandler)
					.start();
			setProgressBarIndeterminateVisibility(true);
			break;
		}
		case MENU_STATUSES_DESTROY: {
			if (System.currentTimeMillis() - downTime > 2000) {
				Toast.makeText(SingleWeibo.this, R.string.toast_press_again_to_delete_statuse, Toast.LENGTH_SHORT)
						.show();
				downTime = System.currentTimeMillis();
			} else {
				new Weibo_StatusesDestroy(i.getStringExtra(WEIBO_ID), mHandler)
						.start();
				setProgressBarIndeterminateVisibility(true);
			}

			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}

	Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {

			setProgressBarIndeterminateVisibility(false);

			switch (msg.what) {
			case GOT_FAVOURITE_CREATE_INFO: {

				WeiboBackBean b = (WeiboBackBean) msg.obj;

				if (b.getFavoritedTime() != null) {
					Toast.makeText(SingleWeibo.this, R.string.toast_add_favourite_success, Toast.LENGTH_SHORT)
							.show();
					finish();
				} else {
					Toast.makeText(SingleWeibo.this, R.string.toast_add_favourite_fail,
							Toast.LENGTH_SHORT).show();
				}
				break;
			}
			case GOT_STATUSES_DESTROY_INFO: {
				WeiboBackBean b = (WeiboBackBean) msg.obj;

				if (b.getId() != null) {
					Toast.makeText(SingleWeibo.this, R.string.toast_delete_success, Toast.LENGTH_SHORT)
							.show();
					finish();
				} else {
					Toast.makeText(SingleWeibo.this, R.string.toast_delete_fail,
							Toast.LENGTH_SHORT).show();
				}
				break;
			}
			case GOT_COMMNETS_SHOW_INFO: {

				ArrayList<HashMap<String, String>> text = (ArrayList<HashMap<String, String>>) msg.obj;

				String[] from = { SCREEN_NAME, TEXT };
				int[] to = { R.id.comments_show_screen_name,
						R.id.comments_show_text };

				SimpleAdapter data = new SimpleAdapter(SingleWeibo.this, text,
						R.layout.comments_show_lv, from, to);

//				int sv_pos = sv.getScrollY();

				pb_comments_loading.setVisibility(View.GONE);

				lv_comments.setAdapter(data);

				if (data.getCount() > 0) {

					int totalHeight = 0;
					for (int i = 0; i < data.getCount(); i++) {
						View listItem = data.getView(i, null, lv_comments);
						listItem.measure(0, 0);
						totalHeight += listItem.getMeasuredHeight() + 8;
					}

					ViewGroup.LayoutParams params = lv_comments
							.getLayoutParams();
					params.height = totalHeight
							+ (lv_comments.getDividerHeight() * (data
									.getCount() - 1));
					lv_comments.setLayoutParams(params);

					sv.scrollTo(0, 0);
				} else {
					tv_divider.setVisibility(View.GONE);
				}

				break;
			}
			}
		}

	};

	private void findViews() {
		pb = (ProgressBar) findViewById(R.id.single_weibo_pb);
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
		iv_rt_image = (ImageView) findViewById(R.id.single_weibo_retweeted_status_weibo_image);
		iv_profile_image = (ImageView) findViewById(R.id.single_weibo_profile_image);
		lv_comments = (ListView) findViewById(R.id.single_weibo_lv_comments);
		pb_comments_loading = (ProgressBar) findViewById(R.id.single_weibo_pb_comments_show);
		sv = (ScrollView) findViewById(R.id.single_weibo_sv);
		tv_divider = (TextView) findViewById(R.id.single_weibo_divider);
	}

	@Override
	protected void onStop() {
		Log.e(TAG, "onStop");

		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.e(TAG, "onDestroy");
		// if (dp != null && dp.getStatus() == AsyncTask.Status.RUNNING) {
		// Log.e(TAG, "onDestroy - cancel dp");
		// dp.cancel(true);
		// }
		if (dp != null) {
			if (dp.getStatus() != AsyncTask.Status.FINISHED) {
				Log.e(TAG, "onDestroy - cancel dp");
				dp.cancel(true);
			}
		}
		super.onDestroy();
	}

	void jumpToSingleUser(Intent i) {
		Intent it = new Intent();
		it.setClass(SingleWeibo.this, SingleUser.class);
		// it.putExtra(UID, i.getStringExtra(UID));
		it.putExtra(SCREEN_NAME, i.getStringExtra(SCREEN_NAME));
		startActivity(it);
	}
}
