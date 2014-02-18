package com.joewoo.ontime.support.view.dialog;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.info.Defines;
import com.joewoo.ontime.support.setting.MyMaidSettingsHelper;

/**
 * Created by Joe on 14-2-7.
 */
public class WeatherDialogView extends RelativeLayout {

    private Switch sw;
    private EditText et;

    public WeatherDialogView(final Context context) {
        super(context);

        View v = LayoutInflater.from(context).inflate(R.layout.dialog_weather_city, null);

        addView(v);

        sw = (Switch) v.findViewById(R.id.sw_dialog_weather_city);
        et = (EditText) v.findViewById(R.id.et_dialog_weather_city);

        setCheck(MyMaidSettingsHelper.getBoolean(MyMaidSettingsHelper.WEATHER_STATUS));
        setCity(MyMaidSettingsHelper.getString(MyMaidSettingsHelper.WEATHER_CITY));


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(et, 0);
            }
        }, Defines.INPUT_SHOW_DELAY);
    }

    public void setCheck(boolean check) {
        sw.setChecked(check);
    }

    public void setCity(String city) {
        if (city != null && !city.equals("")) {
            et.setText(city);
            et.setSelection(et.getText().toString().length());
        }
    }

    public String getCity() {
        return et.getText().toString();
    }

    public boolean getCheck() {
        return sw.isChecked();
    }
}
