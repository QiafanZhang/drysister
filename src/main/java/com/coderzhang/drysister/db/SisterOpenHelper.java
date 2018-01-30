package com.coderzhang.drysister.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by z on 2018/01/29.
 */

public class SisterOpenHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "sister.db";
    public static final int DB_VERSION = 1;

    public SisterOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "create table if not exists" +
                TableDefine.TABLE_FULI + "(" +
                TableDefine.COLUMN_ID + "integer primary key autoincrement,"
                + TableDefine.COLUMN_FULI_ID + "text,"
                + TableDefine.COLUMN_FULI_CREATEAT + "text,"
                + TableDefine.COLUMN_FULI_PUBLISHEDAT + "text,"
                + TableDefine.COLUMN_FULI_SOURCE + "text,"
                + TableDefine.COLUMN_FULI_DESC + "text,"
                + TableDefine.COLUMN_FULI_URL + "text,"
                + TableDefine.COLUMN_FULI_USED + "boolean,"
                + TableDefine.COLUMN_FULI_WHO + "text" + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
