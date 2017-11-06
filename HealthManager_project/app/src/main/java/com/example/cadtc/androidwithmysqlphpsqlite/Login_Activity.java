/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 * */
package com.example.cadtc.androidwithmysqlphpsqlite;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import library.DatabaseHandler;
import library.UserFunctions;

public class Login_Activity extends Activity {
	Button btnLogin;
	Button btnLinkToRegister;
	EditText inputAccount;
	EditText inputPassword;
	TextView loginErrorMsg;

	// JSON Response node names
	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	private static String KEY_ERROR_MSG = "error_msg";
	private static String KEY_UID = "uid";
	private static String KEY_NAME = "name";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";

	Login_MySqlite login_MySqlite;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		login_MySqlite = new Login_MySqlite(Login_Activity.this);

		// Importing all assets like buttons, text fields
		inputAccount = (EditText) findViewById(R.id.loginAccount);
		inputPassword = (EditText) findViewById(R.id.loginPassword);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
		loginErrorMsg = (TextView) findViewById(R.id.login_error);

		//更換字型 需先在/app/src/main/創立一個資料夾assets/fonts
		inputAccount.setTypeface(Typeface.createFromAsset(getAssets()
				, "fonts/Rye-Regular.ttf"));
		inputPassword.setTypeface(Typeface.createFromAsset(getAssets()
				, "fonts/Rye-Regular.ttf"));
		loginErrorMsg.setTypeface(Typeface.createFromAsset(getAssets()
				, "fonts/Rye-Regular.ttf"));

		// Login button Click Event
		btnLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {


				String account = inputAccount.getText().toString();
				String password = inputPassword.getText().toString();
				UserFunctions userFunction = new UserFunctions();
				Log.d("Button", "Login");
				JSONObject json = userFunction.loginUser(account, password);

				// check for login response
				try {
					if (json.getString(KEY_SUCCESS) != null) {
						loginErrorMsg.setText("");
						String res = json.getString(KEY_SUCCESS); 
						if(Integer.parseInt(res) == 1){
							// user successfully logged in
							// Store user details in SQLite Database
							DatabaseHandler db = new DatabaseHandler(getApplicationContext());
							JSONObject json_user = json.getJSONObject("user");
							
							// Clear all previous data in database
							userFunction.logoutUser(getApplicationContext());
							db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));
							
							// Launch Dashboard Screen
							Intent dashboard = new Intent(getApplicationContext(), Login_DashboardActivity.class);
							
							// Close all views before launching Dashboard
							dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(dashboard);

							//防止sqlite連欄位都沒有的情況
							try {
								login_MySqlite.getData();
							}catch (Exception e) {
								login_MySqlite.onCreate(login_MySqlite.getWritableDatabase());
							}

							//判斷是否第一次登入
							if (login_MySqlite.getData().size() == 0)
								login_MySqlite.putData(account, "0", "0");
							else
								login_MySqlite.updateData(account);

							// Close Login Screen
							finish();
						}else{
							// Error in login
							loginErrorMsg.setText("Incorrect username/password");
						}
					}
				} catch (Exception e) {
					new Internet(Login_Activity.this).CheckInternetDialog();
					e.printStackTrace();
				}
			}
		});

		// Link to Register Screen
		btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),
						Login_RegisterActivity.class);
				startActivity(i);
				finish();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		AlertDialog.Builder exitDialog=new AlertDialog.Builder(Login_Activity.this);
		exitDialog.setTitle("Exit");
		exitDialog.setMessage("確定要退出嗎?");
		exitDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});

		exitDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		exitDialog.show();

		return false;
	}
}
