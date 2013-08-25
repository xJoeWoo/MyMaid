package com.joewoo.ontime.tools;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLHelper extends SQLiteOpenHelper {

	public final String tableName = "User";
	public final String UID = "uid";
	public final String ACCESS_TOKEN = "access_token";
	public final String LOCATION = "location";
	public final String EXPIRES_IN = "expires_in";
	public final String SCREEN_NAME = "screen_name";
	public final String DRIFT = "drift";
	public final String PROFILEIMG = "profile_image";
	public final String FRIENDS_TIMELINE = "friends_time_line";
	public final String TO_ME_COMMENTS = "comments";
	public final String MENTIONS = "at";

	final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + tableName
			+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + UID
			+ " int, " + ACCESS_TOKEN + " char, " + LOCATION + " varchar, "
			+ EXPIRES_IN + " varchar, " + SCREEN_NAME + " varchar, " + DRIFT
			+ " varchar, " + PROFILEIMG + " blob, " + FRIENDS_TIMELINE
			+ " varchar, " + TO_ME_COMMENTS + " varchar, " + MENTIONS
			+ " varchar);";

	public MySQLHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public MySQLHelper(Context context, String name, CursorFactory factory,
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

		// switch (newVersion) {
		// case 2: {
		// Log.e(TAG_SQL, "Version " + String.valueOf(oldVersion) + " to "
		// + String.valueOf(newVersion));
		// db.beginTransaction();
		// db.execSQL("ALTER TABLE " + tableName + " ADD COLUMN " + AT
		// + " varchar;");
		// db.execSQL("ALTER TABLE " + tableName + " ADD COLUMN " + COMMENTS
		// + " varchar;");
		// db.execSQL("ALTER TABLE " + tableName + " ADD COLUMN "
		// + FRIENDS_TIMELINE + " varchar;");
		// db.setTransactionSuccessful();
		// db.endTransaction();
		// break;
		// }
		// }

	}

}
