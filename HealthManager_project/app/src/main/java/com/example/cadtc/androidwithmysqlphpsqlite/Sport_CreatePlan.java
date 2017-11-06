package com.example.cadtc.androidwithmysqlphpsqlite;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import library.JSONParser;

import static java.lang.Integer.parseInt;

/**
 * Created by 仁駿 on 2017/10/4.
 */

/*
建立計畫的頁面
 */

public class Sport_CreatePlan extends Activity
{
    ImageButton timeselect, enter;
    Spinner days, hours, mins;
    TextView time;

    private Sport_MySqlite DB;

    SimpleDateFormat sDF = new SimpleDateFormat("M月d日", Locale.TAIWAN); //設定時間格式
    Date date = new Date();
    Calendar cal= Calendar.getInstance();
    String endTime;

    int clockHours = 20; //預設20:00
    int clockMinutes = 0; //提醒時間小時,分鐘
    String selectDays,selectHours,selectMinutes; //選擇的天數,運動小時時間,運動分鐘時間
    String[] daysArray = getDaysArray(); //設定選擇天數Spinner(1~30天)
    String[] hoursArray = getHoursArray(); //設定運動小時(1~5小時//)
    String[] minutesArray = {"0","15","30","45"}; //設定運動分鐘


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sport_activity_create_layout);

        timeselect = (ImageButton) findViewById(R.id.timesbtn);
        enter = (ImageButton) findViewById(R.id.enterbtn);
        days = (Spinner) findViewById(R.id.days_spn);
        hours = (Spinner) findViewById(R.id.hours_spn);
        mins = (Spinner) findViewById(R.id.mins_spn);
        time = (TextView) findViewById(R.id.time);

        time.setText(clockHours + ":0" + clockMinutes);

        //設定Spinner
        ArrayAdapter<String> Days = new ArrayAdapter<String>(
                Sport_CreatePlan.this,android.R.layout.simple_spinner_item,daysArray);
        Days.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        days.setAdapter(Days);
        days.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectDays = daysArray[position];

                cal.setTime(date);
                cal.add(cal.DAY_OF_MONTH,parseInt(selectDays));
                Date endtime = cal.getTime();
                endTime = sDF.format(endtime);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ArrayAdapter<String> Hours = new ArrayAdapter<String>(
                Sport_CreatePlan.this,android.R.layout.simple_spinner_item,hoursArray);
        Days.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hours.setAdapter(Hours);
        hours.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectHours = hoursArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ArrayAdapter<String> Minutes = new ArrayAdapter<String>(
                Sport_CreatePlan.this,android.R.layout.simple_spinner_item,minutesArray);
        Days.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mins.setAdapter(Minutes);
        mins.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectMinutes = minutesArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void timeselect(View V) //設定選擇提醒時間按鈕事件
    {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        // Create a new instance of TimePickerDialog and return it
        new TimePickerDialog(Sport_CreatePlan.this, new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute)
            {
                clockHours = hourOfDay;
                clockMinutes = minute;
                String hours = hourOfDay + "";
                String minutes = minute + "";
                if(hourOfDay < 10)
                {
                    hours = "0" + hourOfDay;
                }
                if(minute < 10)
                {
                    minutes = "0" + minute;
                }
                time.setText(hours + ":" + minutes);
            }
        }, hour, minute, false).show();
    }

    public void createclick(View V)
    {

        DB = new Sport_MySqlite(this);
        //將選擇的參數存入SQLite
        DB.putData("exist",selectDays,selectHours,selectMinutes,clockHours,clockMinutes,endTime);

        Calendar AlarmCal = Calendar.getInstance(); //設定鬧鐘提醒
        AlarmCal.set(Calendar.HOUR_OF_DAY, clockHours);
        AlarmCal.set(Calendar.MINUTE, clockMinutes);
        AlarmCal.set(Calendar.SECOND, 0);

        resetisDone();

        Long alarmTime = AlarmCal.getTimeInMillis(); //判斷時間是否隔天
        if(System.currentTimeMillis() >= alarmTime) //過天避免馬上響,將時間推前1天
            alarmTime += 86400000L;

        Intent intent = new Intent(Sport_CreatePlan.this, Sport_AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(Sport_CreatePlan.this, 101, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) Sport_CreatePlan.this.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP,alarmTime, 86400000/*1天*/,pendingIntent);

        Intent i = new Intent(getApplicationContext(), Sport_MainManager.class);
        startActivity(i);
        Sport_CreatePlan.this.finish();
    }

    public String[] getDaysArray()
    {
        String[] daysArray = new String[30];
        for(int i = 1;i <= 30;i++)
        {
            daysArray[i-1] = i+"";
        }
        return daysArray;
    }

    public String[] getHoursArray()
    {
        String[] hoursArray = new String[5];
        for(int i = 1;i <= 5;i++)
        {
            hoursArray[i-1] = i + "";
        }
        return hoursArray;
    }

    public void resetisDone()
    {
        Login_MySqlite dbs = new Login_MySqlite(Sport_CreatePlan.this);
        String account = dbs.getData().get("account");

        JSONParser jParser = new JSONParser();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag","updateisDone"));
        params.add(new BasicNameValuePair("manager","sportisDone"));
        params.add(new BasicNameValuePair("account",account));
        params.add(new BasicNameValuePair("isDone","2"));

        jParser.getJSONFromUrl("http://www.chiens.idv.tw/lccnet3h/Rex/index.php",params);
    }
}
