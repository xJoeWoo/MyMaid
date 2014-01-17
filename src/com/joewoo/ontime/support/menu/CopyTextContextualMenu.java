package com.joewoo.ontime.support.menu;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.util.GlobalContext;

import static com.joewoo.ontime.support.info.Defines.MENU_COPY_TEXT;

/**
 * Created by JoeWoo on 13-11-21.
 */
public class CopyTextContextualMenu implements ActionMode.Callback {

    public static final String CLIPBOARD_LABLE = "mymaid";
    private String toCopy;

    public CopyTextContextualMenu(String toCopy) {
        this.toCopy = toCopy;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        menu.add(0, MENU_COPY_TEXT, 0, GlobalContext.getAppContext().getString(R.string.menu_copy_text))
                .setIcon(R.drawable.content_copy)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case MENU_COPY_TEXT:
                ((ClipboardManager) GlobalContext.getAppContext().getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText(CLIPBOARD_LABLE, toCopy));
                mode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        toCopy = null;
    }
}
