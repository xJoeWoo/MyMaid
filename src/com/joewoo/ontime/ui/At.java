package com.joewoo.ontime.ui;

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
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.joewoo.ontime.R;
import com.joewoo.ontime.action.MyMaidActionHelper;
import com.joewoo.ontime.support.bean.AtSuggestionBean;

import static com.joewoo.ontime.support.info.Defines.GOT_AT_SUGGESTIONS_INFO;
import static com.joewoo.ontime.support.info.Defines.KEY_AT_USER;
import static com.joewoo.ontime.support.info.Defines.TAG;

public class At extends Activity {

    ListView lv;
    ProgressBar pb;
    ArrayAdapter<AtSuggestionBean> files;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            pb.setVisibility(View.GONE);
            switch (msg.what) {
                case GOT_AT_SUGGESTIONS_INFO: {
                    files = (ArrayAdapter<AtSuggestionBean>) msg.obj;
                    lv.setAdapter(files);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_at);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);

        lv = (ListView) findViewById(R.id.lv_at);
        pb = (ProgressBar) findViewById(R.id.pb_at);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                String at = files.getItem(arg2).getNickname();
                Intent i = getIntent();
                Bundle data = new Bundle();
                data.putString(KEY_AT_USER, at);
                i.putExtras(data);
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }

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
                    MyMaidActionHelper.suggestionsAt(user, mHandler, At.this);
                    pb.setVisibility(View.VISIBLE);
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
