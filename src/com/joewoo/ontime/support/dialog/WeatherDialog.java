package com.joewoo.ontime.support.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import com.joewoo.ontime.R;
import com.joewoo.ontime.action.weather.Weather;
import com.joewoo.ontime.support.setting.MyMaidSettingsHelper;
import com.joewoo.ontime.support.view.dialog.WeatherDialogView;
import com.joewoo.ontime.ui.maintimeline.MainTimelineActivity;

/**
 * Created by Joe on 14-2-7.
 */
public class WeatherDialog {

    public static void show(final boolean isLogin, final Activity act) {
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        final WeatherDialogView wdv = new WeatherDialogView(act);
        if (isLogin)
            wdv.setCheck(true);
        builder.setView(wdv);
        builder.setTitle(R.string.dialog_weather_city_title);
        builder.setPositiveButton(R.string.dialog_weather_city_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyMaidSettingsHelper.save(MyMaidSettingsHelper.WEATHER_CITY, wdv.getCity());
                MyMaidSettingsHelper.save(MyMaidSettingsHelper.WEATHER_STATUS, wdv.getCheck());
                if (wdv.getCheck())
                    new Weather().start();
                if (isLogin) {
                    act.startActivity(new Intent(act, MainTimelineActivity.class));
                    act.finish();
                }
            }
        });
        builder.setNegativeButton(R.string.dialog_weather_city_btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isLogin) {
                    act.startActivity(new Intent(act, MainTimelineActivity.class));
                    act.finish();
                }
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

}
