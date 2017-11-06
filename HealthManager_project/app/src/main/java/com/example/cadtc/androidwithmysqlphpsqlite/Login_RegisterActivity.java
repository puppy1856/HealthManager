/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 * */
package com.example.cadtc.androidwithmysqlphpsqlite;

import org.json.JSONException;
import org.json.JSONObject;

import library.DatabaseHandler;
import library.UserFunctions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login_RegisterActivity extends Activity {
	Button btnRegister;
	Button btnLinkToLogin;
	EditText inputFullName;
	EditText inputEmail;
	EditText inputPassword;
	EditText inputAccount;
	TextView registerErrorMsg;
	
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
		setContentView(R.layout.register);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		login_MySqlite = new Login_MySqlite(Login_RegisterActivity.this);

		// Importing all assets like buttons, text fields
		inputFullName = (EditText) findViewById(R.id.registerName);
		inputEmail = (EditText) findViewById(R.id.registerEmail);
		inputPassword = (EditText) findViewById(R.id.registerPassword);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
		inputAccount = (EditText)findViewById(R.id.registerAccount);
		registerErrorMsg = (TextView) findViewById(R.id.register_error);

		inputFullName.setTypeface(Typeface.createFromAsset(getAssets()
				, "fonts/Rye-Regular.ttf"));
		inputEmail.setTypeface(Typeface.createFromAsset(getAssets()
				, "fonts/Rye-Regular.ttf"));
		inputPassword.setTypeface(Typeface.createFromAsset(getAssets()
				, "fonts/Rye-Regular.ttf"));
		inputAccount.setTypeface(Typeface.createFromAsset(getAssets()
				, "fonts/Rye-Regular.ttf"));
		registerErrorMsg.setTypeface(Typeface.createFromAsset(getAssets()
				, "fonts/Rye-Regular.ttf"));
		
		// Register Button Click event
		btnRegister.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View view) {
				String name = inputFullName.getText().toString();
				String email = inputEmail.getText().toString();
				String password = inputPassword.getText().toString();
				String account = inputAccount.getText().toString();
				UserFunctions userFunction = new UserFunctions();
				JSONObject json;
				if(account.trim().equals("")){
					registerErrorMsg.setText("Error occured in registration : \nNeed to enter your account(16digits)");
				}
				else{
					if(name.trim().equals("")){
						registerErrorMsg.setText("Error occured in registration : \nNeed to enter your name(16digits)");
					}
					else{
						if(password.trim().equals("")){
							registerErrorMsg.setText("Error occured in registration : \nNeed to enter your password(16digits)");
						}
						else{
							json = userFunction.registerUser(name, email, password, account);
							try {
								if (json.getString(KEY_SUCCESS) != null) {
									registerErrorMsg.setText("");
									String res = json.getString(KEY_SUCCESS);
									if(Integer.parseInt(res) == 1){
										// user successfully registred
										// Store user details in SQLite Database
										DatabaseHandler db = new DatabaseHandler(getApplicationContext());
										JSONObject json_user = json.getJSONObject("user");

										// Clear all previous data in database
										userFunction.logoutUser(getApplicationContext());
										db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));

										//防止完全沒資料直接登入的BUG
										if (login_MySqlite.getData().size() == 0)
											login_MySqlite.putData(account, "0", "0");
										else
											login_MySqlite.updateData(account);

										// Launch Dashboard Screen
										Intent dashboard = new Intent(getApplicationContext(), Login_DashboardActivity.class);
										// Close all views before launching Dashboard
										dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(dashboard);
										// Close Registration Screen
										finish();
									}else{
										// Error in registration
										registerErrorMsg.setText("Error occured in registration");
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}

				// check for login response
			}
		});

		// Link to Login Screen
		btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),
						Login_Activity.class);
				startActivity(i);
				// Close Registration View
				finish();
			}
		});
	}

	@Override
	public void onBackPressed() {
		Intent login = new Intent(getApplicationContext(), Login_Activity.class);
		startActivity(login);
		return;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		Intent t = new Intent(Login_RegisterActivity.this,Login_Activity.class);
		startActivity(t);
		finish();
		return false;
	}
}
