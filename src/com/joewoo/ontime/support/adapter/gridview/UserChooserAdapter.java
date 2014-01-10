package com.joewoo.ontime.support.adapter.gridview;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.adapter.listview.ViewHolder;
import com.joewoo.ontime.support.sql.MyMaidSQLHelper;
import com.joewoo.ontime.support.util.GlobalContext;

/**
 * Created by JoeWoo on 14-1-4.
 */
public class UserChooserAdapter extends BaseAdapter {

    private Context context;
    private Cursor cursor;

    public UserChooserAdapter(Context context) {
        this.context = context;
        cursor = GlobalContext.getSQL().query(MyMaidSQLHelper.USER_TABLE, new String[]{MyMaidSQLHelper.SCREEN_NAME, MyMaidSQLHelper.PROFILE_IMG, MyMaidSQLHelper.UID, MyMaidSQLHelper.ACCESS_TOKEN, MyMaidSQLHelper.DRAFT, MyMaidSQLHelper.PIC_FILE_PATH}, null, null, null, null, null);
    }

    public int getUsersCount() {
        return cursor.getCount();
    }

    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public int getCount() {
        Log.e("XXX", "Users Count: " + String.valueOf(cursor.getCount()));
        if(GlobalContext.getSQL().query(MyMaidSQLHelper.USER_TABLE, null, MyMaidSQLHelper.LAST_LOGIN + "=?", new String[]{"1"}, null, null, null).getCount() > 0)
            return cursor.getCount() + 2;
        else
            return cursor.getCount() + 1;
    }

    @Override
    public Object getItem(int position) {
        return cursor;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();

            convertView = LayoutInflater.from(context).inflate(R.layout.user_chooser_dialog_gv, null);

            holder.iv = (ImageView) convertView.findViewById(R.id.user_chooser_dialog_iv);
            holder.tv_scr_name = (TextView) convertView.findViewById(R.id.user_chooser_dialog_scr_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try{

        if (cursor != null && cursor.moveToFirst()) {

            if (cursor.getCount() - position > 0) {

                cursor.move(position);

                holder.tv_scr_name.setText(cursor.getString(cursor.getColumnIndex(MyMaidSQLHelper.SCREEN_NAME)));

                byte[] imgBytes = cursor.getBlob(cursor.getColumnIndex(MyMaidSQLHelper.PROFILE_IMG));
                holder.iv.setImageBitmap(BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length));

            } else if (cursor.getCount() - position == 0) {

                holder.iv.setImageResource(R.drawable.ic_user_chooser_dialog_add);
                holder.tv_scr_name.setText(GlobalContext.getResString(R.string.frag_ftl_dialog_choose_account_add_account));

                } else if (cursor.getCount() - position == -1) {
                holder.iv.setImageResource(R.drawable.ic_user_chooser_dialog_logout);
                holder.tv_scr_name.setText(GlobalContext.getResString(R.string.frag_ftl_dialog_choose_account_logout));
            }

        } else {
            holder.iv.setImageResource(R.drawable.ic_user_chooser_dialog_add);
            holder.tv_scr_name.setText(GlobalContext.getResString(R.string.frag_ftl_dialog_choose_account_add_account));
        }
        }catch (Exception e){
            e.printStackTrace();
        }

        return convertView;
    }
}
