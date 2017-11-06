package com.example.student.coachmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Student on 2017/10/16.
 */

public class CoachManager extends AppCompatActivity
{
    TextView accounttv,foodtv,sporttv;
    ImageButton sportbtn,foodbtn;

    String account;
    int foodisDone,sportisDone;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coachmanager_layout);
        accounttv = (TextView)findViewById(R.id.accounttv);
        foodtv = (TextView)findViewById(R.id.foodtv);
        sporttv = (TextView)findViewById(R.id.sporttv);
        sportbtn = (ImageButton)findViewById(R.id.sportbtn);
        foodbtn = (ImageButton)findViewById(R.id.foodbtn);

        Intent t = this.getIntent();
        account = t.getStringExtra("account");
        Log.v("account",account+"");

        accounttv.setText(account);

        update();
    }

    public void foodclick(View V)
    {
        new Internet(CoachManager.this).CheckInternetDialog();
        boolean isHaveInternet = new Internet(CoachManager.this).IsHaveInternet(CoachManager.this);

        if (isHaveInternet)
        {
            new AlertDialog.Builder(CoachManager.this)
                    .setMessage("確定今天的目標已經完成?")
                    .setPositiveButton("是", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            updateisDone("foodisDone");
                            //foodsusupdate();
                            foodtv.setText("使用者已完成!");
                            foodbtn.setVisibility(View.INVISIBLE);
                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                        }
                    }).show();
        }
    }

    public void sportclick(View V)
    {
        new Internet(CoachManager.this).CheckInternetDialog();
        boolean isHaveInternet = new Internet(CoachManager.this).IsHaveInternet(CoachManager.this);

        if (isHaveInternet)
        {
            new AlertDialog.Builder(CoachManager.this)
                    .setMessage("確定今天的目標已經完成?")
                    .setPositiveButton("是", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            updateisDone("sportisDone");
                            //sportsusupdate();
                            sporttv.setText("使用者已完成!");
                            sportbtn.setVisibility(View.INVISIBLE);

                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                        }
                    }).show();
        }
    }

    public void reupdate(View V)
    {
        new Internet(CoachManager.this).CheckInternetDialog();
        boolean isHaveInternet = new Internet(CoachManager.this).IsHaveInternet(CoachManager.this);

        if (isHaveInternet)
        {
            update();
            Toast.makeText(CoachManager.this, "更新狀態", Toast.LENGTH_SHORT).show();
        }
    }

    public JSONObject getisDone()
    {
        JSONParser jParser = new JSONParser();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag","getisDone"));
        params.add(new BasicNameValuePair("account",account));

        JSONObject jsn = jParser.getJSONFromUrl("http://www.chiens.idv.tw/lccnet3h/Rex/index.php",params);
        return  jsn;
    }

    public void updateisDone(String manager)
    {
        JSONParser jParser = new JSONParser();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag","updateisDone"));
        params.add(new BasicNameValuePair("account",account));
        params.add(new BasicNameValuePair("manager",manager));
        params.add(new BasicNameValuePair("isDone","1"));

        jParser.getJSONFromUrl("http://www.chiens.idv.tw/lccnet3h/Rex/index.php",params);
    }

    public void update() //isDone值 -> 0:沒有計畫 1:計畫已完成 2:計畫未完成
    {
        JSONObject isDone = getisDone();
        Log.v("isDone",isDone+"");
        try
        {
            foodisDone = isDone.getInt("foodisDone");
            sportisDone = isDone.getInt("sportisDone");
        }
        catch (Exception e){}
        //食物計畫判定
        if(foodisDone == 0)
        {
            foodtv.setText("沒有計畫!");
            foodbtn.setVisibility(View.INVISIBLE);
        }
        else if(foodisDone == 1)
        {
            foodtv.setText("使用者已完成!");
            foodbtn.setVisibility(View.INVISIBLE);
        }
        else if(foodisDone == 2)
        {
            foodtv.setText("使用者尚未完成!");
            foodbtn.setVisibility(View.VISIBLE);
        }
        else
        {
            foodtv.setText("資料接收錯誤!");
            foodbtn.setVisibility(View.INVISIBLE);
        }

        //運動計畫判定
        if(sportisDone == 0)
        {
            sporttv.setText("沒有計畫!");
            sportbtn.setVisibility(View.INVISIBLE);
        }
        else if(sportisDone == 1)
        {
            sporttv.setText("使用者已完成!");
            sportbtn.setVisibility(View.INVISIBLE);
        }
        else if(sportisDone == 2)
        {
            sporttv.setText("使用者尚未完成!");
            sportbtn.setVisibility(View.VISIBLE);
        }
        else
        {
            sporttv.setText("資料接收錯誤!");
            sportbtn.setVisibility(View.INVISIBLE);
        }
    }
    /*
    public void sportsusupdate()
    {
        SimpleDateFormat DF = new SimpleDateFormat("YYYYMMdd", Locale.TAIWAN); //日期格式
        Date date = new Date();
        Calendar cal= Calendar.getInstance();
        cal.setTime(date); //將現在時間接收至nowDate字串
        cal.add(Calendar.DAY_OF_MONTH, 0);
        Date nowdate = cal.getTime();
        String dataDate = DF.format(nowdate);

        JSONParser jParser = new JSONParser();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag","update"));
        params.add(new BasicNameValuePair("account",account));
        params.add(new BasicNameValuePair("date",dataDate));
        params.add(new BasicNameValuePair("plan","sport"));
        params.add(new BasicNameValuePair("isdone","2"));

        jParser.getJSONFromUrl("http://www.chiens.idv.tw/lccnet3h/Rex/index.php",params);
    }
    */
    /*
    public void foodsusupdate()
    {
        SimpleDateFormat DF = new SimpleDateFormat("YYYYMMdd", Locale.TAIWAN); //日期格式
        Date date = new Date();
        Calendar cal= Calendar.getInstance();
        cal.setTime(date); //將現在時間接收至nowDate字串
        cal.add(Calendar.DAY_OF_MONTH, 0);
        Date nowdate = cal.getTime();
        String dataDate = DF.format(nowdate);

        JSONParser jParser = new JSONParser();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag","update"));
        params.add(new BasicNameValuePair("account",account));
        params.add(new BasicNameValuePair("date",dataDate));
        params.add(new BasicNameValuePair("plan","food"));
        params.add(new BasicNameValuePair("isdone","2"));

        jParser.getJSONFromUrl("http://www.chiens.idv.tw/lccnet3h/Rex/index.php",params);
    }
    */
}
