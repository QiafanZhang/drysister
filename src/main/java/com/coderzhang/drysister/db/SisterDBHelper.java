package com.coderzhang.drysister.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.coderzhang.drysister.TableDefine;
import com.coderzhang.drysister.entity.Sister;

import java.util.ArrayList;

/**
 * Created by z on 2018/01/29.
 */

public class SisterDBHelper {
    private static final String TAG = "SisterDBHelper";
    private SisterOpenHelper openHelper;
    private static SisterDBHelper dbHelper;
    private SQLiteDatabase db;

    private SisterDBHelper(Context context) {
        openHelper = new SisterOpenHelper(context, TableDefine.TABLE_FULI, null, 1);
    }

    public static SisterDBHelper getInstance(Context context) {
        if (dbHelper == null) {
            synchronized (SisterDBHelper.class) {
                if (dbHelper == null) {
                    dbHelper = new SisterDBHelper(context);
                }
            }
        }
        return dbHelper;
    }

    /**
     * 可读数据库
     *
     * @return
     */
    private SQLiteDatabase getReadableDB() {
        return dbHelper.getReadableDB();
    }

    /**
     * 可写数据库
     *
     * @return
     */
    private SQLiteDatabase getWritableDB() {
        return dbHelper.getWritableDB();
    }

    /**
     * 关闭游标和数据库
     *
     * @param cursor
     */
    private void close(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }
    }

    /**
     * 插入一个小姐姐
     *
     * @param sister 小姐姐实体
     */
    public void insertSister(Sister sister) {
        db = getWritableDB();
        ContentValues values = new ContentValues();
        values.put(TableDefine.COLUMN_FULI_ID, sister.get_id());
        values.put(TableDefine.COLUMN_FULI_CREATEAT, sister.getCreatedAt());
        values.put(TableDefine.COLUMN_FULI_DESC, sister.getDesc());
        values.put(TableDefine.COLUMN_FULI_PUBLISHEDAT, sister.getPublishedAt());
        values.put(TableDefine.COLUMN_FULI_SOURCE, sister.getSource());
        values.put(TableDefine.COLUMN_FULI_TYPE, sister.getType());
        values.put(TableDefine.COLUMN_FULI_URL, sister.getUrl());
        values.put(TableDefine.COLUMN_FULI_USED, sister.isUsed());
        values.put(TableDefine.COLUMN_FULI_WHO, sister.getWho());
        db.insert(TableDefine.TABLE_FULI, null, values);
        close(null);
    }

    /**
     * 插入一堆小姐姐
     *
     * @param sisters 泛型为小姐姐的集合
     */
    public void insertSisters(ArrayList<Sister> sisters) {
        db = getWritableDB();
        db.beginTransaction();
        try {
            for (Sister sister : sisters
                    ) {
                ContentValues values = new ContentValues();
                values.put(TableDefine.COLUMN_FULI_ID, sister.get_id());
                values.put(TableDefine.COLUMN_FULI_CREATEAT, sister.getCreatedAt());
                values.put(TableDefine.COLUMN_FULI_DESC, sister.getDesc());
                values.put(TableDefine.COLUMN_FULI_PUBLISHEDAT, sister.getPublishedAt());
                values.put(TableDefine.COLUMN_FULI_SOURCE, sister.getSource());
                values.put(TableDefine.COLUMN_FULI_TYPE, sister.getType());
                values.put(TableDefine.COLUMN_FULI_URL, sister.getUrl());
                values.put(TableDefine.COLUMN_FULI_USED, sister.isUsed());
                values.put(TableDefine.COLUMN_FULI_WHO, sister.getWho());
                db.insert(TableDefine.TABLE_FULI, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            if (db != null && db.isOpen()) {
                db.endTransaction();
                close(null);
            }
        }
    }

    /**
     * 根据id删除小姐姐
     *
     * @param _id
     */
    public void deleteSister(String _id) {
        db = getWritableDB();
        db.delete(TableDefine.TABLE_FULI, "_id=?", new String[]{_id});
        close(null);
    }

    /**
     * 删除所有小姐姐
     */
    public void deleteAllSisters() {
        db = getWritableDB();
        db.delete(TableDefine.TABLE_FULI, null, null);
        close(null);
    }

    /**
     * 根据—id更新小姐姐
     *
     * @param _id
     * @param sister
     */
    public void updateSister(String _id, Sister sister) {
        db = getWritableDB();
        ContentValues values = new ContentValues();
        values.put(TableDefine.COLUMN_FULI_ID, sister.get_id());
        values.put(TableDefine.COLUMN_FULI_CREATEAT, sister.getCreatedAt());
        values.put(TableDefine.COLUMN_FULI_DESC, sister.getDesc());
        values.put(TableDefine.COLUMN_FULI_PUBLISHEDAT, sister.getPublishedAt());
        values.put(TableDefine.COLUMN_FULI_SOURCE, sister.getSource());
        values.put(TableDefine.COLUMN_FULI_TYPE, sister.getType());
        values.put(TableDefine.COLUMN_FULI_URL, sister.getUrl());
        values.put(TableDefine.COLUMN_FULI_USED, sister.isUsed());
        values.put(TableDefine.COLUMN_FULI_WHO, sister.getWho());
        db.update(TableDefine.TABLE_FULI, values, "_id=?", new String[]{_id});
        close(null);
    }

    /**
     * 获得小姐姐总数
     *
     * @return
     */
    public int getSisterCount() {
        db = getReadableDB();
        Cursor cursor = db.rawQuery("select count(*) from" + TableDefine.TABLE_FULI, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        Log.v(TAG, "小姐姐count=" + count);
        return count;
    }

    public ArrayList<Sister> querySisterLimit(int curPos, int limit) {
        db = getReadableDB();
        ArrayList<Sister> sisters = new ArrayList<>();
        String startPos = String.valueOf(curPos * limit);
        if (db != null) {
            Cursor cursor = db.query(TableDefine.TABLE_FULI,
                    new String[]{TableDefine.COLUMN_FULI_ID, TableDefine.COLUMN_FULI_CREATEAT,
                            TableDefine.COLUMN_FULI_DESC, TableDefine.COLUMN_FULI_PUBLISHEDAT,
                            TableDefine.COLUMN_FULI_SOURCE, TableDefine.COLUMN_FULI_TYPE,
                            TableDefine.COLUMN_FULI_URL, TableDefine.COLUMN_FULI_WHO},
                    null, null, null, null, TableDefine.COLUMN_FULI_ID, startPos + "," + limit);
            while (cursor.moveToNext()) {
                Sister sister = new Sister();
                sister.set_id(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_ID)));
                sister.setCreatedAt(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_CREATEAT)));
                sister.setUrl(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_URL)));
                sister.setPublishedAt(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_PUBLISHEDAT)));
                sister.setDesc(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_DESC)));
                sister.setSource(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_SOURCE)));
                sister.setWho(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_WHO)));
//                sister.setUsed(cursor.getInt(cursor.getColumnIndex(TableDefine.COLUMN_FULI_USED)));
                sisters.add(sister);
            }
            close(cursor);
        }
        return sisters;
    }

    /**
     * 返回所有小姐姐
     *
     * @return
     */
    public ArrayList<Sister> queryAllSisters() {
        db = getReadableDB();
        ArrayList<Sister> sisters = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + TableDefine.TABLE_FULI, null);
        while (cursor.moveToNext()) {
            Sister sister = new Sister();
            sister.set_id(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_ID)));
            sister.setCreatedAt(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_CREATEAT)));
            sister.setDesc(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_DESC)));
            sister.setPublishedAt(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_PUBLISHEDAT)));
            sister.setSource(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_SOURCE)));
            sister.setType(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_TYPE)));
            sister.setUrl(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_URL)));
//            sister.setUsed(cursor.getInt(cursor.getColumnIndex(TableDefine.COLUMN_FULI_USED)));
            sisters.add(sister);
        }
        close(cursor);
        return sisters;
    }
}
