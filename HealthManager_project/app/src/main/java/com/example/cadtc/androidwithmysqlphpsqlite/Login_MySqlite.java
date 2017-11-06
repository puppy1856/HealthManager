package com.example.cadtc.androidwithmysqlphpsqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;


public class Login_MySqlite extends SQLiteOpenHelper
{
    // 資料庫名稱
    public static final String DATABASE_NAME = "login_mySqlite.db";
    //資料表名稱
    public static final String TABLE_NAME = "user";
    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    public static final int VERSION = 1;

    public Login_MySqlite(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }
    // 資料庫物件，固定的欄位變數

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( " +
                "_id INTEGER PRIMARY KEY, " +
                "account VARCHAR(10), " +
                "flowerSetting VARCHAR(10), " +
                "potSetting VARCHAR(10) " +
                ");";

        db.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        final String SQL = "DROP TABLE " + TABLE_NAME + ";";
        db.execSQL(SQL);

        onCreate(db);
    }

    public void putData(String account, String flowerSetting, String potSetting) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("account", account); // 帳號
        values.put("flowerSetting", flowerSetting); // 花設定
        values.put("potSetting", potSetting); // 花盆設定

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    public void updateData(String account) {
        final String SQL = "UPDATE " + TABLE_NAME +
                           " SET account = '" + account +
                           "' WHERE _id = 1;";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL);
        db.close();
    }

    public void updateFlowerSetting(String flowerSetting) {
        final String SQL = "UPDATE " + TABLE_NAME +
                " SET flowerSetting = '" + flowerSetting +
                "' WHERE _id = 1;";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL);
        db.close();
    }

    public void updatePotSetting(String potSetting) {
        final String SQL = "UPDATE " + TABLE_NAME +
                " SET potSetting = '" + potSetting +
                "' WHERE _id = 1;";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL);
        db.close();
    }

    public HashMap<String, String> getData(){
        HashMap<String,String> data = new HashMap<String,String>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + ";";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            data.put("_id", cursor.getString(0));
            data.put("account", cursor.getString(1));
            data.put("flowerSetting", cursor.getString(2));
            data.put("potSetting", cursor.getString(3));
        }
        cursor.close();
        db.close();
        return data;
    }

    public void cleanTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        final String SQL = "DROP TABLE " + TABLE_NAME + ";";
        db.execSQL(SQL);
        db.close();
    }
}
