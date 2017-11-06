package com.example.cadtc.androidwithmysqlphpsqlite;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Student on 2017/10/11.
 */

public class Sport_AlarmBroadcastReceiver extends BroadcastReceiver
{
    int notificationId = 1;
    NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        //define a notification manager
        String serName = Context.NOTIFICATION_SERVICE;
        notificationManager = (NotificationManager)context.getSystemService(serName);

        //define notification using: icon, text, and timing.
        int icon = R.drawable.sporticon;
        long when = System.currentTimeMillis();

        //configure appearance of the notification
        String extendedTitle = "運動管理員提醒";
        String extendedText  = "該確認有沒有完成計畫摟~!";

        // set a Pending Activity to take care of the potential request the user
        // may have by clicking on the notification asking for more explanations
        Intent notificationIntent = new Intent(context, Sport_MainManager.class);
        notificationIntent.putExtra("extendedText", extendedText);
        notificationIntent.putExtra("extendedTitle", extendedTitle);
        PendingIntent launchIntent =
                PendingIntent.getActivity(context,0,notificationIntent,0);


        Notification notification = new Notification.Builder(context)
                .setAutoCancel(false)
                .setContentTitle(extendedTitle)
                .setContentText(extendedText)
                .setContentIntent(launchIntent)
                .setSmallIcon(icon)
                .setWhen(when)
                .build();

        notification.vibrate=new long[]{100,200,100,1000,100,1000};

        //trigger notification
        notificationId = 1;
        notificationManager.notify(notificationId, notification);
    }
}
