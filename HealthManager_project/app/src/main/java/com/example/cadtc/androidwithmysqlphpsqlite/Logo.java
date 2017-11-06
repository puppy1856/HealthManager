package com.example.cadtc.androidwithmysqlphpsqlite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

public class Logo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo_layout);


        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(1500);
                Intent login = new Intent(Logo.this, Login_DashboardActivity.class);
                startActivity(login);
                finish();
            }
        }).start();
    }
}
