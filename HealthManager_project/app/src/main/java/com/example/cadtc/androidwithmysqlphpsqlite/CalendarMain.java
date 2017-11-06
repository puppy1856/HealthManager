package com.example.cadtc.androidwithmysqlphpsqlite;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TabHost;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import library.UserFunctions;


public class CalendarMain extends ListActivity {

    public Calendar month,month2;
    public CalendarAdapter Cadapter,Cadapter2;
    ArrayAdapter Aadapter,Aadapter2;
    public Handler handler,handler2;
    public ArrayList<String> items,items2;  //花朵辨識存放日期用
    ArrayList<String> dates=new ArrayList<>(); //花朵判斷日期用
    ArrayList<String> getdates=new ArrayList<>(); //資料庫抓日期用
    ArrayList<String> getfood=new ArrayList<>(); //資料庫抓飲食參數用
    ArrayList<String> getsport=new ArrayList<>(); //資料庫抓運動參數用
    int count=1; //計算資料庫總筆數用

    //-----------------------------------------------
    String account;   //帳號名稱
    //-----------------------------------------------

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainlayout);
        //--------嚴格模式
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        account = new Login_MySqlite(CalendarMain.this).getData().get("account");

        //--------Listview排行榜建立
        setListAdapter(new ImageAdapter(this));
        UserFunctions userFunctions = new UserFunctions();
        JSONObject rt = userFunctions.getmytotal(account);
        try
        {
            TextView myscore =(TextView)findViewById(R.id.myscore);
            myscore.setText(account+":"+rt.getString("total_flower"));
        }
        catch(Exception e){}

        try {
            UserFunctions userFunction = new UserFunctions();
            while (true){
                JSONObject json = userFunction.getdata(account, Integer.toString(count));

                if(json.getString("date").equals("null"))
                    break; //若為空值離開迴圈  , 總筆數為(count-1)

                String date = json.getString("date");
                getdates.add(date);
                String food = json.getString("food");
                getfood.add(food);
                String sport = json.getString("sport");
                getsport.add(sport);
                count++;
            }

        }catch (JSONException e) {e.printStackTrace();}
        for(int i=0;i<count-1;i++)
            dates.add(getdates.get(i));


        //TAB使用
        TabHost tabs = (TabHost) findViewById(R.id.tabhost);
        tabs.setup();
        TabHost.TabSpec spec;

        spec = tabs.newTabSpec("tag1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("飲食");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tag2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("運動");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tag3");
        spec.setContent(R.id.tab3);
        spec.setIndicator("排行榜");
        tabs.addTab(spec);

        tabs.setCurrentTab(0);
        //------------------------------------------

        //--------------------飲食----------------------------------------------------

        month = Calendar.getInstance();  //取得今天日期

        items = new ArrayList<String>();
        Cadapter = new CalendarAdapter(this, month);


        //          日曆
        GridView gridview = (GridView) findViewById(R.id.gridview1);
        gridview.setAdapter(Cadapter);

        //          星期
        String[] s=getResources().getStringArray(R.array.week);
        GridView gridViewweek=(GridView)findViewById(R.id.gridviewweek1);
        Aadapter=new ArrayAdapter(this,R.layout.week,s);
        gridViewweek.setAdapter(Aadapter);


        handler = new Handler();
        refreshCalendar1(month);
        TextView title  = (TextView) findViewById(R.id.title1);
        title.setText(android.text.format.DateFormat.format("yyyy MMMM", month));


        //    上一頁
        TextView previous  = (TextView) findViewById(R.id.previous1);
        previous.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(month.get(Calendar.MONTH)== month.getActualMinimum(Calendar.MONTH)) {
                    month.set((month.get(Calendar.YEAR)-1),month.getActualMaximum(Calendar.MONTH),1);
                } else {
                    month.set(Calendar.MONTH,month.get(Calendar.MONTH)-1);
                }
                refreshCalendar1(month);
            }
        });
        //      下一頁
        TextView next  = (TextView) findViewById(R.id.next1);
        next.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(month.get(Calendar.MONTH)== month.getActualMaximum(Calendar.MONTH)) {
                    month.set((month.get(Calendar.YEAR)+1),month.getActualMinimum(Calendar.MONTH),1);
                } else {
                    month.set(Calendar.MONTH,month.get(Calendar.MONTH)+1);
                }
                refreshCalendar1(month);

            }
        });

        //-----------------------------------運動--------------------------------


        month2 = Calendar.getInstance();  //取得今天日期
        items2 = new ArrayList<String>();
        Cadapter2 = new CalendarAdapter(this, month2);


        //          日曆
        GridView gridview2 = (GridView) findViewById(R.id.gridview2);
        gridview2.setAdapter(Cadapter2);

        //          星期
        String[] s2=getResources().getStringArray(R.array.week);
        GridView gridViewweek2=(GridView)findViewById(R.id.gridviewweek2);
        Aadapter2=new ArrayAdapter(this,R.layout.week,s2);
        gridViewweek2.setAdapter(Aadapter2);
        //------------------------------------------------------------------------------------------------
        handler2 = new Handler();
        refreshCalendar2(month2);
        TextView title2  = (TextView) findViewById(R.id.title2);
        title2.setText(android.text.format.DateFormat.format("yyyy MMMM", month2));


        //    上一頁
        TextView previous2  = (TextView) findViewById(R.id.previous2);
        previous2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(month2.get(Calendar.MONTH)== month2.getActualMinimum(Calendar.MONTH)) {
                    month2.set((month2.get(Calendar.YEAR)-1),month2.getActualMaximum(Calendar.MONTH),1);
                } else {
                    month2.set(Calendar.MONTH,month2.get(Calendar.MONTH)-1);
                }
                refreshCalendar2(month2);
            }
        });
        //      下一頁
        TextView next2  = (TextView) findViewById(R.id.next2);
        next2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(month2.get(Calendar.MONTH)== month2.getActualMaximum(Calendar.MONTH)) {
                    month2.set((month2.get(Calendar.YEAR)+1),month2.getActualMinimum(Calendar.MONTH),1);
                } else {
                    month2.set(Calendar.MONTH,month2.get(Calendar.MONTH)+1);
                }
                refreshCalendar2(month2);

            }
        });


    } //onCreate

    public void refreshCalendar1(Calendar month)  //上下頁更新日曆(飲食)
    {
        TextView title  = (TextView) findViewById(R.id.title1);
        Cadapter.refreshDays();
        Cadapter.notifyDataSetChanged();
//----------------------------------------------------------------
        handler.post(calendarUpdater1); // 何日顯示圖片
//----------------------------------------------------------------
        title.setText(android.text.format.DateFormat.format("yyyy MMMM", month));
    }



    public Runnable calendarUpdater1 = new Runnable() {          //指定花的位置(飲食)
        @Override
        public void run() {
            items.clear();
// --------------------- 指定顯示的日--------------------------------------------------------------------
            for(int x=0;x<count-1;x++) {
                if (getfood.get(x).equals("2")) { //成功的花
                    int yea = Integer.parseInt(dates.get(x).substring(0, 4));  //年
                    int mon = Integer.parseInt(dates.get(x).substring(4, 6));  //月
                    int day = Integer.parseInt(dates.get(x).substring(6, 8));  //日
                    if (yea == month.get(Calendar.YEAR) + 0)
                        if (mon == month.get(Calendar.MONTH) + 1) {
                            items.add("y" + Integer.toString(day));
                        }

                }
                else if(getfood.get(x).equals("1")) {
                    int yea = Integer.parseInt(dates.get(x).substring(0, 4));  //年
                    int mon = Integer.parseInt(dates.get(x).substring(4, 6));  //月
                    int day = Integer.parseInt(dates.get(x).substring(6, 8));  //日
                    if (yea == month.get(Calendar.YEAR) + 0)
                        if (mon == month.get(Calendar.MONTH) + 1)
                            items.add("n"+ Integer.toString(day));
                }
            }
//-------------------------------------------------------------------------------------------------------------
            Cadapter.setItems(items);
            Cadapter.notifyDataSetChanged();
        }
    };

    public void refreshCalendar2(Calendar month)  //上下頁更新日曆(運動)
    {
        TextView title  = (TextView) findViewById(R.id.title2);

        Cadapter2.refreshDays();
        Cadapter2.notifyDataSetChanged();
//----------------------------------------------------------------
        handler2.post(calendarUpdater2); // 何日顯示圖片
//----------------------------------------------------------------
        title.setText(android.text.format.DateFormat.format("yyyy MMMM", month));
    }



    public Runnable calendarUpdater2 = new Runnable() {     //指定花的位置(運動)
        @Override
        public void run() {
            items2.clear();
// --------------------- 指定顯示的日--------------------------------------------------------------------
            for(int x=0;x<count-1;x++) {
                if (getsport.get(x).equals("2")) {
                    int yea = Integer.parseInt(dates.get(x).substring(0, 4));  //年
                    int mon = Integer.parseInt(dates.get(x).substring(4, 6));  //月
                    int day = Integer.parseInt(dates.get(x).substring(6, 8));  //日
                    if (yea == month2.get(Calendar.YEAR) + 0)
                        if (mon == month2.get(Calendar.MONTH) + 1)
                            items2.add("y"+ Integer.toString(day));
                }
                else if (getsport.get(x).equals("1")) {
                    int yea = Integer.parseInt(dates.get(x).substring(0, 4));  //年
                    int mon = Integer.parseInt(dates.get(x).substring(4, 6));  //月
                    int day = Integer.parseInt(dates.get(x).substring(6, 8));  //日
                    if (yea == month2.get(Calendar.YEAR) + 0)
                        if (mon == month2.get(Calendar.MONTH) + 1)
                            items2.add("n"+ Integer.toString(day));
                }
            }
//-------------------------------------------------------------------------------------------------------------
            Cadapter2.setItems(items2);
            Cadapter2.notifyDataSetChanged();
        }
    };

}

