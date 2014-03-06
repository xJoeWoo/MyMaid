package com.joewoo.ontime.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.dialog.UserChooserDialog;
import com.joewoo.ontime.support.info.Defines;
import com.joewoo.ontime.support.net.NetworkStatus;
import com.joewoo.ontime.support.notification.MyMaidNotificationHelper;
import com.joewoo.ontime.support.service.MyMaidServiceHelper;
import com.joewoo.ontime.support.sql.MyMaidSQLHelper;
import com.joewoo.ontime.support.util.GlobalContext;

import static com.joewoo.ontime.support.info.Defines.ACT_GOT_AT;
import static com.joewoo.ontime.support.info.Defines.ACT_GOT_PHOTO;
import static com.joewoo.ontime.support.info.Defines.KEY_AT_USER;
import static com.joewoo.ontime.support.info.Defines.MENU_ADD;
import static com.joewoo.ontime.support.info.Defines.MENU_AT;
import static com.joewoo.ontime.support.info.Defines.MENU_EMOTION;
import static com.joewoo.ontime.support.info.Defines.MENU_LETTERS;
import static com.joewoo.ontime.support.info.Defines.MENU_POST;
import static com.joewoo.ontime.support.info.Defines.MENU_TOPIC;
import static com.joewoo.ontime.support.info.Defines.TAG;

public class PostActivity extends Activity {

