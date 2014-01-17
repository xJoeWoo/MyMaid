package com.joewoo.ontime.support.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.joewoo.ontime.support.info.Defines;
import com.joewoo.ontime.support.util.GlobalContext;

public class MyMaidSQLHelper extends SQLiteOpenHelper {

    public final static int SQL_VERSION = 5;
    public static final String USER_TABLE = "User";
    public static final String UID = Defines.UID;
    public static final String ACCESS_TOKEN = Defines.ACCESS_TOKEN;
    public static final String LOCATION = Defines.LOCATION;
    public static final String PIC_FILE_PATH = Defines.EXPIRES_IN;
    public static final String SCREEN_NAME = Defines.SCREEN_NAME;
    public static final String DRAFT = Defines.DRAFT;
    public static final String PROFILE_IMG = Defines.PROFILE_IMAGE;
    public static final String FRIENDS_TIMELINE = "friends_time_line";
    public static final String COMMENTS_TO_ME = "comments";
    public static final String MENTIONS = "at";
    public static final String COMMENTS_MENTIONS = "comments_mentions";
    public static final String FRIENDS_IDS = "friends_ids";
    public static final String LAST_LOGIN = "last_login";
    public static final String COLUMN_1 = "extra_1";
    public static final String COLUMN_2 = "extra_2";
    public static final String COLUMN_3 = "extra_3";
    public static final String COLUMN_4 = "extra_4";
    public static final String COLUMN_5 = "extra_5";
    public static final String COLUMN_6 = "extra_6";
    public static final String COLUMN_7 = "extra_7";
    public static final String COLUMN_8 = "extra_8";
    public static final String COLUMN_9 = "extra_9";
    public static final String COLUMN_10 = "extra_10";
    public final static String TAG_SQL = "OnTime SQL ---";
    public final static String SQL_NAME = "MyMaid.db";

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + USER_TABLE
            + " (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + UID
            + " int, " + ACCESS_TOKEN + " char, " + LOCATION + " varchar, "
            + PIC_FILE_PATH + " varchar, " + SCREEN_NAME + " varchar, " + DRAFT
            + " varchar, " + PROFILE_IMG + " blob, " + FRIENDS_TIMELINE
            + " varchar, " + COMMENTS_TO_ME + " varchar, " + MENTIONS
            + " varchar, " + COMMENTS_MENTIONS + " varchar, " + FRIENDS_IDS + " varchar, " + COLUMN_1 + " varchar, "
            + COLUMN_2 + " varchar, " + COLUMN_3 + " varchar, " + COLUMN_4 + " varchar, "
            + COLUMN_5 + " varchar, " + COLUMN_6 + " varchar, " + COLUMN_7 + " varchar, "
            + COLUMN_8 + " varchar, " + COLUMN_9 + " varchar, " + COLUMN_10 + " varchar, " + LAST_LOGIN + " int);";

    public MyMaidSQLHelper(Context context, String name, CursorFactory factory,
                           int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    public static String getOneString(String columnIndex) {
        if (GlobalContext.getSQL() != null) {
            Log.e(TAG_SQL, "Get one String: " + columnIndex);
            Cursor c = GlobalContext.getSQL().query(USER_TABLE, new String[]{
                    columnIndex},
                    UID + "=?", new String[]{GlobalContext.getUID()}, null,
                    null, null);
            if (c.getCount() > 0 && c.moveToFirst() && c.getString(c
                    .getColumnIndex(columnIndex)) != null) {

                String str = c.getString(c.getColumnIndex(columnIndex));

                c.close();

                return str;
            } else {
                Log.e(TAG_SQL, "SQL no this String");
                return null;
            }
        } else {
            return null;
        }
    }

    public static boolean saveOneString(String columnIndex, String toSave) {
        if (GlobalContext.getSQL() != null) {
            ContentValues cv = new ContentValues();
            cv.put(columnIndex, toSave);
            if (GlobalContext.getSQL().update(USER_TABLE, cv, UID + "='"
                    + GlobalContext.getUID() + "'", null) != 0) {
                Log.e(TAG_SQL, "Saved " + columnIndex);
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    public static boolean setLastLogin(String uid) {

        Cursor c = GlobalContext.getSQL().query(USER_TABLE, new String[]{MyMaidSQLHelper.UID}, null, null, null, null, null);
        if (c != null && c.getCount() > 0 && c.moveToFirst()) {
            ContentValues cv = new ContentValues();
            cv.put(LAST_LOGIN, 0);

            while (c.moveToNext())
                GlobalContext.getSQL().update(USER_TABLE, cv, UID + "=?", new String[]{c.getString(c.getColumnIndex(UID))});

            c.close();
            cv.clear();
            cv.put(LAST_LOGIN, 1);
            return GlobalContext.getSQL().update(USER_TABLE, cv, UID + "=?", new String[]{uid}) > 0;

        } else
            return false;
    }

    public static boolean clearDraft() {
        ContentValues cv = new ContentValues();
        GlobalContext.setDraft(null);
        GlobalContext.setPicPath(null);
        cv.put(DRAFT, GlobalContext.getDraft());
        cv.put(PIC_FILE_PATH, GlobalContext.getPicPath());
        if (GlobalContext.getSQL() != null && GlobalContext.getSQL().update(USER_TABLE, cv, UID + "='" + GlobalContext.getUID() + "'", null) != 0) {
            Log.e(TAG_SQL, "Cleared Draft");
            return true;
        } else
            return false;
    }

    public static boolean saveDraft() {
        ContentValues cv = new ContentValues();
        if (GlobalContext.getDraft() != null) {
            cv.put(DRAFT, GlobalContext.getDraft());
        }
        if (GlobalContext.getPicPath() != null) {
            cv.put(PIC_FILE_PATH, GlobalContext.getPicPath());
        }
        if (cv.size() > 0 && GlobalContext.getSQL() != null && GlobalContext.getSQL().update(USER_TABLE, cv, UID + "='" + GlobalContext.getUID() + "'", null) != 0) {
            Log.e(TAG_SQL, "Saved Draft");
            return true;
        } else
            return false;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.e(TAG_SQL, "Version " + String.valueOf(oldVersion) + " to " + String.valueOf(newVersion));

        if (newVersion == 5) {
            db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
            db.execSQL(CREATE_TABLE);
        }
    }

}
