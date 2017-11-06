package com.example.student.coachmanager;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserInput extends AppCompatActivity
{
    TextView tv;
    EditText edtv;
    String account;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_input);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); //強制開啟主程式可以下載東西*
        StrictMode.setThreadPolicy(policy);

        tv = (TextView)findViewById(R.id.tv);
        edtv = (EditText)findViewById(R.id.edtv);
    }
    public void click(View V)
    {
        new Internet(UserInput.this).CheckInternetDialog();
        boolean isHaveInternet = new Internet(UserInput.this).IsHaveInternet(UserInput.this);

        if (isHaveInternet)
        {
            String message;
            account = edtv.getText() + "";

            JSONObject msg = checkAccount();
            try
            {
                message = msg.getString("msg");
            } catch (Exception e)
            {
                message = "";
            }

            if (message.equals("fail"))
            {
                tv.setText("找不到使用者,請確定使用者帳號!");
            } else
            {
                Intent t = new Intent(UserInput.this, CoachManager.class);
                t.putExtra("account", account);
                startActivity(t);
            }
        }
    }

    public JSONObject checkAccount()
    {
        JSONParser jParser = new JSONParser();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag","checkaccount"));
        params.add(new BasicNameValuePair("account",account));

        JSONObject jsn = jParser.getJSONFromUrl("http://www.chiens.idv.tw/lccnet3h/Rex/index.php",params);
        return  jsn;
    }
}
