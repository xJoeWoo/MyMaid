package com.joewoo.ontime.tools;

import java.util.ArrayList;
import java.util.HashMap;
import com.joewoo.ontime.R;
import static com.joewoo.ontime.info.Defines.*;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyMaidAdapter extends BaseAdapter {

	private ArrayList<HashMap<String, String>> data;
	private LayoutInflater mInflater;

	public MyMaidAdapter(Context context,
			ArrayList<HashMap<String, String>> data) {
		this.data = data;
		this.mInflater = LayoutInflater.from(context);
	}

	public void addItem(ArrayList<HashMap<String, String>> toAdd) {
		this.data = toAdd;
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class ViewHolder {
		public TextView tv_scr_name;
		public TextView tv_text;
		public TextView tv_rt_rl;
		public TextView tv_rt_scr_name;
		public TextView tv_rt;
		public TextView tv_source;
		public TextView tv_crt_at;
		public TextView tv_cmt_cnt;
		public TextView tv_rpos_cnt;
		public TextView tv_img;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Log.e(TAG, "getView");

		ViewHolder holder = null;

		if (convertView == null) {

			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.friendstimeline_lv_new,
					null);

			// Find views
			holder.tv_scr_name = (TextView) convertView
					.findViewById(R.id.friendstimeline_screen_name);

			holder.tv_text = (TextView) convertView
					.findViewById(R.id.friendstimeline_text);
			// holder.tv_text.setMovementMethod(LinkMovementMethod.getInstance());

			holder.tv_rt_rl = (TextView) convertView
					.findViewById(R.id.friendstimeline_retweeted_status_rl);

			holder.tv_rt_scr_name = (TextView) convertView
					.findViewById(R.id.friendstimeline_retweeted_status_screen_name);

			holder.tv_rt = (TextView) convertView
					.findViewById(R.id.friendstimeline_retweeted_status);
			// holder.tv_rt.setMovementMethod(LinkMovementMethod.getInstance());

			holder.tv_source = (TextView) convertView
					.findViewById(R.id.friendstimeline_source);

			holder.tv_crt_at = (TextView) convertView
					.findViewById(R.id.friendstimeline_created_at);

			holder.tv_cmt_cnt = (TextView) convertView
					.findViewById(R.id.friendstimeline_comments_count);

			holder.tv_rpos_cnt = (TextView) convertView
					.findViewById(R.id.friendstimeline_reposts_count);

			holder.tv_img = (TextView) convertView
					.findViewById(R.id.friendstimeline_have_image);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// Settings
		if (data.get(position).get(THUMBNAIL_PIC) != null
				|| data.get(position).get(RETWEETED_STATUS_THUMBNAIL_PIC) != null)
			holder.tv_img.setVisibility(View.VISIBLE);
		else
			holder.tv_img.setVisibility(View.GONE);

		holder.tv_scr_name.setText(data.get(position).get(SCREEN_NAME));

		// SpannableString tmp = addURLSpan(data.get(position).get(TEXT));
		// // Log.e(TAG, "TEXT: " + data.get(position).get(TEXT));
		// if (tmp != null)
		// holder.tv_text.setText(tmp);
		// else

		holder.tv_text.setText(data.get(position).get(TEXT));

//		checkAtMentionsURL(data.get(position).get(TEXT));

		if (data.get(position).get(IS_REPOST) != null)
			holder.tv_rt_rl.setVisibility(View.VISIBLE);
		else
			holder.tv_rt_rl.setVisibility(View.GONE);

		if (data.get(position).get(RETWEETED_STATUS_SCREEN_NAME) != null) {
			holder.tv_rt_scr_name.setVisibility(View.VISIBLE);
			holder.tv_rt_scr_name.setText(data.get(position).get(
					RETWEETED_STATUS_SCREEN_NAME));
		} else {
			holder.tv_rt_scr_name.setVisibility(View.GONE);
		}

		if (data.get(position).get(RETWEETED_STATUS) != null) {
			holder.tv_rt.setVisibility(View.VISIBLE);
			// tmp = addURLSpan(data.get(position).get(RETWEETED_STATUS));
			// if (tmp != null)
			// holder.tv_rt.setText(tmp);
			// else

			holder.tv_rt.setText(data.get(position).get(RETWEETED_STATUS));
		} else
			holder.tv_rt.setVisibility(View.GONE);

		holder.tv_source.setText(data.get(position).get(SOURCE));

		holder.tv_crt_at.setText(data.get(position).get(CREATED_AT));

		holder.tv_cmt_cnt.setText(data.get(position).get(COMMENTS_COUNT));

		holder.tv_rpos_cnt.setText(data.get(position).get(REPOSTS_COUNT));

		return convertView;
	}

	private SpannableString checkAtMentionsURL(String str) {

		try {
			char strarray[];
			// SpannableString ss = new SpannableString(str);
			strarray = str.toCharArray();

			StringBuffer sb1 = new StringBuffer();
			for (int i = 0; i < str.length(); i++) {
				sb1.append(strarray[i]);
			}
			Log.e(TAG, sb1.toString());

			for (int i = 0; i < str.length(); i++) {
				if (strarray[i] == 'h' && strarray[i + 1] == 't'
						&& strarray[i + 2] == 't' && strarray[i + 3] == 'p'
						&& strarray[i + 4] == ':' && strarray[i + 5] == '/'
						&& strarray[i + 6] == '/') {

					Log.e(TAG, "Found URL");

					StringBuffer sb = new StringBuffer("http://");
					for (int j = i + 7; j <19; j++) {
						if (strarray[j] != ' ') {
							sb.append(strarray[j]);
						} else if (strarray[j] == '\0') {
							Log.e(TAG, "URL - " + sb.toString());
							i = j;
							break;
						} else {
							Log.e(TAG, "URL - " + sb.toString());
							i = j;
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "URL Failed");
		}

		return null;
	}

	public SpannableString addURLSpan(String str) {
		try {
			char strarray[];
			// 处理网址
			SpannableString ss = new SpannableString(str);
			strarray = str.toCharArray();
			int l = str.length() - 10;
			for (int i = 0; i < l; i++) {
				if (strarray[i] == 'h' && strarray[i + 1] == 't'
						&& strarray[i + 2] == 't' && strarray[i + 3] == 'p'
						&& strarray[i + 4] == ':' && strarray[i + 5] == '/'
						&& strarray[i + 6] == '/') {
					StringBuffer sb = new StringBuffer("http://");
					for (int j = i + 7; true; j++) {
						if (strarray[j] != ' '
								|| ((strarray[j] >= 0x4e00) && (strarray[j] <= 0x9fbb))
								|| strarray[j] == '\0')
							sb.append(strarray[j]);
						else {
							Log.e(TAG, "HTTP URL - " + sb.toString());
							ss.setSpan(new URLSpan(sb.toString()), i, j,
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							i = j;
							break;
						}
					}
				}
			}
			// 处理@
			l = str.length();
			StringBuffer sb = null;
			boolean start = false;
			int startIndex = 0;
			for (int i = 0; i < l; i++) {
				if (strarray[i] == '@') {
					start = true;
					sb = new StringBuffer("http://s.weibo.com/weibo/");
					startIndex = i;
				} else {
					if (start) {
						if (strarray[i] == ':' || strarray[i] == ' ') {
							Log.e(TAG, "AT URL - " + sb.toString());
							ss.setSpan(new URLSpan(sb.toString()), startIndex,
									i, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							ss.setSpan(
									new ForegroundColorSpan(Resources
											.getSystem().getColor(
													R.color.mymaid_pink)),
									startIndex, i + 1,
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							sb = null;
							start = false;
						} else {
							sb.append(strarray[i]);
						}
					}
				}

			}
			// 处理话题
			start = false;
			startIndex = 0;
			for (int i = 0; i < l; i++) {
				if (strarray[i] == '#') {
					if (!start) {
						start = true;
						sb = new StringBuffer("http://s.weibo.com/weibo/#");
						startIndex = i;
					} else {
						sb.append('#');
						Log.e(TAG, "TOPIC URL - " + sb.toString());
						ss.setSpan(new URLSpan(sb.toString()), startIndex,
								i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						ss.setSpan(new ForegroundColorSpan(Resources
								.getSystem().getColor(R.color.mymaid_pink)),
								startIndex, i + 1,
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						sb = null;
						start = false;
					}
				} else {
					if (start) {
						sb.append(strarray[i]);
					}
				}
			}

			strarray = null;
			return ss;
		} catch (Exception e) {
			Log.e(TAG, "Exception in adding URL span!" + e.getCause());
		}
		return null;

	}
}
