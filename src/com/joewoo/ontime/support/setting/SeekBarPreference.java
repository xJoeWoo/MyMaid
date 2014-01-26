package com.joewoo.ontime.support.setting;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.info.AcquireCount;

/**
 * Created by Joe on 14-1-24.
 */
public class SeekBarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener {

    private SeekBar sb;
    private TextView tv;

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        sb = (SeekBar) view.findViewById(R.id.dialog_acquire_count_sb);
        tv = (TextView) view.findViewById(R.id.dialog_acquire_count_tv);

//        sb.setProgress();
        sb.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
