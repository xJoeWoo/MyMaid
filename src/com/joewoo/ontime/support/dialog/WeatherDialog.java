package com.joewoo.ontime.support.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.joewoo.ontime.R;
import com.joewoo.ontime.action.aqi.AQIDetails;
import com.joewoo.ontime.support.setting.MyMaidSettingsHelper;
import com.joewoo.ontime.support.view.dialog.WeatherDialogView;
import com.joewoo.ontime.ui.Post;

/**
 * Created by Joe on 14-2-7.
 */
public class WeatherDialog {

    public static void show(final boolean isLogin, final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final WeatherDialogView wdv = new WeatherDialogView(context);
        builder.setView(wdv);
        builder.setTitle(R.string.title_weather);
        builder.setPositiveButton(R.string.frag_ftl_dialog_confirm_logout_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyMaidSettingsHelper.save(MyMaidSettingsHelper.WEATHER_CITY, wdv.getCity());
                MyMaidSettingsHelper.save(MyMaidSettingsHelper.WEATHER_STATUS, wdv.getCheck());
                if (wdv.getCheck())
                    new AQIDetails().start();
                if (isLogin)
                    context.startActivity(new Intent(context, Post.class));
            }
        });
        builder.setNegativeButton(R.string.frag_ftl_dialog_confirm_logout_btn_cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isLogin)
                    context.startActivity(new Intent(context, Post.class));
            }
        });
        builder.setCancelable(false);

        builder.show();
    }

}
