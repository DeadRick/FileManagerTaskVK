package com.example.myfilemanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

public class FileDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "file_database";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "files";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_HASH = "hash";
    public static final String COLUMN_LAST_MODIFIED = "last_modified";

    public FileDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_NAME + " TEXT PRIMARY KEY," +
                COLUMN_HASH + " TEXT," +
                COLUMN_LAST_MODIFIED + " INTEGER" +
                ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public String getHashForFile(File fileName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_HASH},
                COLUMN_NAME + "=?", new String[]{fileName.getName()}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String hash = cursor.getString(0);
            cursor.close();
            return hash;
        } else {
            return null;
        }
    }
}
