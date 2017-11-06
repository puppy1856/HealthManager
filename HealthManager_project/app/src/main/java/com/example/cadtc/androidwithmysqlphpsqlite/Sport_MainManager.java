package com.example.cadtc.androidwithmysqlphpsqlite;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import library.JSONParser;

import static java.lang.Integer.parseInt;

/**
 * Created by 仁駿 on 2017/10/4.
 */

/*
運動管理的主頁面
 */

public class Sport_MainManager extends Activity
{
    String nowDate,checkDate,update;
    int countDays; //計畫倒數日期

    Sport_MySqlite DB; //SQLite 資料庫
    HashMap<String, String> data; //接受資料之陣列

    int clockHours,clockMinutes; //接收資料庫之數值
    String selectDays,selectHours,selectMinutes,clockTime,endTime; //接收資料庫之字串

    final String MYINTPUT = "MySportInput"; //儲存狀態
    SharedPreferences myEdit;
    SharedPreferences.Editor myEditor;
    int isDone; //判斷是否按下完成鈕,1:完成,2:未完成,0:沒有計畫

    TextView days,sport,clock,congras;
    ImageView iv;

    Intent intent;
    PendingIntent pendingIntent;
    AlarmManager alarm;

    AnimationDrawable ad;//建立AnimationDrawable物件
    int potSetting,flowerSetting;  //花 和 花盆設定值

    int [] potBuildArrays ={R.drawable.building_1_0, R.drawable.building_2_0, R.drawable.building_3_0};  //有計畫花盆

    int [] potFailueArrays = {R.drawable.failure_1_0, R.drawable.failure_2_0, R.drawable.failure_3_0};    //失敗枯萎花盆

    boolean DayFailue;  //過天失敗 動畫用

    int [][] potFlowerArrays = {                 //成功花盆
            {R.drawable.success_1_1, R.drawable.success_1_2, R.drawable.success_1_3},
            {R.drawable.success_2_1, R.drawable.success_2_2, R.drawable.success_2_3},
            {R.drawable.success_3_1, R.drawable.success_3_2, R.drawable.success_3_3}};

