package com.example.cadtc.androidwithmysqlphpsqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

/**
 * Created by Student on 2017/10/6.
 */

/*
MySQLite功能
 */

public class Sport_MySqlite extends SQLiteOpenHelper
{
    // 資料庫名稱
    public static final String DATABASE_NAME = "mydata.db";
    //資料表名稱
    public static final String TABLE_NAME = "data";
    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    public static final int VERSION = 1;

    public Sport_MySqlite(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }
    // 資料庫物件，固定的欄位變數

    @Override
    public void onCreate(SQLiteDatabase db) //建立資料庫
    {
        final String SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( " +
                "_id INTEGER PRIMARY KEY, " +
                "checkPlan VARCHAR(10) DEFAULT NULL, " +
                "selectDays VARCHAR(2), " +
                "selectHours VARCHAR(2), " +
                "selectMinutes VARCHAR(2), " +
                "clockHours INTEGER(2), " +
                "clockMinutes INTEGER(2), " +
                "endDate VARCHAR(6)" +
                ");";

        db.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1)
    {
        final String SQL = "DROP TABLE " + TABLE_NAME;
        db.execSQL(SQL);

        onCreate(db);
    }

    public void putData(String checkP, String seclectD, String selectH,
                        String selectM, int clockH, int clockM, String endDate)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("checkPlan", checkP); // 確認計畫存在參數
        values.put("selectDays", seclectD); // 計畫天數
        values.put("selectHours", selectH); // 運動時數
        values.put("selectMinutes", selectM); // 運動分鐘數
        values.put("clockHours", clockH + ""); // 提醒時間小時
        values.put("clockMinutes", clockM + ""); // 提醒時間分鐘
        values.put("endDate", endDate); //計畫結束日期

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    //讀取資料內容放在HashMap裡
    public HashMap<String, String> getData(){
        HashMap<String,String> data = new HashMap<String,String>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + ";";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            data.put("checkPlan", cursor.getString(1));
            data.put("selectDays", cursor.getString(2));
            data.put("selectHours", cursor.getString(3));
            data.put("selectMinutes", cursor.getString(4));
            data.put("clockHours", cursor.getString(5));
            data.put("clockMinutes", cursor.getString(6));
            data.put("endDate", cursor.getString(7));
        }
        cursor.close();
        db.close();

        return data;
    }

    public void resetTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}
