package com.joewoo.ontime;

import com.joewoo.ontime.action.Weibo_TimeLine;
import com.joewoo.ontime.tools.MySQLHelper;
import static com.joewoo.ontime.info.Defines.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class TimeLine extends Activity {

	ListView lv;
	TextView tv;

	MySQLHelper sqlHelper = new MySQLHelper(TimeLine.this, "MyMaid.db", null, 1);
	SQLiteDatabase sql;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		new Weibo_TimeLine(25, mHandler).start();

		tv = (TextView) findViewById(R.id.textView1);

	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GOT_FRIENDS_TIMELINE_INFO: {
				String temp = (String)msg.obj;
				if(temp != null && !temp.equals(""))
					tv.setText(temp);
				else
					tv.setText("No Response");

				break;
			}
			}
		}
	};

}