    private EditText et_post;
    private long downTime = 0;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        MyMaidNotificationHelper.cancel(MyMaidNotificationHelper.UPDATE);
        MyMaidNotificationHelper.cancel(MyMaidNotificationHelper.UPLOAD);

        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(intent.getAction()) && type != null) {// 分享到此Activity
            if (type.startsWith("text/")) { // 传入文件为文字
                Log.e(TAG, "Share TEXT");
                String text = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (text != null) {
                    et_post.setText(text);
                    et_post.setSelection(text.length());
                }
            } else if (type.startsWith("image/")) { // 传入文件为图片
                Log.e(TAG, "Share PHOTO");
                Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (uri != null) {
                    GlobalContext.setPicPath(getFilePath(uri));
                }
                invalidateOptionsMenu();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_post);
        findViews();

        Log.e(TAG, "PostActivity Weibo");

        getActionBar().setDisplayUseLogoEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();

        String type = i.getType();

        if (Intent.ACTION_SEND.equals(i.getAction()) && type != null) {// 分享到此Activity
            if (type.startsWith("text/")) { // 传入文件为文字
                Log.e(TAG, "Share TEXT");
                String text = i.getStringExtra(Intent.EXTRA_TEXT);
                if (text != null) {
                    et_post.setText(text);
                    et_post.setSelection(text.length());
                }
            } else if (type.startsWith("image/")) { // 传入文件为图片
                Log.e(TAG, "Share PHOTO");
                Uri uri = i.getParcelableExtra(Intent.EXTRA_STREAM);
                if (uri != null) {
                    GlobalContext.setPicPath(getFilePath(uri));
                }
                invalidateOptionsMenu();
            }
        }

        if (GlobalContext.getAccessToken() != null) {
            String draft = GlobalContext.getDraft();
            if (draft != null && et_post.getText() != null && et_post.getText().toString().equals("")) {
                et_post.setText(draft);
                et_post.setSelection(draft.length());
            }
        } else {
            new UserChooserDialog().show(this);
        }

        et_post.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                invalidateOptionsMenu();
            }
        });

        setTitle(R.string.title_act_post);
        getActionBar().setLogo(GlobalContext.getProfileImg());
        getActionBar().setSubtitle(GlobalContext.getScreenName());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(et_post, 0);
            }
        }, Defines.INPUT_SHOW_DELAY);
    }

    private void findViews() {
        et_post = (EditText) findViewById(R.id.et_post);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();

        menu.add(0, MENU_LETTERS, 0,
                String.valueOf(140 - et_post.getText().length()))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        if (GlobalContext.getPicPath() != null) {
            menu.add(0, MENU_ADD, 0, R.string.menu_image_clear)
                    .setIcon(R.drawable.content_picture_ok)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        } else {
            menu.add(0, MENU_ADD, 0, R.string.menu_image_add)
                    .setIcon(R.drawable.content_picture)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        menu.add(0, MENU_AT, 0, R.string.menu_at)
                .setIcon(R.drawable.social_group)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(0, MENU_EMOTION, 0, R.string.menu_emotion)
                .setIcon(R.drawable.ic_menu_emoticons)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

//        menu.add(0, MENU_TOPIC, 0, R.string.menu_topic);

        menu.add(0, MENU_POST, 0, R.string.menu_post)
                .setIcon(R.drawable.social_send_now)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

//        menu.add(0, MENU_CLEAR_DRAFT, 0, R.string.menu_draft_clear);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
            case MENU_LETTERS: {
                if (System.currentTimeMillis() - downTime > 2000) {
                    Toast.makeText(PostActivity.this,
                            R.string.toast_press_again_to_clear_text,
                            Toast.LENGTH_SHORT).show();
                    downTime = System.currentTimeMillis();
                } else {
                    et_post.setText("");
                    GlobalContext.setDraft(null);
                }
                break;
            }
            case MENU_ADD: {
                if (GlobalContext.getPicPath() == null) {

                    Intent ii = new Intent();

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                        ii.setType("image/*");
                        ii.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(ii, ACT_GOT_PHOTO);
                    } else {
                        ii.setAction(Intent.ACTION_PICK);
                        ii.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(ii, ACT_GOT_PHOTO);
                    }

                } else {
                    if (System.currentTimeMillis() - downTime > 2000) {
                        Toast.makeText(PostActivity.this,
                                R.string.toast_press_again_to_clear_img,
                                Toast.LENGTH_SHORT).show();
                        downTime = System.currentTimeMillis();
                    } else {
                        GlobalContext.setPicPath(null);
                    }
                }
                invalidateOptionsMenu();
                break;
            }
            case MENU_POST: {
//                if (NetworkStatus.check(true)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_post.getWindowToken(), 0);
                if (et_post.getText() != null && !et_post.getText().toString().equals("")) {
                    String status = et_post.getText().toString();
                    if (GlobalContext.getPicPath() != null) {
                        MyMaidServiceHelper.upload(status);
                    } else {
                        MyMaidServiceHelper.update(status);
                    }
                    finish();
                } else {
                    Toast.makeText(PostActivity.this, R.string.toast_say_sth,
                            Toast.LENGTH_SHORT).show();
                }
//                }
                break;
            }
            case MENU_AT: {
                if (NetworkStatus.check(false))
                    startActivityForResult(new Intent(PostActivity.this, AtSuggestionsActivity.class),
                            ACT_GOT_AT);
                else {
                    Toast.makeText(PostActivity.this, R.string.toast_no_network_to_at,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case MENU_EMOTION: {
                insertString("[]", false);
                break;
            }
            case MENU_TOPIC: {
                insertString("##", false);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Log.e(TAG, "on Activity Result OK");
            switch (requestCode) {
                case ACT_GOT_AT: {
                    insertString("@" + data.getExtras().getString(KEY_AT_USER)
                            + " ", true);
                    break;
                }
                case ACT_GOT_PHOTO: {
                    Log.e(TAG, data.getData().toString());
                    GlobalContext.setPicPath(getFilePath(data.getData()));
                    invalidateOptionsMenu();
                    break;
                }
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getFilePath(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null,
                null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        Log.e(TAG, "File Path: " + filePath);
        return filePath;
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "PAUSE");
        super.onPause();
        if (et_post.getText() != null && !et_post.getText().toString().trim().equals(""))
            GlobalContext.setDraft(et_post.getText().toString());
        MyMaidSQLHelper.saveDraft();
    }

    private void insertString(String toInert, boolean toInsertEnd) {
        int selection = et_post.getSelectionStart();
        Log.e(TAG, String.valueOf(selection));
        String inEt = et_post.getText().toString();
        String beforeSel = inEt.substring(0, selection);
        String afterSel = inEt.substring(selection);
        Log.e(TAG, beforeSel);
        Log.e(TAG, afterSel);
        et_post.setText(beforeSel + toInert + afterSel);
        if (toInsertEnd) {
            et_post.setSelection((beforeSel + toInert).length());
        } else {
            et_post.setSelection(beforeSel.length() + 1);
        }
    }

}
