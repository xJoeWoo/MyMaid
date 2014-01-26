package com.joewoo.ontime.support.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.info.AcquireCount;
import com.joewoo.ontime.support.info.Defines;
import com.joewoo.ontime.support.setting.MyMaidSettingHelper;

/**
 * Created by Joe on 14-1-24.
 */
public class AcquireCountDialog {

    public static void show(Context context, final String key) {

        int count = -1;

        if(key.equals(MyMaidSettingHelper.KEY_NETWORK_ACQUIRE_COUNT_FRIENDS_TIMELINE)) {
            count = Integer.valueOf(AcquireCount.FRIENDS_TIMELINE_COUNT);
        } else if(key.equals(MyMaidSettingHelper.KEY_NETWORK_ACQUIRE_COUNT_COMMENTS_TO_ME)) {
            count = Integer.valueOf(AcquireCount.COMMENTS_TO_ME_COUNT);
        } else if(key.equals(MyMaidSettingHelper.KEY_NETWORK_ACQUIRE_COUNT_MENTIONS)) {
            count = Integer.valueOf(AcquireCount.MENTIONS_COUNT);
        } else if(key.equals(MyMaidSettingHelper.KEY_NETWORK_ACQUIRE_COUNT_COMMENTS_MENTIONS)) {
            count = Integer.valueOf(AcquireCount.COMMENTS_MENTIONS_COUNT);
        }

        if(count == -1)
            return;

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View v = LayoutInflater.from(context).inflate(R.layout.dialog_acquire_count, null);

//        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View v = inflater.inflate(R.layout.dialog_acquire_count, null);

        builder.setView(v);

        final SeekBar sb = (SeekBar) v.findViewById(R.id.dialog_acquire_count_sb);
        final TextView tv = (TextView) v.findViewById(R.id.dialog_acquire_count_tv);

        sb.setProgress(count - 15);
        tv.setText(String.valueOf(count));

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv.setText(String.valueOf(sb.getProgress() + 15));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AcquireCount.setCount(key, sb.getProgress() + 15);
                MyMaidSettingHelper.save(key, String.valueOf(sb.getProgress() + 15));
            }
        });

        builder.setNegativeButton(android.R.string.cancel, null);

        builder.show();
    }
}
