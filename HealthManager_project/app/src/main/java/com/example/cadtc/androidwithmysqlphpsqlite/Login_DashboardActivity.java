package com.example.cadtc.androidwithmysqlphpsqlite;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import library.UserFunctions;

public class Login_DashboardActivity extends Activity {
    UserFunctions userFunctions;
    Button btnLogout,btnFood,btnSport,btnFont,btnSetting;
    TextView accounttv;
    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);

        /**
         * Dashboard Screen for the application
         * */
        // Check login status in database
        userFunctions = new UserFunctions();
        if(userFunctions.isUserLoggedIn(getApplicationContext())){
            setContentView(R.layout.dashboard);
            btnLogout = (Button) findViewById(R.id.btnLogout);
            btnFood = (Button) findViewById(R.id.FoodBtn);
            btnSport = (Button) findViewById(R.id.SportBtn);
            btnFont = (Button) findViewById(R.id.FontBtn);
            btnSetting = (Button) findViewById(R.id.SettingBtn);

            accounttv = (TextView)findViewById(R.id.accounttv);

            btnLogout.setOnClickListener(onClickListener);
            btnFood.setOnClickListener(onClickListener);
            btnSport.setOnClickListener(onClickListener);
            btnFont.setOnClickListener(onClickListener);
            btnSetting.setOnClickListener(onClickListener);

            String account = new Login_MySqlite(Login_DashboardActivity.this).getData().get("account");
            accounttv.setText(account);

        }else{
            // user is not logged in show login screen
            Intent login = new Intent(getApplicationContext(), Login_Activity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(login);
            // Closing dashboard screen
            finish();
        }




    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        AlertDialog.Builder exitDialog=new AlertDialog.Builder(Login_DashboardActivity.this);
        exitDialog.setTitle("Exit");
        exitDialog.setMessage("確定要退出嗎?");
        exitDialog.setNegativeButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        exitDialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        exitDialog.show();

        return false;
    }

    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int select = view.getId();

            if (select == R.id.btnLogout) {
                AlertDialog.Builder exitDialog=new AlertDialog.Builder(Login_DashboardActivity.this);
                exitDialog.setTitle("");
                exitDialog.setMessage("確定要登出嗎?");
                exitDialog.setNegativeButton("登出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userFunctions.logoutUser(getApplicationContext());
                        Intent login = new Intent(getApplicationContext(), Login_Activity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(login);
                        // Closing dashboard screen
                        finish();
                    }
                });

                exitDialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                exitDialog.show();
            }
            else {
                new Internet(Login_DashboardActivity.this).CheckInternetDialog();
                boolean isHaveInternet = new Internet(Login_DashboardActivity.this).IsHaveInternet(Login_DashboardActivity.this);

                if (isHaveInternet) {
                    if (select == R.id.FoodBtn) {
                        Intent foodManagemt = new Intent(getApplicationContext(), FoodManagment_Main.class);
                        startActivity(foodManagemt);
                    } else if (select == R.id.SportBtn) {
                        String checkPlan;
                        Sport_MySqlite DB;
                        HashMap<String, String> data;

                        DB = new Sport_MySqlite(Login_DashboardActivity.this);
                        data = DB.getData();

                        checkPlan =data.get("checkPlan");
                        if (checkPlan == null) {
                            new AlertDialog.Builder(Login_DashboardActivity.this)
                                    .setTitle("沒有找到計畫")
                                    .setMessage("是否要新增?")
                                    .setNegativeButton("是", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent i = new Intent(getApplicationContext(), Sport_CreatePlan.class);
                                            startActivity(i);
                                        }
                                    })
                                    .setPositiveButton("否", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).show();
                        } else {
                            Intent i = new Intent(getApplicationContext(), Sport_MainManager.class);
                            startActivity(i);
                        }
                    } else if (select == R.id.FontBtn) {
                        Intent fontManagemt = new Intent(getApplicationContext(), CalendarMain.class);
                        startActivity(fontManagemt);
                    } else if (select == R.id.SettingBtn) {
                        Intent settingManagemt = new Intent(getApplicationContext(), AchievementMain.class);
                        startActivity(settingManagemt);
                    }
                }
            }
        }
    };
}