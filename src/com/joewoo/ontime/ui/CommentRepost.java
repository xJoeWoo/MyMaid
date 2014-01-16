package com.joewoo.ontime.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.joewoo.ontime.R;
import com.joewoo.ontime.support.service.MyMaidServiceHelper;

import static com.joewoo.ontime.support.info.Defines.ACT_GOT_AT;
import static com.joewoo.ontime.support.info.Defines.COMMENT;
import static com.joewoo.ontime.support.info.Defines.COMMENT_ID;
import static com.joewoo.ontime.support.info.Defines.IS_COMMENT;
import static com.joewoo.ontime.support.info.Defines.IS_REPLY;
import static com.joewoo.ontime.support.info.Defines.IS_REPOST;
import static com.joewoo.ontime.support.info.Defines.KEY_AT_USER;
import static com.joewoo.ontime.support.info.Defines.MENU_AT;
import static com.joewoo.ontime.support.info.Defines.MENU_EMOTION;
import static com.joewoo.ontime.support.info.Defines.MENU_LETTERS;
import static com.joewoo.ontime.support.info.Defines.MENU_POST;
import static com.joewoo.ontime.support.info.Defines.MENU_TOPIC;
import static com.joewoo.ontime.support.info.Defines.STATUS;
import static com.joewoo.ontime.support.info.Defines.TAG;
import static com.joewoo.ontime.support.info.Defines.WEIBO_ID;

@SuppressLint("HandlerLeak")
public class CommentRepost extends Activity {

    private long downTime;
    private EditText et;
    private String weiboID;
    private String commentID;
    private boolean isComment;
    private boolean isReply;
    private boolean isRepost;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.comment);

        setProgressBarIndeterminateVisibility(false);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        et = (EditText) findViewById(R.id.comment_et);

        Intent i = getIntent();

        isComment = i.getBooleanExtra(IS_COMMENT, false);
        isReply = i.getBooleanExtra(IS_REPLY, false);
        isRepost = i.getBooleanExtra(IS_REPOST, false);

        if (isRepost) {
            actionBar.setTitle(R.string.title_act_repost);
            if (i.getStringExtra(STATUS) != null) {
                et.setText(i.getStringExtra(STATUS));
            }
            et.setHint(R.string.comment_repost_repost);
        } else if (isReply) {
            if (i.getStringExtra(COMMENT) != null) {
                et.setText(i.getStringExtra(COMMENT));
            }
            actionBar.setTitle(R.string.title_act_reply);
            commentID = i.getStringExtra(COMMENT_ID);
        } else if (isComment) {
            if(i.getStringExtra(COMMENT) != null) {
                et.setText(i.getStringExtra(COMMENT));
            }
            actionBar.setTitle(R.string.title_act_comment);
        } else {
            finish();
            return;
        }

        weiboID = i.getStringExtra(WEIBO_ID);

        if (weiboID != null)
            Log.e(TAG, "Weibo to comment id: " + weiboID);
        if (commentID != null)
            Log.e(TAG, "Replay comment id: " + commentID);

        et.addTextChangedListener(new TextWatcher() {
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
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();

        menu.add(0, MENU_LETTERS, 0,
                String.valueOf(140 - et.getText().length())).setShowAsAction(
                MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(0, MENU_AT, 0, R.string.menu_at).setIcon(R.drawable.social_group)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(0, MENU_EMOTION, 0, R.string.menu_emotion)
                .setIcon(R.drawable.ic_menu_emoticons)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

//        menu.add(0, MENU_TOPIC, 0, R.string.menu_topic);

        menu.add(0, MENU_POST, 0, R.string.menu_post)
                    .setIcon(R.drawable.social_send_now)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

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
                    Toast.makeText(CommentRepost.this, R.string.toast_press_again_to_clear_text,
                            Toast.LENGTH_SHORT).show();
                    downTime = System.currentTimeMillis();
                } else {
                    et.setText("");
                }
                break;
            }
            case MENU_POST: {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                
                if (et.getText() != null && !et.getText().toString().equals("")) {

                    if (isComment)
                        MyMaidServiceHelper.commentCreate(et.getText().toString(), weiboID);
                    else if (isRepost)
                        MyMaidServiceHelper.repost(et.getText().toString(), weiboID);
                    else if (isReply)
                        MyMaidServiceHelper.reply(et.getText().toString(), weiboID, commentID);

                    finish();
                } else if(isRepost) {

                    MyMaidServiceHelper.repost(getString(R.string.comment_repost_repost), weiboID);

                    finish();

                } else {
                    Toast.makeText(CommentRepost.this, R.string.toast_say_sth, Toast.LENGTH_SHORT)
                            .show();
                }

                break;
            }
            case MENU_AT: {

                startActivityForResult(new Intent(CommentRepost.this, At.class),
                        ACT_GOT_AT);

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
        invalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }

//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            sending = false;
//            setProgressBarIndeterminateVisibility(false);
//            invalidateOptionsMenu();
//            Toast.makeText(CommentRepost.this, (String) msg.obj,
//                    Toast.LENGTH_SHORT).show();
//            switch (msg.what) {
//                case GOT_COMMENT_CREATE_INFO:
//                case GOT_REPOST_INFO:
//                case GOT_REPLY_INFO:
//                    finish();
//                    break;
//            }
//        }
//    };

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
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void insertString(String toInert, boolean toInsertEnd) {
        int selection = et.getSelectionStart();
        Log.e(TAG, String.valueOf(selection));
        String inEt = et.getText().toString();
        String beforeSel = inEt.substring(0, selection);
        String afterSel = inEt.substring(selection);
        Log.e(TAG, beforeSel);
        Log.e(TAG, afterSel);
        et.setText(beforeSel + toInert + afterSel);
        if (toInsertEnd) {
            et.setSelection((beforeSel + toInert).length());
        } else {
            et.setSelection(beforeSel.length() + 1);
        }
    }

}
