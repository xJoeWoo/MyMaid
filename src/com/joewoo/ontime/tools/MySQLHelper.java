package com.joewoo.ontime.tools;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.joewoo.ontime.info.Defines;

public class MySQLHelper extends SQLiteOpenHelper {

	public static final String tableName = "User";
	public static final String UID = Defines.UID;
	public static final String ACCESS_TOKEN = Defines.ACCESS_TOKEN;
	public static final String LOCATION = Defines.LOCATION;
	public static final String EXPIRES_IN = Defines.EXPIRES_IN;
	public static final String SCREEN_NAME = Defines.SCREEN_NAME;
	public static final String DRAFT = Defines.DRAFT;
	public static final String PROFILEIMG = Defines.PROFILE_IMAGE;
	public static final String FRIENDS_TIMELINE = "friends_time_line";
	public static final String TO_ME_COMMENTS = "comments";
	public static final String MENTIONS = "at";

	private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + tableName
			+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + UID
			+ " int, " + ACCESS_TOKEN + " char, " + LOCATION + " varchar, "
			+ EXPIRES_IN + " varchar, " + SCREEN_NAME + " varchar, " + DRAFT
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
