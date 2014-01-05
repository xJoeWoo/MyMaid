package com.joewoo.ontime.support.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.adapter.gridview.UserChooserAdapter;
import com.joewoo.ontime.support.sql.MyMaidSQLHelper;
import com.joewoo.ontime.support.util.GlobalContext;
import com.joewoo.ontime.ui.Login;
import com.joewoo.ontime.ui.maintimeline.MainTimelineActivity;

import static com.joewoo.ontime.support.info.Defines.TAG;

/**
 * Created by JoeWoo on 14-1-4.
 */
public class UserChooserDialog {

    public static void show(final Activity act) {

        final Dialog dialog = new Dialog(act);

        dialog.setTitle(R.string.frag_ftl_dialog_choose_account_title);
        dialog.setContentView(R.layout.user_chooser_dialog);

        GridView gv = (GridView) dialog.findViewById(R.id.user_chooser_dialog_gv);

        final UserChooserAdapter uAdapter = new UserChooserAdapter(act);
        final int usersCount = uAdapter.getUsersCount();

        gv.setAdapter(uAdapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "User Chose " + String.valueOf(position));

                if (usersCount - position > 0) {

                    Cursor c = uAdapter.getCursor();

                    c.move(position);

                    if (!c.getString(c.getColumnIndex(MyMaidSQLHelper.UID)).equals(GlobalContext.getUID())) {
                        GlobalContext.setProfileImg(c.getBlob(c.getColumnIndex(MyMaidSQLHelper.PROFILE_IMG)));
                        GlobalContext.setUID(c.getString(c.getColumnIndex(MyMaidSQLHelper.UID)));
                        GlobalContext.setScreenName(c.getString(c.getColumnIndex(MyMaidSQLHelper.SCREEN_NAME)));
                        GlobalContext.setAccessToken(c.getString(c.getColumnIndex(MyMaidSQLHelper.ACCESS_TOKEN)));
                        try {
                            GlobalContext.setDraft(c.getString(c.getColumnIndex(MyMaidSQLHelper.DRAFT)));
                            GlobalContext.setPicPath(c.getString(c.getColumnIndex(MyMaidSQLHelper.PIC_FILE_PATH)));
                        } catch (Exception e) {
                            Log.e(TAG, "No Draft of Pic Path");
                        }

                        MyMaidSQLHelper.setLastLogin(GlobalContext.getUID());

                        act.finish();
                        act.startActivity(new Intent(act, MainTimelineActivity.class));
                    } else {
                        Toast.makeText(act, R.string.user_chooser_dialog_choosed, Toast.LENGTH_SHORT).show();
                    }

                } else if (usersCount - position <= 0) {
                    if (usersCount - position == -1) {
                        if (GlobalContext.getSQL().delete(MyMaidSQLHelper.USER_TABLE, MyMaidSQLHelper.LAST_LOGIN + "=?",
                                new String[]{"1"}) > 0) {
                            Log.e(MyMaidSQLHelper.TAG_SQL, "LOGOUT - Cleared user info");
                            Toast.makeText(act, "<(￣︶￣)>", Toast.LENGTH_SHORT).show();
                            GlobalContext.clear();
                        }
                    }
                    act.finish();
                    act.startActivity(new Intent(act, Login.class));
                }
                dialog.cancel();
            }
        }
        );

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.e("XXX", "onCancel");
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.e("XXX", "onDismiss");
            }
        });

        dialog.show();
    }


}
