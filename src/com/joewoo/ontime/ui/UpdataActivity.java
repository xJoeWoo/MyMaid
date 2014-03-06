package com.joewoo.ontime.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.joewoo.ontime.R;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.setting.MyMaidSettingsHelper;

/**
 * Created by Joe on 14-3-2.
 */
public class UpdataActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View v = LayoutInflater.from(this).inflate(R.layout.dialog_update, null);
        TextView tv_details = (TextView) v.findViewById(R.id.tv_dialog_update_details);

        builder.setView(v);

        String[] infos = MyMaidSettingsHelper.getString(MyMaidSettingsHelper.NEW_VERSION).split("\\|");

        builder.setTitle("MyMaid " + infos[0].substring(0, 4) + " (" + infos[1] + "KB)");

        String details = infos[2];
        details = details.replace("_", "\n");
        tv_details.setText(details);

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyMaidSettingsHelper.save(MyMaidSettingsHelper.UPDATED, true);
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(URLHelper.MYMAID_DOWNLOAD));
                startActivity(i);
                finish();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();

    }

}
