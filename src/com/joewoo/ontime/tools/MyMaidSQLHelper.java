package com.joewoo.ontime.tools;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.joewoo.ontime.info.Constants;

public class MyMaidSQLHelper extends SQLiteOpenHelper {

	public static final String tableName = "User";
	public static final String UID = Constants.UID;
	public static final String ACCESS_TOKEN = Constants.ACCESS_TOKEN;
	public static final String LOCATION = Constants.LOCATION;
	public static final String EXPIRES_IN = Constants.EXPIRES_IN;
	public static final String SCREEN_NAME = Constants.SCREEN_NAME;
	public static final String DRAFT = Constants.DRAFT;
	public static final String PROFILEIMG = Constants.PROFILE_IMAGE;
	public static final String FRIENDS_TIMELINE = "friends_time_line";
	public static final String TO_ME_COMMENTS = "comments";
	public static final String MENTIONS = "at";
    public static final String COMMENTS_MENTIONS = "comments_mentions";
    public final static String TAG_SQL = "OnTime SQL ---";
    public final static String SQL_NAME = "MyMaid.db";
    public final static int SQL_VERSION = 3;

	private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + tableName
			+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + UID
			+ " int, " + ACCESS_TOKEN + " char, " + LOCATION + " varchar, "
			+ EXPIRES_IN + " varchar, " + SCREEN_NAME + " varchar, " + DRAFT
			+ " varchar, " + PROFILEIMG + " blob, " + FRIENDS_TIMELINE
			+ " varchar, " + TO_ME_COMMENTS + " varchar, " + MENTIONS
			+ " varchar, " + COMMENTS_MENTIONS + " varchar);";

	public MyMaidSQLHelper(Context context, String name, CursorFactory factory,
                           int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public MyMaidSQLHelper(Context context, String name, CursorFactory factory,
                           int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

        if(oldVersion == 2 && newVersion == 3)
        {
            Log.e(TAG_SQL, "Version " + String.valueOf(oldVersion) + " to " + String.valueOf(newVersion));
            db.beginTransaction();
            db.execSQL("ALTER TABLE " + tableName + " ADD COLUMN "
                    + COMMENTS_MENTIONS + " varchar;");
            db.setTransactionSuccessful();
            db.endTransaction();
        }
	}

}
