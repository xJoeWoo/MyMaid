package com.joewoo.ontime.support.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.joewoo.ontime.R;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.support.setting.MyMaidSettingsHelper;

/**
 * Created by Joe on 14-3-2.
 */
public class UpdataDialog {

    public static void show(String verStr, final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View v = LayoutInflater.from(context).inflate(R.layout.dialog_update, null);
        TextView tv_details = (TextView) v.findViewById(R.id.tv_dialog_update_details);

        builder.setView(v);

        String[] infos = verStr.split("\\|");

        builder.setTitle("MyMaid " + infos[0].substring(0, 4) + " (" + infos[1] + "KB)");

        String details = infos[2];
        details = details.replace("_", "\n");
        tv_details.setText(details);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyMaidSettingsHelper.save(MyMaidSettingsHelper.UPDATED, true);
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(URLHelper.MYMAID_DOWNLOAD));
                context.startActivity(i);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }

}
