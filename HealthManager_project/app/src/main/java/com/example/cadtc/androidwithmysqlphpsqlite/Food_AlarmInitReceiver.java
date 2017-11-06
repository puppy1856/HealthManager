package com.example.cadtc.androidwithmysqlphpsqlite;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Food_AlarmInitReceiver extends BroadcastReceiver {

    Food_MySqliteForClock mySqliteForClock;

    @Override
    public void onReceive(Context context, Intent intent) {

        mySqliteForClock = new Food_MySqliteForClock(context);

        for (int i=0;i<mySqliteForClock.GetCount();i++){
            ArrayList<HashMap<String, String>> sqlData = mySqliteForClock.getData();
            int hour = Integer.parseInt(sqlData.get(i).get("hour"));
            int minute = Integer.parseInt(sqlData.get(i).get("minute"));

            final Calendar calendar;
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);

            //防止設定過去時間，立即觸發alarm的BUG
            long startLong = calendar.getTimeInMillis();
            if(System.currentTimeMillis()>=startLong)
                startLong += 1000*60*60*24L;


            Intent intent2 = new Intent(context, Food_MyBroadcastReceiver.class);
            intent2.putExtra("msg","該確認您的飲食計畫摟!" + hour + "-" + minute);
            intent2.addCategory((i+1)+"");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            //alarm.set(AlarmManager.RTC_WAKEUP, startLong, pendingIntent);
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, startLong,86400000, pendingIntent);
        }
    }
}
