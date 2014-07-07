package com.joewoo.ontime.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.joewoo.ontime.R;
import com.joewoo.ontime.action.URLHelper;
import com.joewoo.ontime.action.auth.AccessToken;
import com.joewoo.ontime.action.user.UserShow;
import com.joewoo.ontime.support.bean.WeiboBackBean;
import com.joewoo.ontime.support.dialog.WeatherDialog;
import com.joewoo.ontime.support.image.LoginProfileImage;
import com.joewoo.ontime.support.sql.MyMaidSQLHelper;
import com.joewoo.ontime.support.util.GlobalContext;

import static com.joewoo.ontime.support.info.Defines.GOT_ACCESS_TOKEN;
import static com.joewoo.ontime.support.info.Defines.GOT_ACCESS_TOKEN_FAIL;
import static com.joewoo.ontime.support.info.Defines.GOT_PROFILEIMG_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_SHOW_INFO;
import static com.joewoo.ontime.support.info.Defines.GOT_SHOW_INFO_FAIL;
import static com.joewoo.ontime.support.info.Defines.LOGIN_FROM_POST;
import static com.joewoo.ontime.support.info.Defines.TAG;

public class LoginActivity extends Activity {

    public WebView wv_login;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            setProgressBarIndeterminateVisibility(true);
            switch (msg.what) {
                case GOT_ACCESS_TOKEN: {
                    WeiboBackBean token = (WeiboBackBean) msg.obj;
                    GlobalContext.setAccessToken(token.getAccessToken());
                    GlobalContext.setUID(token.getUID());
                    new UserShow(mHandler).start();
                    token = null;
                    break;
                }

                case GOT_ACCESS_TOKEN_FAIL: {
                    Toast.makeText(LoginActivity.this, R.string.toast_access_token_fail, Toast.LENGTH_SHORT).show();
                    break;
                }
                case GOT_SHOW_INFO: {
                    WeiboBackBean show = (WeiboBackBean) msg.obj;
                    GlobalContext.setScreenName(show.getScreenName());
                    new LoginProfileImage(show.getAvatarLarge(), mHandler)
                            .start();
                    show = null;
                    break;
                }

                case GOT_PROFILEIMG_INFO: {

                    GlobalContext.setProfileImg((byte[]) msg.obj);

                    ContentValues cv = new ContentValues();

                    cv.put(MyMaidSQLHelper.ACCESS_TOKEN, GlobalContext.getAccessToken());
                    cv.put(MyMaidSQLHelper.SCREEN_NAME, GlobalContext.getScreenName());
                    cv.put(MyMaidSQLHelper.PROFILE_IMG, (byte[]) msg.obj);

                    if (GlobalContext.getSQL().query(MyMaidSQLHelper.USER_TABLE, null, MyMaidSQLHelper.UID + "=?",
                            new String[]{GlobalContext.getUID()}, null, null, null).getCount() > 0) {// 查询到已经存在UID（已经登录）

                        Log.e(MyMaidSQLHelper.TAG_SQL, "Got login info");

                        if (GlobalContext.getSQL().update(MyMaidSQLHelper.USER_TABLE, cv, MyMaidSQLHelper.UID
                                + "='" + GlobalContext.getUID() + "'", null) != 0) {
                            Log.e(MyMaidSQLHelper.TAG_SQL, "SQL login info Updated");
                        }


                    } else {// 否则插入登录信息

                        cv.put(MyMaidSQLHelper.UID, GlobalContext.getUID());

                        GlobalContext.getSQL().insert(MyMaidSQLHelper.USER_TABLE, null, cv);

                        Log.e(MyMaidSQLHelper.TAG_SQL, "Inserted");

                    }

                    setProgressBarIndeterminateVisibility(false);

                    MyMaidSQLHelper.setLastLogin(GlobalContext.getUID());

                    if (getIntent().getBooleanExtra(LOGIN_FROM_POST, false)) {
                        startActivity(new Intent(LoginActivity.this, PostActivity.class));
                        finish();
                    } else {
                        WeatherDialog.show(true, LoginActivity.this);
                    }

                    break;
                }
                case GOT_SHOW_INFO_FAIL: {
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.act_login);
        setProgressBarIndeterminateVisibility(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        wv_login = (WebView) findViewById(R.id.wv_login);
        wv_login.getSettings().setJavaScriptEnabled(true);

        // 加载网页
        wv_login.loadUrl(URLHelper.AUTH);

        wv_login.setWebViewClient(new WebViewClient() {

            // 监听返回的网址
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.e(TAG, "onPageStarted URL: " + url);
                setProgressBarIndeterminateVisibility(true);

                // 判断是不是以Callback的网址开头
                // 在【微博开放平台】-【应用信息】-【高级信息】里可以设置Callback网址
                if (url.startsWith(URLHelper.CALLBACK)) {

                    // 是的话停止加载新网页
                    view.cancelLongPress();
                    view.stopLoading();

                    // 输出auth_code看一下
                    // url.substring(url.indexOf("=") + 1) 就是截取auth_code的代码
                    Log.e(TAG, "Auth Code: " + url.substring(url.indexOf("=") + 1));

                    // 用截取到auth_code来获得AccessToken，AccessToken为新线程，获得服务器响应后用Handler给Activity传回有关信息
                    // AccessToken在 action.auth 里
                    new AccessToken(url.substring(url.indexOf("=") + 1), mHandler).start();
                }

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.e(TAG, "onPageFinished");
                setProgressBarIndeterminateVisibility(false);
//				super.onPageFinished(view, url);
            }
        });


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
