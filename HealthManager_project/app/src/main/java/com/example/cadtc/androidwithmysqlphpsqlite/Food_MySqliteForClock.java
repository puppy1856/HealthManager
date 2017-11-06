package com.example.cadtc.androidwithmysqlphpsqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;


public class Food_MySqliteForClock extends SQLiteOpenHelper
{
    // 資料庫名稱
    public static final String DATABASE_NAME = "foodManagement.db";
    //資料表名稱
    public static final String TABLE_NAME = "clock";
    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    public static final int VERSION = 1;

    public Food_MySqliteForClock(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }
    // 資料庫物件，固定的欄位變數

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( " +
                "_id INTEGER PRIMARY KEY, " +
                "hour VARCHAR(10), " +
                "minute VARCHAR(10) " +
                ");";

        db.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        final String SQL = "DROP TABLE " + TABLE_NAME + ";";
        db.execSQL(SQL);

        onCreate(db);
    }

    public void putData(String hour, String minute) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("hour", hour); // 時
        values.put("minute", minute); // 分

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    public ArrayList<HashMap<String, String>> getData(){
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + ";";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            do{
                HashMap<String,String> data = new HashMap<String,String>();
                data.put("id", cursor.getString(0));
                data.put("hour", cursor.getString(1));
                data.put("minute", cursor.getString(2));
                arrayList.add(data);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return arrayList;
    }

    public int GetCount(){
        String selectQuery = "SELECT * FROM " + TABLE_NAME + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor.getCount();
    }

    public void deleteData(String id){
        String SQL = "DELETE FROM " + TABLE_NAME + " WHERE _id = " + id + ";";
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
