package com.example.cadtc.androidwithmysqlphpsqlite;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.HashMap;

import static java.lang.Integer.parseInt;

/**
 * Created by Student on 2017/10/11.
 */

/*
這裡是開機時重新註冊鬧鐘提醒
有計畫時才註冊,沒有計畫時不做動作
 */
public class Sport_AlarmInitReceiver extends BroadcastReceiver
{
    int clockHours,clockMinutes;

    Sport_MySqlite DB; //SQLite 資料庫
    HashMap<String, String> data; //接受資料之陣列
    @Override
    public void onReceive(Context context, Intent intent)
    {
        DB = new Sport_MySqlite(context); //接收資料庫之資料
        data = DB.getData();
        DB.close();

        if(data.containsKey("clockHours"))
        {
            clockHours = parseInt(data.get("clockHours"));
            clockMinutes = parseInt(data.get("clockMinutes"));

            Calendar AlarmCal = Calendar.getInstance(); //設定鬧鐘提醒
            AlarmCal.set(Calendar.HOUR_OF_DAY, clockHours);
            AlarmCal.set(Calendar.MINUTE, clockMinutes);
            AlarmCal.set(Calendar.SECOND, 0);

            Long alarmTime = AlarmCal.getTimeInMillis(); //判斷時間是否隔天
            if(System.currentTimeMillis() >= alarmTime) //過天避免馬上響,將時間推前1天
                alarmTime += 86400000L;

            Intent it = new Intent(context, Sport_AlarmBroadcastReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 101, it, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, 86400000/*1天*/, pendingIntent);

            //Toast.makeText(context, "鬧鐘設定成功!" + clockHours + ":" + clockMinutes, Toast.LENGTH_SHORT).show();
        }
        //else Toast.makeText(context,"鬧鐘提醒設定失敗!",Toast.LENGTH_SHORT).show();
    }
}
