package com.example.cadtc.androidwithmysqlphpsqlite;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.IdRes;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import library.JSONParser;

public class AchievementMain extends Activity {
    RadioGroup rg_flower,rg_pot;
    RadioButton flower1,flower2,flower3,pot1,pot2,pot3;
    TextView textView2,textView3,textView5,textView6;
    int count;
    //使用者帳號-------------------
    String account;
    //----------------------------------

    Login_MySqlite login_mySqlite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.achievement_layout);
        //--------嚴格模式
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        login_mySqlite = new Login_MySqlite(AchievementMain.this);
        account = login_mySqlite.getData().get("account");

        rg_flower=(RadioGroup)findViewById(R.id.radioGroupflower);
        rg_pot=(RadioGroup)findViewById(R.id.radioGrouppot);
        flower1=(RadioButton)findViewById(R.id.radioButton1);
        flower2=(RadioButton)findViewById(R.id.radioButton2);
        flower3=(RadioButton)findViewById(R.id.radioButton3);
        pot1=(RadioButton)findViewById(R.id.radioButton4);
        pot2=(RadioButton)findViewById(R.id.radioButton5);
        pot3=(RadioButton)findViewById(R.id.radioButton6);
        textView2=(TextView)findViewById(R.id.textView2);
        textView3=(TextView)findViewById(R.id.textView3);
        textView5=(TextView)findViewById(R.id.textView5);
        textView6=(TextView)findViewById(R.id.textView6);

        flower2.setEnabled(false);
        flower3.setEnabled(false);
        pot2.setEnabled(false);
        pot3.setEnabled(false);

        JSONObject rt=getflower_code(account);
        try {
            count=Integer.parseInt(rt.getString("flower_code"));  //接收解鎖碼
        }
        catch(Exception e){}
        switch (count){
            case 4:
                flower3.setEnabled(true);
                textView3.setText("已解鎖");
            case 3:
                pot3.setEnabled(true);
                textView6.setText("已解鎖");
            case 2:
                flower2.setEnabled(true);
                textView2.setText("已解鎖");
            case 1:
                pot2.setEnabled(true);
                textView5.setText("已解鎖");
            case 0:
                break;
        }

        int flowerSetting = Integer.parseInt(login_mySqlite.getData().get("flowerSetting"));
        int potSetting = Integer.parseInt(login_mySqlite.getData().get("potSetting"));

        if (flowerSetting == 0)
            flower1.setChecked(true);
        else if (flowerSetting == 1)
            flower2.setChecked(true);
        else if (flowerSetting == 2)
            flower3.setChecked(true);

        if (potSetting == 0)
            pot1.setChecked(true);
        else if (potSetting == 1)
            pot2.setChecked(true);
        else if (potSetting == 2)
            pot3.setChecked(true);


        rg_flower.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch(i){
                    case R.id.radioButton1:
                        login_mySqlite.updateFlowerSetting("0");
                        break;
                    case R.id.radioButton2:
                        login_mySqlite.updateFlowerSetting("1");
                        break;
                    case R.id.radioButton3:
                        login_mySqlite.updateFlowerSetting("2");
                        break;

                }
            }
        });

        rg_pot.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch(i){
                    case R.id.radioButton4:
                        login_mySqlite.updatePotSetting("0");
                        break;
                    case R.id.radioButton5:
                        login_mySqlite.updatePotSetting("1");
                        break;
                    case R.id.radioButton6:
                        login_mySqlite.updatePotSetting("2");
                        break;
                }
            }
        });

    }

    public JSONObject getflower_code(String account)
    {
        JSONParser jParser = new JSONParser();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "getuserdata"));
        params.add(new BasicNameValuePair("account",account));
        JSONObject json = jParser.getJSONFromUrl("http://www.chiens.idv.tw/lccnet3h/Rex/index.php", params);
        return json;
    }
}
