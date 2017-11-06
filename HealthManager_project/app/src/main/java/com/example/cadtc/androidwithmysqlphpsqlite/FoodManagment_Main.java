package com.example.cadtc.androidwithmysqlphpsqlite;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import library.JSONParser;

public class FoodManagment_Main extends Activity {

    TextView textDayCal;

    TextView textBMI,textDayNeedCal,textDayNeedCal2,textDayNeedCal_average,textDayNeedCal_suggest,main_textDayNeedCal_target,doneTextView;

    Food_ProjectSetting projectSetting = new Food_ProjectSetting(0,25,100.0,30.0,"輕度活動",30.0,1.0);

    String[] items,items2,items3,items4;

    ListView tab2ListView;
    MyAdapter tab2Adapter;

    ImageView img;

    Food_MySqlite mySqlite;
    Food_MySqliteForClock mySqliteForClock;

    //TODO 再增加一個deleteProjectButton2做為完成後的delete按鈕移位
    ImageButton createProjectButton,deleteProjectButton,doneBtn, deleteProjectButton2;

    AnimationDrawable ad;//建立AnimationDrawable物件

    //儲存過天資料
    SharedPreferences myEdit;
    SharedPreferences.Editor myEditor;

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    int potSetting,flowerSetting;  //花 和 花盆設定值

    int [] potInitArrays = {R.drawable.pot_1,R.drawable.pot_2,R.drawable.pot_3};   //初始花盆

    int [] potBuildArrays ={R.drawable.building_1_0,R.drawable.building_2_0,R.drawable.building_3_0};  //有計畫花盆

    int [] potFailueArrays = {R.drawable.failure_1_0,R.drawable.failure_2_0,R.drawable.failure_3_0};    //失敗枯萎花盆
    boolean DayFailue = false;  //過天失敗  動畫用

    int [][] potFlowerArrays = {                 //成功花盆
            {R.drawable.success_1_1,R.drawable.success_1_2,R.drawable.success_1_3},
            {R.drawable.success_2_1,R.drawable.success_2_2,R.drawable.success_2_3},
            {R.drawable.success_3_1,R.drawable.success_3_2,R.drawable.success_3_3}};

