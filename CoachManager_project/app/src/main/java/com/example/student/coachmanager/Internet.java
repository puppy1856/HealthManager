package com.example.student.coachmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Jason on 2017/10/13.
 */

public class Internet
{

    Activity _activity;

    Internet(Activity activity){
        _activity = activity;
    }

    public void CheckInternetDialog(){
        if (!IsHaveInternet(_activity)){
            AlertDialog.Builder exitDialog=new AlertDialog.Builder(_activity);
            exitDialog.setTitle("偵測不到網路");
            exitDialog.setMessage("請確認你的網路狀態");
            exitDialog.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            exitDialog.setNegativeButton("離開程式", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    _activity.finish();
                }
            });

            exitDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (!IsHaveInternet(_activity)){
                        CheckInternetDialog();
                    }
                }
            });

            exitDialog.show();
        }
    }



    public boolean IsHaveInternet(Context context)
    {
        boolean result = false;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info=connManager.getActiveNetworkInfo();
        if (info == null || !info.isConnected())
        {
            result = false;
        }
        else
        {
            if (!info.isAvailable())
            {
                result =false;
            }
            else
            {
                result = true;
            }
        }

        return result;
    }
}
