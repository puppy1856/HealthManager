package com.example.cadtc.androidwithmysqlphpsqlite;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class Food_MyBroadcastReceiver extends BroadcastReceiver {

    int notificationId = 1;
    NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle tData = intent.getExtras();

        Log.e("account", "alarm clock");

        //define a notification manager
        String serName = Context.NOTIFICATION_SERVICE;
        notificationManager = (NotificationManager)context.getSystemService(serName);

        //define notification using: icon, text, and timing.
        long when = System.currentTimeMillis();

        //configure appearance of the notification
        String extendedTitle = "提醒";
        String extendedText  = tData.get("msg")+"";

        // set a Pending Activity to take care of the potential request the user
        // may have by clicking on the notification asking for more explanations
        Intent notificationIntent = new Intent(context, FoodManagment_Main.class);
        notificationIntent.putExtra("extendedText", extendedText);
        notificationIntent.putExtra("extendedTitle", extendedTitle);
        PendingIntent launchIntent =
                PendingIntent.getActivity(context,0,notificationIntent,0);


        Notification notification = new Notification.Builder(context)
                .setAutoCancel(false)
                .setContentTitle(extendedTitle)
                .setContentText(extendedText)
                .setContentIntent(launchIntent)
                .setSmallIcon(R.drawable.btn_star_big_on_selected)
                .setWhen(when)
                .build();

        notification.vibrate=new long[]{100,200,100,1000,100,2000};

        //trigger notification
        notificationId = 1;
        notificationManager.notify(notificationId, notification);
    }
}