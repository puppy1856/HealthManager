/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 * */
package library;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;

public class UserFunctions {
	
	private JSONParser jsonParser;
	
	private static String loginURL = "http://www.chiens.idv.tw/lccnet3h/Rex/";
	private static String registerURL = "http://www.chiens.idv.tw/lccnet3h/Rex/";
	
	private static String login_tag = "login";
	private static String register_tag = "register";
	
	// constructor
	public UserFunctions(){
		jsonParser = new JSONParser();
	}
	
	/**
	 * function make Login Request
	 * @param account
	 * @param password
	 * */
	public JSONObject loginUser(String account, String password){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", login_tag));
		params.add(new BasicNameValuePair("account", account));
		params.add(new BasicNameValuePair("password", password));
		JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
		// return json
		// Log.e("JSON", json.toString());
		return json;
	}
	
	/**
	 * function make Login Request
	 * @param name
	 * @param email
	 * @param password
	 * @param account
	 * */
	public JSONObject registerUser(String name, String email, String password, String account){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", register_tag));
		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("account", account));
		
		// getting JSON Object
		JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
		// return json
		return json;
	}
	
	/**
	 * Function get Login status
	 * */
	public boolean isUserLoggedIn(Context context){
		DatabaseHandler db = new DatabaseHandler(context);
		int count = db.getRowCount();
		if(count > 0){
			// user logged in
			return true;
		}
		return false;
	}
	
	/**
	 * Function to logout user
	 * Reset Database
	 * */
	public boolean logoutUser(Context context){
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetTables();
		return true;
	}


	private static String URL = "http://www.chiens.idv.tw/lccnet3h/Rex/";

	public JSONObject getdata(String account, String uid){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "getDateData"));
		params.add(new BasicNameValuePair("account",account));
		params.add(new BasicNameValuePair("uid",uid));
		JSONObject json = jsonParser.getJSONFromUrl(URL, params);
		return json;

	}
	public JSONObject getdatarank()
	{
		JSONParser jParser = new JSONParser();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "getrecord"));
		JSONObject json = jParser.getJSONFromUrl("http://www.chiens.idv.tw/lccnet3h/Rex/index.php", params);
		return json;
	}
	public JSONObject getmytotal(String account)
	{
		JSONParser jParser = new JSONParser();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", "getuserdata"));
		params.add(new BasicNameValuePair("account",account));
		JSONObject json = jParser.getJSONFromUrl("http://www.chiens.idv.tw/lccnet3h/Rex/index.php", params);
		return json;
	}
	
}