    Login_MySqlite loginMySqlite;               //拿取 花 和 花盆設定值 用

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.food_main);

        //網路要求權限  暴力破解
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        InitUI();

        //Load Sqlite
        try {
            LoadSQLData();
        }catch(Exception e) {
            mySqlite.onCreate(mySqlite.getWritableDatabase());
        }

        //Load SqliteForClock
        try {
            LoadSQLDataForClock();
        }catch(Exception e) {
            mySqliteForClock.onCreate(mySqliteForClock.getWritableDatabase());
        }

        //TODO OnCreate時判斷是否完成計畫
        //Load Login_MySqlite
        potSetting = Integer.parseInt(loginMySqlite.getData().get("potSetting") +"");
        flowerSetting = Integer.parseInt(loginMySqlite.getData().get("flowerSetting") +"");

        checkDate();  //判斷過日
        UpdateUI();   //更新UI介面
    }// onCreate

    public void InitUI(){
        img = (ImageView) findViewById(R.id.imageView);

        createProjectButton = (ImageButton)findViewById(R.id.createProjectButton) ;
        deleteProjectButton = (ImageButton)findViewById(R.id.deleteProjectButton) ;
        deleteProjectButton2 = (ImageButton)findViewById(R.id.deleteProjectButton2);

        doneTextView = (TextView) findViewById(R.id.doneTextView);
        doneBtn = (ImageButton) findViewById(R.id.doneBtn);

        textDayCal = (TextView) findViewById(R.id.textDayCal);
        main_textDayNeedCal_target = (TextView)findViewById(R.id.main_textDayNeedCal_target);

        tab2Adapter = new MyAdapter();
        tab2ListView = (ListView) findViewById(R.id.tab2_listview);
        tab2ListView.setAdapter(tab2Adapter);

        mySqlite = new Food_MySqlite(FoodManagment_Main.this);
        mySqliteForClock = new Food_MySqliteForClock(FoodManagment_Main.this);
        loginMySqlite = new Login_MySqlite(FoodManagment_Main.this);

        myEdit = getSharedPreferences("MySharedPreferences", 0); //狀態寫入
        myEditor = myEdit.edit();

        initTab();
    }

    public void initTab(){

        TabHost tabs = (TabHost) findViewById(R.id.tabhost);

        tabs.setup();

        TabHost.TabSpec spec;
        //tab1
        spec = tabs.newTabSpec("tag1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("飲食計畫");
        tabs.addTab(spec);

        //tab2
        spec = tabs.newTabSpec("tag2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("鬧鐘提醒");
        tabs.addTab(spec);

        tabs.setCurrentTab(0);
    }

    public void LoadSQLData(){
        HashMap<String, String> sqlData = mySqlite.getData();

        int sex = Integer.parseInt(sqlData.get("sex"));
        int age = Integer.parseInt(sqlData.get("age"));

        double height = Double.parseDouble(sqlData.get("height"));
        double weight = Double.parseDouble(sqlData.get("weight"));
        String activity = sqlData.get("activity");
        double targetWeight = Double.parseDouble(sqlData.get("targetWeight"));
        double targetDay = Double.parseDouble(sqlData.get("targetDay"));

        projectSetting.SetProjectSetting(sex,age,height,weight,activity,targetWeight,targetDay);
    }

    public void LoadSQLDataForClock(){
        for (int i=0;i<mySqliteForClock.GetCount();i++){
            ArrayList<HashMap<String, String>> sqlData = mySqliteForClock.getData();
            int id = Integer.parseInt(sqlData.get(i).get("id"));
            int hour = Integer.parseInt(sqlData.get(i).get("hour"));
            int minute = Integer.parseInt(sqlData.get(i).get("minute"));

            clockIdArrayList.add(id+"");
            hoursArrayList.add(hour+"");
            minutesArrayList.add(minute+"");
            tab2Adapter.addItem(tab2Adapter.getCount() + 1);
            tab2Adapter.notifyDataSetChanged();

            CreateAlarmManager(i+1,hour,minute, "該確認飲食計畫摟!");
        }
    }

    public void UpdateUI(){

        textDayCal.setText(projectSetting.GetDayNeedCalSuggest());//每日目標所需熱量
        main_textDayNeedCal_target.setText(projectSetting._targetDay+"");//剩餘天數

        String s = "";
        try
        {
            s = getisDone().getString("foodisDone") + "";
        }catch (Exception e){

        }

        if (s.equals("1"))
        {
            mySqlite.UpdateCheckPlan("2");
        }
        else if (s.equals("2"))
            mySqlite.UpdateCheckPlan("1");

        HashMap<String, String> sqlData = mySqlite.getData();

        String checkPlan = sqlData.get("checkPlan") + "";
        if (checkPlan.equals("null")){  //沒計畫
            textDayCal.setText("沒計畫");//每日目標所需熱量
            main_textDayNeedCal_target.setText("沒計畫");//剩餘天數
            createProjectButton.setVisibility(View.VISIBLE);
            deleteProjectButton.setVisibility(View.INVISIBLE);
            deleteProjectButton2.setVisibility(View.INVISIBLE);
            img.setImageResource(potInitArrays[potSetting]);
        }
        else{       //有計畫
            createProjectButton.setVisibility(View.INVISIBLE);
            deleteProjectButton.setVisibility(View.VISIBLE);
            deleteProjectButton2.setVisibility(View.INVISIBLE);

            if (checkPlan.equals("1") && !DayFailue) {    //有計畫  還沒按下成功
                doneTextView.setText("");
                doneBtn.setVisibility(View.VISIBLE);
                ad=(AnimationDrawable) ContextCompat.getDrawable(this,potBuildArrays[potSetting]);//和編輯好的目標xml連結
            }
            else if (checkPlan.equals("2")){                          //有計畫  已經按下成功
                ad=(AnimationDrawable) ContextCompat.getDrawable(this,potFlowerArrays[potSetting][flowerSetting]);//和編輯好的目標xml連結
                doneTextView.setText("恭喜你完成今天目標,請等待明天上傳資料");
                deleteProjectButton.setVisibility(View.INVISIBLE);
                deleteProjectButton2.setVisibility(View.VISIBLE);
            }
            img.setImageDrawable(ad);//與目標的imageView連結
            ad.start();
        }
    }

   public void CreateProjectButton(View v) {

       AlertDialog.Builder builder = new AlertDialog.Builder(FoodManagment_Main.this);

       View mView = getLayoutInflater().inflate(R.layout.food_create_poject_dialog, null);
       builder.setTitle("Create Project");
       initCreateDialog(mView);

       //set three option buttons
       builder.setNegativeButton("Create", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int whichButton) {
               if (!textDayNeedCal_suggest.getText().equals("有害健康請降低目標或增加天數")) {
                   CreateProject();

                   createProjectButton.setVisibility(View.INVISIBLE);
                   deleteProjectButton.setVisibility(View.VISIBLE);
                   //TODO 控制delete2為隱藏
                   deleteProjectButton2.setVisibility(View.INVISIBLE);
               }
               else{
                   Toast.makeText(FoodManagment_Main.this,"有害健康請降低目標或增加天數",Toast.LENGTH_SHORT).show();
               }
           }
       });//setPositiveButton

       builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int whichButton) {
               dialog.dismiss();
           }
       });//setPositiveButton

       builder.setView(mView);
       AlertDialog alertDialog = builder.create();

       alertDialog.show();
    }

    public void deleteProjectButton(View v){
        AlertDialog.Builder deleteDialog=new AlertDialog.Builder(FoodManagment_Main.this);
        deleteDialog.setTitle("Delete Project");
        deleteDialog.setMessage("確定要刪除嗎?");

        deleteDialog.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeleteProject();
            }
        });

        deleteDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        deleteDialog.show();
    }

    public void initCreateDialog(View mView){
        final Spinner spinner_height = (Spinner)mView.findViewById(R.id.spinner_height);
        spinner_height.setOnItemSelectedListener(new MyOnItemSelectedListener());

        final Spinner spinner_weight = (Spinner) mView.findViewById(R.id.spinner_weight);
        spinner_weight.setOnItemSelectedListener(new MyOnItemSelectedListener());

        final Spinner spinner_activity = (Spinner) mView.findViewById(R.id.spinner_activity);
        spinner_activity.setOnItemSelectedListener(new MyOnItemSelectedListener());

        final Spinner targetWeight = (Spinner) mView.findViewById(R.id.targetWeight);
        targetWeight.setOnItemSelectedListener(new MyOnItemSelectedListener());

        final Spinner spinner_targetDay = (Spinner) mView.findViewById(R.id.spinner_targetDay);
        spinner_targetDay.setOnItemSelectedListener(new MyOnItemSelectedListener());

        textBMI = (TextView) mView.findViewById(R.id.textBMI);
        textDayNeedCal = (TextView) mView.findViewById(R.id.textDayNeedCal);
        textDayNeedCal2 = (TextView) mView.findViewById(R.id.textDayNeedCal_target);
        textDayNeedCal_average = (TextView) mView.findViewById(R.id.textDayNeedCal_average);
        textDayNeedCal_suggest = (TextView) mView.findViewById(R.id.textDayNeedCal_suggest);

        items = new String[100];
        for (int i=0;i<100;i++){
            items[i] = (i+100) +"";
        }

        items2 = new String[90];
        for (int i=0;i<90;i++){
            items2[i] = (i+30)+"";
        }

        items3 = new String[100];
        for (int i=0;i<100;i++){
            items3[i] = (i+1)+"";
        }

        items4 = new String[]{"輕度活動","中度活動","重度活動"};


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                FoodManagment_Main.this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                FoodManagment_Main.this, android.R.layout.simple_spinner_item, items2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(
                FoodManagment_Main.this, android.R.layout.simple_spinner_item, items3);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(
                FoodManagment_Main.this, android.R.layout.simple_spinner_item, items4);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_height.setAdapter(adapter);
        spinner_weight.setAdapter(adapter2);
        spinner_activity.setAdapter(adapter4);
        targetWeight.setAdapter(adapter2);
        spinner_targetDay.setAdapter(adapter3);
    }

    public class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,View view, int pos, long id) {
            try
            {
                if (parent.getId() == R.id.spinner_height) {
                    projectSetting._height = Double.parseDouble(items[pos]);
                }
                else if (parent.getId() == R.id.spinner_weight) {
                    projectSetting._weight = Double.parseDouble(items2[pos]);
                }
                else if (parent.getId() == R.id.spinner_activity) {
                    projectSetting._activity = items4[pos];
                }
                else if (parent.getId() == R.id.targetWeight) {
                    projectSetting._targetWeight = Double.parseDouble(items2[pos]);
                }
                else if (parent.getId() == R.id.spinner_targetDay) {
                    projectSetting._targetDay = Double.parseDouble(items3[pos]);
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            finally {
                textBMI.setText(projectSetting.GetStringBMI());
                textDayNeedCal.setText(projectSetting.GetStringDayNeedCal());
                textDayNeedCal2.setText(projectSetting.GetStringTargetDayNeedCal());
                textDayNeedCal_average.setText(projectSetting.GetStringTotalDayNeedCal());
                textDayNeedCal_suggest.setText(projectSetting.GetDayNeedCalSuggest());
            }
        }
        public void onNothingSelected(AdapterView<?> parent) {
            Toast.makeText(getBaseContext(),"NO Selected",Toast.LENGTH_SHORT).show();
        }
    }

    public void CreateProject(){
        resetisDone();

        textDayCal.setText(projectSetting.GetDayNeedCalSuggest());
        main_textDayNeedCal_target.setText(projectSetting._targetDay + "");

        mySqlite = new Food_MySqlite(FoodManagment_Main.this);

        String checkPlan = "1";
        String sex = projectSetting._sex + "";
        String age = projectSetting._age + "";
        String height = projectSetting._height + "";
        String weight = projectSetting._weight + "";
        String activity = projectSetting._activity + "";
        String targetWeight = projectSetting._targetWeight + "";
        String targetDay = projectSetting._targetDay + "";

        mySqlite.putData(checkPlan, sex, age, height, weight, activity, targetWeight, targetDay);

        doneTextView.setText("");
        doneBtn.setVisibility(View.VISIBLE);

        ad=(AnimationDrawable) ContextCompat.getDrawable(this,potBuildArrays[potSetting]);//和編輯好的目標xml連結
        img.setImageDrawable(ad);//與目標的imageView連結
        ad.start();
    }

    public void DeleteProject (){
        delisDone();

        textDayCal.setText("沒計畫");
        main_textDayNeedCal_target.setText("沒計畫");
        createProjectButton.setVisibility(View.VISIBLE);
        deleteProjectButton.setVisibility(View.INVISIBLE);
        //TODO 控制delete2為隱藏
        deleteProjectButton2.setVisibility(View.INVISIBLE);
        doneTextView.setText("");
        doneBtn.setVisibility(View.INVISIBLE);

        projectSetting.Init();
        mySqlite.cleanTable();
        mySqlite.onCreate(mySqlite.getWritableDatabase());

        img.setImageResource(potInitArrays[potSetting]);
    }

    boolean boolSuccess = false;

    public void Success(View v){
        new AlertDialog.Builder(FoodManagment_Main.this)
                .setMessage("確定今天的目標已經完成?")
                .setNegativeButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        isDone();

                        mySqlite.UpdateCheckPlan("2");
                        //boolSuccess =true;
                        //DayUpdate("2");//上傳資料

                        doneTextView.setText("恭喜你完成今天目標,請等待明天上傳資料");
                        doneBtn.setVisibility(View.INVISIBLE);
                        //TODO 置換deletebutton
                        deleteProjectButton.setVisibility(View.INVISIBLE);
                        deleteProjectButton2.setVisibility(View.VISIBLE);
                        //TODO 刪除deletebutton

                        //取設定  換圖片
                        ad=(AnimationDrawable) ContextCompat.getDrawable(FoodManagment_Main.this,potFlowerArrays[potSetting][flowerSetting]);//和編輯好的目標xml連結
                        img.setImageDrawable(ad);//與目標的imageView連結
                        ad.start();

                    }
                })
                .setPositiveButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {}
                }).show();

    }

    /////////////////////////////////////////TAB2//////////////////////////////////////////////////////////////////////

    ArrayList<String> clockIdArrayList = new ArrayList<String>(),
            hoursArrayList = new ArrayList<String>(),
            minutesArrayList = new ArrayList<String>();
    ArrayList<PendingIntent> pendingIntentArrayList = new ArrayList<PendingIntent>();

    public void AddListViewDialog(View v){
        //時間格式
        Calendar calendar;
        calendar = Calendar.getInstance();
        TimePickerDialog myTPDialog = new TimePickerDialog(this,mTimeSetListener,calendar.get(Calendar.HOUR),calendar.get(Calendar.MINUTE),false);
        myTPDialog.show();
    }

    public  TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            String hourString,minuteString;
            if(hourOfDay < 10)
                hourString = "0" + hourOfDay;
            else
                hourString = hourOfDay + "";

            if(minute < 10)
                minuteString = "0" + minute;
            else
                minuteString = minute + "";

            mySqliteForClock.putData(hourString,minuteString);

            String clockId = mySqliteForClock.getData().get(tab2Adapter.getCount()).get("id");
            clockIdArrayList.add(clockId);
            hoursArrayList.add(hourString);
            minutesArrayList.add(minuteString);
            tab2Adapter.addItem(tab2Adapter.getCount() + 1);
            tab2Adapter.notifyDataSetChanged();

            CreateAlarmManager(tab2Adapter.getCount(),hourOfDay,minute,"該確認飲食計畫摟!");
        }
    };

    public void CreateAlarmManager(int Id,int hourOfDay,int minute, String msg){
        Calendar calendar;
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        //防止設定過去時間，立即觸發alarm的BUG
        long startLong = calendar.getTimeInMillis();
        if(System.currentTimeMillis()>=startLong)
            startLong += 1000*60*60*24L;

        Intent intent;
        intent = new Intent(FoodManagment_Main.this, Food_MyBroadcastReceiver.class);
        intent.addCategory(Id+"");  //利用Category 和 ID 產生出不同的鬧鐘 避免覆蓋重複
        intent.putExtra("msg",hourOfDay + " : " + minute + " - " + msg);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(FoodManagment_Main.this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntentArrayList.add(pendingIntent);

        AlarmManager alarm = (AlarmManager) FoodManagment_Main.this.getSystemService(Context.ALARM_SERVICE);

        //alarm.set(AlarmManager.RTC_WAKEUP, startLong, pendingIntent);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, startLong,86400000, pendingIntent);
    }

    public void CancelAlarmManager(int position){
        AlarmManager alarm = (AlarmManager) FoodManagment_Main.this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pendingIntentArrayList.get(position));
    }

    private class MyAdapter extends BaseAdapter {
        private ArrayList<Integer> mList;

        public MyAdapter(){
            mList = new ArrayList<>();
        }

        public void addItem(Integer i){
            mList.add(i);
        }

        public void removeItem(int index){
            mList.remove(index);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            Holder holder;
            if(v == null){
                v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.food_tab2_listview, null);
                holder = new Holder();
                holder.text = (TextView) v.findViewById(R.id.textView11);
                holder.button =  (Button) v.findViewById(R.id.deleteListBtn);
                v.setTag(holder);
            } else{
                holder = (Holder) v.getTag();
            }

            holder.text.setText(hoursArrayList.get(position) + " : " + minutesArrayList.get(position));
            holder.button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    CancelAlarmManager(position);
                    mySqliteForClock.deleteData(clockIdArrayList.get(position));
                    clockIdArrayList.remove(position);
                    hoursArrayList.remove(position);
                    minutesArrayList.remove(position);
                    removeItem(position);
                    tab2Adapter.notifyDataSetChanged();
                }
            });
            return v;
        }
        class Holder{
            TextView text;
            Button button;
        }
    }

    @Override //暫停後將頁面結束
    protected void onPause() {
        super.onPause();
       FoodManagment_Main.this.finish();
    }

    //確認是否過日的方法
    public void checkDate() {
        String nowDateDF,checkDate;   //現在時間  比較用時間
        SimpleDateFormat DF = new SimpleDateFormat("Md", Locale.TAIWAN); //判定是否過隔天設定時間格式
        Date date = new Date();
        Calendar cal2= Calendar.getInstance();
        cal2.setTime(date); //將現在時間接收至nowDate字串
        Date nowDate = cal2.getTime();
        nowDateDF = DF.format(nowDate);

        if(myEdit.contains("checkDate"))  {//如果存過日期則把資料取出並判斷
            checkDate = myEdit.getString("checkDate",null);

            String checkPlan = mySqlite.getData().get("checkPlan")+"";  //過日上傳資料  和判斷是否有計畫

            if (!nowDateDF.equals(checkDate) && !checkPlan.equals("null")) {//現在日期與建立時的日期不同代表已經過了12點 並且有計畫  ,要將頁面更新並上傳資料庫
                resetisDone();

                DayUpdate(checkPlan);//上傳過日資料

                mySqlite.UpdateCheckPlan("1");
                projectSetting._targetDay -= 1; //日子減一
                mySqlite.UpdateTargetDay(projectSetting._targetDay+"");

                if (projectSetting._targetDay == 0.0) {     //顯示天數
                    Toast.makeText(FoodManagment_Main.this,"恭喜你，這次計畫已經完成了",Toast.LENGTH_SHORT).show();
                    DeleteProject();
                }
                else{
                    if (checkPlan.equals("1")){         //昨日失敗  失敗動畫
                        DayFailue = true;
                        ad=(AnimationDrawable) ContextCompat.getDrawable(this,potFailueArrays[potSetting]);//失敗動畫
                        img.setImageDrawable(ad);//與目標的imageView連結
                        ad.start();
                        Toast.makeText(FoodManagment_Main.this,"昨天的花枯死了，請再接再厲", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(FoodManagment_Main.this,"剩下" + projectSetting._targetDay + "天，加油喔!", Toast.LENGTH_SHORT).show();
                }


                //將設定值初始化
                myEditor.remove("checkDate");
            }
        }

        myEditor.putString("checkDate",nowDateDF); //一天開始時的日期存至狀態中
        myEditor.commit();
    }

    //過日上傳
    public void DayUpdate(String update) {
        SimpleDateFormat DF = new SimpleDateFormat("YYYYMMdd", Locale.TAIWAN); //日期格式
        Date date = new Date();
        Calendar cal= Calendar.getInstance();
        cal.setTime(date); //將現在時間接收至nowDate字串

        if (boolSuccess) {
            cal.add(Calendar.DAY_OF_MONTH, 0);
            boolSuccess = false;
        }
        else
            cal.add(Calendar.DAY_OF_MONTH, -1);
        Date nowdate = cal.getTime();
        String dataDate = DF.format(nowdate);

        Login_MySqlite dbs = new Login_MySqlite(FoodManagment_Main.this);
        String account = dbs.getData().get("account");

        JSONParser jParser = new JSONParser();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag","update"));
        params.add(new BasicNameValuePair("account",account));
        params.add(new BasicNameValuePair("date",dataDate));
        params.add(new BasicNameValuePair("plan","food"));
        params.add(new BasicNameValuePair("isdone",update));

        Log.e("account", account + "");
        Log.e("date", dataDate + "");
        Log.e("isdone", update + "");

        jParser.getJSONFromUrl("http://www.chiens.idv.tw/lccnet3h/Rex/index.php",params);
    }

    public void resetisDone() {
        Login_MySqlite dbs = new Login_MySqlite(FoodManagment_Main.this);
        String account = dbs.getData().get("account");

        JSONParser jParser = new JSONParser();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag","updateisDone"));
        params.add(new BasicNameValuePair("manager","foodisDone"));
        params.add(new BasicNameValuePair("account",account));
        params.add(new BasicNameValuePair("isDone","2"));

        jParser.getJSONFromUrl("http://www.chiens.idv.tw/lccnet3h/Rex/index.php",params);
    }

    public void delisDone() {
        Login_MySqlite dbs = new Login_MySqlite(FoodManagment_Main.this);
        String account = dbs.getData().get("account");

        JSONParser jParser = new JSONParser();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag","updateisDone"));
        params.add(new BasicNameValuePair("manager","foodisDone"));
        params.add(new BasicNameValuePair("account",account));
        params.add(new BasicNameValuePair("isDone","0"));

        jParser.getJSONFromUrl("http://www.chiens.idv.tw/lccnet3h/Rex/index.php",params);
    }

    public void isDone() {
        Login_MySqlite dbs = new Login_MySqlite(FoodManagment_Main.this);
        String account = dbs.getData().get("account");

        JSONParser jParser = new JSONParser();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag","updateisDone"));
        params.add(new BasicNameValuePair("manager","foodisDone"));
        params.add(new BasicNameValuePair("account",account));
        params.add(new BasicNameValuePair("isDone","1"));

        jParser.getJSONFromUrl("http://www.chiens.idv.tw/lccnet3h/Rex/index.php",params);
    }

    public JSONObject getisDone() {
        Login_MySqlite dbs = new Login_MySqlite(FoodManagment_Main.this);
        String account = dbs.getData().get("account");

        JSONParser jParser = new JSONParser();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag","getisDone"));
        params.add(new BasicNameValuePair("account",account));

        JSONObject jsn = jParser.getJSONFromUrl("http://www.chiens.idv.tw/lccnet3h/Rex/index.php",params);
        Log.e("jsn",jsn+"");
        return  jsn;
    }
}// class