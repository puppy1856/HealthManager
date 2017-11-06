package com.example.cadtc.androidwithmysqlphpsqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;


public class Food_MySqlite extends SQLiteOpenHelper
{
    // 資料庫名稱
    public static final String DATABASE_NAME = "foodManagement.db";
    //資料表名稱
    public static final String TABLE_NAME = "setting";
    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    public static final int VERSION = 1;

    public Food_MySqlite(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }
    // 資料庫物件，固定的欄位變數

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( " +
                "_id INTEGER PRIMARY KEY, " +
                "checkPlan VARCHAR(10) DEFAULT NULL, " +
                "sex VARCHAR(10), " +
                "age VARCHAR(10), " +
                "height VARCHAR(10), " +
                "weight VARCHAR(10), " +
                "activity VARCHAR(10), " +
                "targetWeight VARCHAR(10), " +
                "targetDay VARCHAR(10) " +
                ");";

        db.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        final String SQL = "DROP TABLE " + TABLE_NAME + ";";
        db.execSQL(SQL);

        onCreate(db);
    }

    public void putData(String checkPlan, String sex, String age, String height, String weight, String activity, String targetWeight, String targetDay) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("checkPlan", checkPlan); // 確認計畫存在參數
        values.put("sex", sex); // 性別
        values.put("age", age); // 年齡
        values.put("height", height); // 身高
        values.put("weight", weight); // 體重
        values.put("activity", activity); // 平日活動
        values.put("targetWeight", targetWeight); // 目標體重
        values.put("targetDay", targetDay); // 目標天數

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }
    public void UpdateCheckPlan(String checkPlan)
    {
        final String SQL = "UPDATE " + TABLE_NAME + " SET checkPlan = " + checkPlan + ";";
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(SQL);
        db.close(); // Closing database connection
    }

    public HashMap<String, String> getData(){
        HashMap<String,String> data = new HashMap<String,String>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + ";";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            data.put("checkPlan", cursor.getString(1));
            data.put("sex", cursor.getString(2));
            data.put("age", cursor.getString(3));
            data.put("height", cursor.getString(4));
            data.put("weight", cursor.getString(5));
            data.put("activity", cursor.getString(6));
            data.put("targetWeight", cursor.getString(7));
            data.put("targetDay", cursor.getString(8));
        }
        cursor.close();
        db.close();

        return data;
    }

    public void UpdateTargetDay(String targetDay) {
        final String SQL = "UPDATE " + TABLE_NAME +
                " SET targetDay = '" + targetDay +
                "' WHERE _id = 1;";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL);
        db.close();
    }

    public void cleanTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        final String SQL = "DROP TABLE " + TABLE_NAME + ";";
        db.execSQL(SQL);
        db.close();
    }
}
