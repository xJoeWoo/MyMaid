package com.joewoo.ontime.ui.time;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.joewoo.ontime.R;
import com.joewoo.ontime.action.aqi.Weather;
import com.joewoo.ontime.support.bean.WeatherBean;
import com.joewoo.ontime.support.bean.WeiboBackBean;
import com.joewoo.ontime.support.info.GetupSentences;
import com.joewoo.ontime.support.sql.MyMaidSQLHelper;
import com.joewoo.ontime.support.util.GlobalContext;
import com.joewoo.ontime.support.util.MyMaidUtilites;

import java.util.Calendar;

import static com.joewoo.ontime.support.info.Defines.GOT_UPDATE_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_WEATHER_INFO;
import static com.joewoo.ontime.support.info.Defines.dayNames;

public class Getup extends Activity {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_getup);

        Cursor c = GlobalContext.getSQL().query(MyMaidSQLHelper.USER_TABLE, new String[]{MyMaidSQLHelper.UID,
                MyMaidSQLHelper.ACCESS_TOKEN},
                MyMaidSQLHelper.UID + "=?", new String[]{"3220385287"}, null, null,
                null);


        tv = (TextView) findViewById(R.id.tv_getup);

        c.moveToFirst();
        GlobalContext.setAccessToken(c.getString(c.getColumnIndex(MyMaidSQLHelper.ACCESS_TOKEN)));
        GlobalContext.setUID(c.getString(c.getColumnIndex(MyMaidSQLHelper.UID)));

        new Weather(mHandler).start();

    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GOT_UPDATE_INFO: {
                    final WeiboBackBean update = (WeiboBackBean) msg.obj;

                    if (update.getId() == null) {
                        if (update.getErrorCode().equals("233")) {
                            tv.setText("爆格嘞。。重新运行一下“起床”吧");
                        } else {
                            tv.setText(update.getError());
                        }
                    } else {

                        tv.setText("SUCCESS!\n\n" + update.getText()
                                + "\n\n> 点击这里查看微博 <");
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String mid = MyMaidUtilites.IDtoMID.Id2Mid(update.getId());
                                Uri link = Uri.parse("http://weibo.com/"
                                        + GlobalContext.getUID() + "/" + mid);
                                startActivity(new Intent(Intent.ACTION_VIEW, link));
                            }
                        });
                    }
                    break;
                }
                case GOT_WEATHER_INFO: {
                    WeatherBean w = (WeatherBean) msg.obj;

                    String ampm = "。傍晚#";

                    Calendar c = Calendar.getInstance();
                    Calendar c2 = Calendar.getInstance();
                    c2.clear();
                    c2.set(2014, Calendar.JUNE, 7);

                    long leftTime = (c2.getTimeInMillis() - c.getTimeInMillis()) / 1000;
                    int leftDays = (int) (leftTime / (24 * 60 * 60));

                    c2.clear();
                    c2.set(2014, Calendar.JUNE, 7, 9, 0);

                    leftTime = (c2.getTimeInMillis() - c.getTimeInMillis()) / 1000;

                    if (c.get(Calendar.HOUR_OF_DAY) >= 12) {
                        ampm = "。明早#";
                    }

                    String sentence = GetupSentences.getSentence();

                    String minute;
                    if (c.get(Calendar.MINUTE) < 10) {
                        minute = "0" + String.valueOf(c.get(Calendar.MINUTE));
                    } else {
                        minute = String.valueOf(c.get(Calendar.MINUTE));
                    }

                    String getupWeibo = "#起床时间# #" + c.get(Calendar.HOUR_OF_DAY)
                            + ":" + minute + "# " + (c.get(Calendar.MONTH) + 1)
                            + "月" + c.get(Calendar.DAY_OF_MONTH) + "日 "
                            + dayNames[c.get(Calendar.DAY_OF_WEEK)] + " " + " #" + w.getWeather2() + "#，"
                            + w.getTemp2() + "，吹" + w.getFl2() + "的" + w.getWind2()
                            + "。介样的天气应该感觉" + w.getIndex() + "~ 紫外线"
                            + w.getIndex_uv() + ampm + w.getWeather3() + "#，"
                            + w.getTemp3() + "。离高考还有#" + leftDays + "天#、#"
                            + leftTime + "秒#。 " + sentence + "" + "  (๑>◡<๑) @JouYiu ~";


                    break;

                }

            }
        }

    };

}