    Login_MySqlite loginMySqlite;               //拿取 花 和 花盆設定值 用

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sport_activity_mainmanager_layout);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); //強制開啟主程式可以下載東西*
        StrictMode.setThreadPolicy(policy);

        days = (TextView)findViewById(R.id.days);
        sport = (TextView)findViewById(R.id.sport);
        clock = (TextView)findViewById(R.id.clock);
        congras = (TextView)findViewById(R.id.congras);
        iv = (ImageView)findViewById(R.id.iv);

        loginMySqlite = new Login_MySqlite(Sport_MainManager.this);
        potSetting = Integer.parseInt(loginMySqlite.getData().get("potSetting") +""); //抓圖片設定之參數
        flowerSetting = Integer.parseInt(loginMySqlite.getData().get("flowerSetting") +"");

        DB = new Sport_MySqlite(this); //接收資料庫之資料
        data = DB.getData();
        selectDays = data.get("selectDays");
        selectHours = data.get("selectHours");
        selectMinutes = data.get("selectMinutes");
        clockHours = parseInt(data.get("clockHours"));
        clockMinutes = parseInt(data.get("clockMinutes"));
        endTime = data.get("endDate");
        DB.close();

        myEdit = getSharedPreferences(MYINTPUT, 0); //狀態寫入
        myEditor = myEdit.edit();
        //if(myEdit != null && myEdit.contains("isDone")) applyDone();
        if(!myEdit.contains("countDays"))
        {
            countDays = parseInt(selectDays); //將倒數日期寫入
            myEditor.putInt("countDays",countDays);
            myEditor.commit();
        }
        else applyCountDays();

        JSONObject getisDone = getisDone();
        Log.e("getisDone",getisDone+"");
        try
        {
            isDone = getisDone.getInt("sportisDone");
            Log.e("isDone",isDone+"");
        }
        catch (Exception e) {}

        checkDate(); //確認是否過日了,將方法打包寫成Function

        if(isDone == 1) //如果已經按下完成按鈕
        {
            congras.setText("恭喜你完成目標了!隔天會上傳資料!");

            ad=(AnimationDrawable) ContextCompat.getDrawable(this,potFlowerArrays[potSetting][flowerSetting]);//和編輯好的目標xml連結
            iv.setImageDrawable(ad);//與目標的imageView連結
            ad.start();//直接開始撥放
        }
        else if(isDone == 2 && !DayFailue)
        {
            ad=(AnimationDrawable) ContextCompat.getDrawable(this,potBuildArrays[potSetting]);//和編輯好的目標xml連結 //設定盆栽
            iv.setImageDrawable(ad);//與目標的imageView連結
            ad.start();//直接開始撥放
        }

        if(clockHours < 10) //整理提醒時間格式,低於10後端加入0
        {
            if(clockMinutes< 10)
                clockTime = "0" + clockHours + ":" + "0" + clockMinutes;

            else clockTime = "0" + clockHours + ":" + clockMinutes;
        }
        else
        {
            if(clockMinutes< 10)
                clockTime = clockHours + ":" + "0" + clockMinutes;

            else clockTime = clockHours + ":" + clockMinutes;
        }
        days.setText(endTime);
        sport.setText(selectHours + "小時" + selectMinutes + "分鐘");
        clock.setText(clockTime);
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        Sport_MainManager.this.finish();
    }

    public void delclick(View V) //按下刪除計畫按鈕時的動作
    {
        new AlertDialog.Builder(Sport_MainManager.this)
                .setMessage("是否要刪除計畫?")
                .setNegativeButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        DB = new Sport_MySqlite(getApplicationContext());
                        DB.resetTables(); //重置SQLite資料庫
                        DB.close();

                        delisDone();

                        myEditor.remove("checkDate"); //將Check日期和完成參數重置
                        myEditor.remove("countDays");
                        //myEditor.remove("isDone");
                        //myEditor.putInt("isDone",2);
                        myEditor.commit();

                        cancelAlarm(); //取消鬧鐘提醒方法

                        Toast.makeText(Sport_MainManager.this,"刪除計畫成功!", Toast.LENGTH_SHORT).show();

                        Sport_MainManager.this.finish();
                    }
                })
                .setPositiveButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                }).show();
    }

    public void applyDate()
    {checkDate = myEdit.getString("checkDate",null);}

    public void applyCountDays() {countDays = myEdit.getInt("countDays",0);}

    public void checkDate() //確認是否過日的方法
    {
        SimpleDateFormat DF = new SimpleDateFormat("Md", Locale.TAIWAN); //判定是否過隔天設定時間格式
        Date date = new Date();
        Calendar cal2= Calendar.getInstance();
        cal2.setTime(date); //將現在時間接收至nowDate字串
        Date nowdate = cal2.getTime();
        nowDate = DF.format(nowdate);

        if(!myEdit.contains("checkDate"))  //如果沒有存過日期,將一天開始時的日期存至狀態中
        {
            myEditor.putString("checkDate",nowDate);
            myEditor.commit();
        }

        if(myEdit != null && myEdit.contains("checkDate")) applyDate();

        Log.v("checkDate",checkDate+"");
        Log.v("nowDate",nowDate+"");

        if(myEdit.contains("checkDate"))
        {
            if (!nowDate.equals(checkDate)) //現在日期與建立時的日期不同代表已經過了12點,要將頁面更新並上傳資料庫
            {
                String toastmsg;
                if (isDone == 1)
                {
                    update = "2"; //資料庫上傳參數 1:計畫失敗 2:計畫成功
                    toastmsg = "恭喜你!前一天的目標已達成!∠(^ー^)";
                } else if (isDone == 2)
                {
                    update = "1";
                    DayFailue = true;

                    ad=(AnimationDrawable) ContextCompat.getDrawable(this,potFailueArrays[potSetting]);//失敗動畫
                    iv.setImageDrawable(ad);//與目標的imageView連結
                    ad.start();

                    toastmsg = "很可惜!前一天的目標失敗了!(´･ω･`)";
                } else toastmsg = "加油!努力達成目標吧!(･`ω･´)";

                JSONObject jsn = update();

                resetisDone();//初始化完成參數
                isDone = 2;

                //myEditor.remove("isDone");
                myEditor.remove("checkDate");
                //myEditor.putInt("isDone",2);
                myEditor.putString("checkDate",nowDate); //清空設定後設定建立日期
                myEditor.commit();

                congras.setText("");

                Toast.makeText(Sport_MainManager.this, toastmsg, Toast.LENGTH_LONG).show();

                countDays -= 1; //計畫倒數日期前進一天
                if(countDays < 1) //如果計畫日期倒數到０時代表計畫結束
                {
                    DB = new Sport_MySqlite(getApplicationContext());
                    DB.resetTables(); //重置SQLite資料庫
                    DB.close();

                    iv.setImageResource(R.drawable.def);

                    delisDone();

                    myEditor.remove("checkDate"); //將Check日期和完成參數重置
                    myEditor.remove("countDays");
                    //myEditor.remove("isDone");
                    //myEditor.putInt("isDone",2);
                    myEditor.commit();
                    cancelAlarm(); //取消鬧鐘提醒方法

                    new AlertDialog.Builder(Sport_MainManager.this)
                            .setMessage("您的計畫已經到齊!日期:" + endTime)
                            .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    Sport_MainManager.this.finish();
                                }
                            }).show();
                }
                else //計畫還沒到期
                {
                    myEditor.remove("countDays");
                    myEditor.putInt("countDays",0);
                    myEditor.commit();
                    Toast.makeText(Sport_MainManager.this,"剩下" + countDays + "天，加油喔!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void cancelAlarm() //取消提醒
    {
        intent = new Intent(Sport_MainManager.this, Sport_AlarmBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(Sport_MainManager.this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm = (AlarmManager) Sport_MainManager.this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pendingIntent);
    }

    public JSONObject update()
    {
        SimpleDateFormat DF = new SimpleDateFormat("YYYYMMdd", Locale.TAIWAN); //日期格式
        Date date = new Date();
        Calendar cal= Calendar.getInstance();
        cal.setTime(date); //將現在時間接收至nowDate字串
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date nowdate = cal.getTime();
        String dataDate = DF.format(nowdate);

        Login_MySqlite dbs = new Login_MySqlite(Sport_MainManager.this);
        String account = dbs.getData().get("account");

        JSONParser jParser = new JSONParser();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag","update"));
        params.add(new BasicNameValuePair("account",account));
        params.add(new BasicNameValuePair("date",dataDate));
        params.add(new BasicNameValuePair("plan","sport"));
        params.add(new BasicNameValuePair("isdone",update+""));

        Log.e("account", account + "");
        Log.e("date", dataDate + "");
        Log.e("isdone", update + "");

        JSONObject json = jParser.getJSONFromUrl("http://www.chiens.idv.tw/lccnet3h/Rex/index.php",params);
        return json;
    }

    public void resetisDone()
    {
        Login_MySqlite dbs = new Login_MySqlite(Sport_MainManager.this);
        String account = dbs.getData().get("account");

        JSONParser jParser = new JSONParser();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag","updateisDone"));
        params.add(new BasicNameValuePair("manager","sportisDone"));
        params.add(new BasicNameValuePair("account",account));
        params.add(new BasicNameValuePair("isDone","2"));

        jParser.getJSONFromUrl("http://www.chiens.idv.tw/lccnet3h/Rex/index.php",params);
    }

    public void delisDone()
    {
        Login_MySqlite dbs = new Login_MySqlite(Sport_MainManager.this);
        String account = dbs.getData().get("account");

        JSONParser jParser = new JSONParser();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag","updateisDone"));
        params.add(new BasicNameValuePair("manager","sportisDone"));
        params.add(new BasicNameValuePair("account",account));
        params.add(new BasicNameValuePair("isDone","0"));

        jParser.getJSONFromUrl("http://www.chiens.idv.tw/lccnet3h/Rex/index.php",params);
    }

    public JSONObject getisDone()
    {
        Login_MySqlite dbs = new Login_MySqlite(Sport_MainManager.this);
        String account = dbs.getData().get("account");

        JSONParser jParser = new JSONParser();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag","getisDone"));
        params.add(new BasicNameValuePair("account",account));

        JSONObject jsn = jParser.getJSONFromUrl("http://www.chiens.idv.tw/lccnet3h/Rex/index.php",params);
        Log.e("jsn",jsn+"");
        return  jsn;
    }
}