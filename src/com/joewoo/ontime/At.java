package com.joewoo.ontime;

import com.joewoo.ontime.action.Weibo_AtSuggestions;
import com.joewoo.ontime.bean.AtSuggestionBean;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import static com.joewoo.ontime.info.Constants.*;

@SuppressLint({ "NewApi", "HandlerLeak" })
public class At extends Activity {

	ListView lv;
	// SimpleAdapter sa;
	ArrayAdapter<AtSuggestionBean> files;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.at);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		lv = (ListView) findViewById(R.id.lv_at);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				// String at = sa.getItem(arg2)arg0;
				// Log.e(TAG, at);
				// ListView lv = (ListView)arg0;
				// HashMap<String, String> obj = (HashMap<String,
				// String>)lv.getItemAtPosition(arg2);
				// String at = obj.get(NICKNAME).toString();

				String at = files.getItem(arg2).getNickname();
				Intent i = getIntent();
				Bundle data = new Bundle();
				data.putString(KEY_AT_USER, at);
				i.putExtras(data);
				At.this.setResult(RESULT_OK, i);
				At.this.finish();
			}
		});
	}

	private Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GOT_AT_SUGGESTIONS_INFO: {

				files = (ArrayAdapter<AtSuggestionBean>) msg.obj;
				lv.setAdapter(files);

				// sa = (SimpleAdapter)msg.obj;
				// lv.setAdapter(sa);

			}
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.at, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		final SearchView searchView = (SearchView) searchItem.getActionView();
		searchView.setQueryHint(getString(R.string.action_search));
		searchView.setIconifiedByDefault(false);

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				String user = searchView.getQuery().toString().trim();
				if (!"".equals(user)) {
					Log.e(TAG, user);
					new Weibo_AtSuggestions(user, At.this, mHandler).start();
				}
				return false;
			}
		});

		return super.onCreateOptionsMenu(menu);
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
