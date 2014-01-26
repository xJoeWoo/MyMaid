package com.joewoo.ontime.ui.settings;

import android.app.Activity;
import android.os.Bundle;

import com.joewoo.ontime.R;

/**
 * Created by Joe on 14-1-24.
 */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_settings);

        getFragmentManager().beginTransaction()
                .replace(R.id.act_settings_rl, new SettingsFragment())
                .commit();
    }
}
